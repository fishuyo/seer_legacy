package com.fishuyo.seer
package parsers

import maths._
import spatial._
import graphics._

import scala.io.Source
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.combinator.JavaTokenParsers
import scala.util.parsing.combinator.syntactical.StandardTokenParsers
import scala.util.parsing.combinator.lexical._
import scala.collection.mutable.ListBuffer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics._

abstract trait Statement
case class Var(k:String,v:Float) extends Statement
case class Set(k:String,v:String) extends Statement
case class Rule(name:String,actions:List[Statement]) extends Statement
case class Action(loop:List[Statement], trans:List[Statement], prim:Primitive) extends Statement
case class TransLoop(times:Int,trans:List[Statement]) extends Statement
case class TransLoopV(times:String,trans:List[Statement]) extends Statement
case class Transform(p:Pose,s:Vec3) extends Statement
case class TransformV(param:String,value:String) extends Statement
case class Comment extends Statement

abstract trait Primitive extends Statement
case class Draw(d:Drawable) extends Primitive
case class RuleCall(ident:String) extends Primitive
case class Dont() extends Primitive

class MyLexer extends StdLexical {
  override def digit = ( super.digit | hexDigit )
  lazy val hexDigits = Array('0','1','2','3','4','5','6','7','8','9','.','-')
  lazy val hexDigit = elem("hex digit", hexDigits.contains(_))
}

object EisenScriptParser extends StandardTokenParsers {

	override val lexical:MyLexer = new MyLexer()
 
  lexical.delimiters ++= List("{","}","(", ")",",","*","=")
 	lexical.reserved += ("var","set","rule","x","y","z","rx","ry","rz","s",
 											"hue","h","sat","brightness","b","alpha","a","color",
 											"box","sphere","grid")
 

//  program = { set | rule } ;
// rule = 'RULE' ,  rule_name , [ weight ] , '{' , { set | action } , '}' ;
// action = { transformationloop } [transformationlist] rule_ref ;
// transformationloop = number , '*' , transformationlist ;
// transformationlist = '{' , { transformation } , '}';
// transformation = 'X' , number |                      // translations
//                  'Y' , number |
//                  'Z' , number |
//                  'RX', number |						 // rotation about axis
//                  'RY', number |
//                  'RZ', number |
//                  'S', number |						 // resizing (all axis equal)
//                  'S', number number number, 		 // resizing for x,y,z individually
                 
// set = 'SET' , var_name , string ;
  // grammar starts here
  def program = stmt*
 
  def stmt = set | action | rule | variable | comment

  def comment = "//"~ ((ident | numericLit | stringLit )*) ~ "\n" ^^^ Comment()

  def variable = "var" ~ ident ~ "=" ~ numericLit ^^ { case _~name~_~value => Var(name,value.toFloat) }
  def set = "set" ~ ident ~ numericLit ^^ { case _~k~v => Set(k,v) }
  def rule = "rule" ~ ident ~ "{" ~ rule_body ~ "}" ^^ { case _~name~_~list~_ => Rule(name,list) }
  def rule_body = (set | action | comment)*

  def action = (tloop*) ~ opt(tlist) ~ primitive ^^ { case loop~list~prim => Action(loop,list.getOrElse(List()),prim)}
  def tloop = (numericLit ~ "*" ~ tlist ^^ { case t~_~list => TransLoop(t.toInt, list)}
 						| ident ~ "*" ~ tlist ^^ { case t~_~list => TransLoopV(t, list)})
  def tlist = "{" ~ (transformation*) ~ "}" ^^ { case _~list~_ => list }

  def transformation = ("x"~numericLit ^^ { case _~v => Transform(Pose(Vec3(v.toFloat,0,0)),Vec3(1)) }
  										| "y"~numericLit ^^ { case _~v => Transform(Pose(Vec3(0,v.toFloat,0)),Vec3(1)) }
  										| "z"~numericLit ^^ { case _~v => Transform(Pose(Vec3(0,0,v.toFloat)),Vec3(1)) }
  										| "rx"~numericLit ^^ { case _~v => Transform(Pose(Vec3(),Quat().fromEuler((v.toFloat.toRadians,0.f,0.f))),Vec3(1)) }
  										| "ry"~numericLit ^^ { case _~v => Transform(Pose(Vec3(),Quat().fromEuler((0.f,v.toFloat.toRadians,0.f))),Vec3(1)) }
  										| "rz"~numericLit ^^ { case _~v => Transform(Pose(Vec3(),Quat().fromEuler((0.f,0.f,v.toFloat.toRadians))),Vec3(1)) }
  										| "s"~numericLit~numericLit~numericLit ^^ { case _~x~y~z => Transform(Pose(),Vec3(x.toFloat,y.toFloat,z.toFloat)) }
  										| "s"~numericLit ^^ { case _~v => Transform(Pose(),Vec3(v.toFloat)) }
  										| "x"~ident ^^ { case p~v => TransformV(p,v) }
  										| "y"~ident ^^ { case p~v => TransformV(p,v) }
  										| "z"~ident ^^ { case p~v => TransformV(p,v) }
  										| "rx"~ident ^^ { case p~v => TransformV(p,v) }
  										| "ry"~ident ^^ { case p~v => TransformV(p,v) }
  										| "rz"~ident ^^ { case p~v => TransformV(p,v) }
  										| "s"~ident ^^ { case p~v => TransformV(p,v) })

  def primitive = ( ident ^^ {case id => RuleCall(id)} | box | sphere | grid )
  def box = "box" ^^^ Draw(Cube())
  def grid = "grid" ^^^ Draw(Cube()) //TODO wireframe
  def sphere = "sphere" ^^^ Draw(Sphere())


  def apply( script: String ) = {

  	//val input = Source.fromFile("input.talk").getLines.reduceLeft[String](_ + '\n' + _)
  	val tokens = new lexical.Scanner(script)
 
  	val result = phrase(program)(tokens)

		result match {
		  case Success(tree, _) => new ModelInterpreter(tree)
		 
		  case e: NoSuccess => {
		    Console.err.println(e)
		    new Model()
		  }
		}

  }
}

class ModelInterpreter(val tree: List[Statement]) {
	type Config = Map[String,String]
	var env = Map[String,String]()
	var gst = Map[String,Float]()
	var initialActions = List[Action]()
	var rules = Map[String,(Config,List[Action])]()
	var ruleDepth = collection.mutable.Map[String,Int]()

	var model = new Model()
	var node = model

	var depth = 0
	env = env + ("maxdepth"->"500")

	this(tree)

	def apply(tree: List[Statement]):ModelInterpreter = {

		tree match {
			case Var(n,v) :: rest => {
				gst = gst + (n->v)
				this(rest)
			}

			case Set(k,v) :: rest => {
				env = env + (k->v)
				this(rest)
			}
			case (a @ Action(loop,list,call)) :: rest => {
				initialActions = initialActions :+ a
				this(rest)
			}
			case Rule(name,list) :: rest => {
				val rule = list.foldLeft((Map[String,String](),List[Action]()))( (r:(Config,List[Action]),s:Statement) => {
					s match {
						case Set(k,v) => (r._1 + (k->v), r._2) 
						case a:Action => (r._1, r._2 :+ a)
					}
				})
				rules = rules + (name -> rule)
				ruleDepth(name) = 0
				this(rest)
			}
			case _ => ()

		}
		this
	}

	def applyRule(name:String, n:Model){
		val rule = rules(name)
		val depth = ruleDepth(name)
		ruleDepth(name) = depth+1
		if( depth >= env("maxdepth").toInt ) return
		rule._2.foreach( applyAction(_).foreach(n.addChild(_)) )
		ruleDepth(name) = depth
	}

	def applyAction(a:Action) = {
		a match {
			case Action(loop,list,call) => {
				val (rp,rs) = mergeTransformList(list)
				var res = loop.foldRight(Vector[Model]())( (tl,m) => {
					var times = 0
					var list:List[Statement] = List()
					tl match {
						case TransLoop(t,ts) => { times = t; list = ts }
						case TransLoopV(t,ts) => { times = gst(t).toInt; list = ts }
					}
					val (p,s) = mergeTransformList(list)
					var pp = Pose(p)
					var ss = Vec3(s)
					var group = Vector[Model]()
					for( i <- (0 until times)){

						node = new Model{ pose = pp; scale = ss}
						m.foreach( node.addChild(_) )

						group = group :+ node
						pp = pp*p
						ss = ss*s
					}
					group
				})

				if( res.length == 0) res = Vector(new Model())
				res.foreach( _.getLeaves().foreach( (n) => {
				  n.pose = n.pose * rp
				  n.scale = n.scale * rs
				  applyPrimitive(call,n)
				}))

				res
			}
		}

	}
	def applyPrimitive(p:Primitive, n:Model){
		p match {
			case RuleCall(r) => applyRule(r,n)
			case Draw(d) => n.addPrimitive( d )
			case Dont() => ()
		}
	}
	def mergeTransformList(ts:List[Statement]) = {
		ts.foldLeft(Pose(),Vec3(1))( (m,t) => {
			var pose = Pose()
			var scale = Vec3(1)

			t match {
				case Transform(p,s) => { pose = p; scale = s } //(m._1*p, m._2*s)
				case TransformV(param,v) => {
					param match {
						case "x" => pose.pos.x = gst(v)
						case "y" => pose.pos.y = gst(v)
						case "z" => pose.pos.z = gst(v)
						case "rx" => pose.quat.fromEuler((gst(v).toRadians,0.f,0.f))
						case "ry" => pose.quat.fromEuler((0.f,gst(v).toRadians,0.f))
						case "rz" => pose.quat.fromEuler((0.f,0.f,gst(v).toRadians))
						case "s" => scale.set(gst(v))
					}
				}
			}
			(m._1*pose, m._2*scale)
		})
	}

	def set(s:String,v:Float) = gst = gst + (s->v)

	def buildModel() = {
		model = Model()
		initialActions.foreach( (a) => applyAction(a).foreach(model.addChild(_)) )
		model
	}


}
