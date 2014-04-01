package com.fishuyo.seer
package parsers

import maths._
import spatial._
import graphics._
import util._

import scala.io.Source
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.combinator.JavaTokenParsers
import scala.util.parsing.combinator.syntactical.StandardTokenParsers
import scala.util.parsing.combinator.lexical._
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Queue

// import com.badlogic.gdx.Gdx
// import com.badlogic.gdx.graphics._

abstract trait Statement

case class Comment extends Statement

case class Var(k:String,v:Float) extends Statement
case class Set(k:String,v:String) extends Statement

case class Rule(name:String,mods:List[Mod],actions:List[Statement]) extends Statement
abstract trait Mod extends Statement
case class MaxDepth(d:Int) extends Mod
case class Weight(w:Float) extends Mod

case class Action(loop:List[Statement], trans:List[Statement], prim:Primitive) extends Statement

case class TransLoop(times:Int,trans:List[Statement]) extends Statement
case class TransLoopV(times:String,trans:List[Statement]) extends Statement

case class Transform(p:Pose,s:Vec3) extends Statement
case class TransformV(param:String,value:String) extends Statement
case class ColorTransform(h:HSV) extends Statement
case class Color(c:RGBA) extends Statement
case class Alpha(a:Float) extends Statement

abstract trait Primitive extends Statement
case class Draw(m:Mesh) extends Primitive
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
 	lexical.reserved += ("var","set","rule","md","maxdepth","w","weight",
 											"x","y","z","rx","ry","rz","s",
 											"hue","h","sat","brightness","b","value","v","alpha","a","color",
 											"box","sphere","grid")
 
  // grammar starts here
  def program = stmt*
 
  def stmt = set | action | rule | variable | comment

  def comment = "//"~ ((ident | numericLit | stringLit )*) ~ "\n" ^^^ Comment()

  def variable = "var" ~ ident ~ "=" ~ numericLit ^^ { case _~name~_~value => Var(name,value.toFloat) }
  def set = ("set" ~ "maxdepth" ~ numericLit ^^ { case _~k~v => Set("maxdepth",v) }
  					|"set" ~ ident ~ numericLit ^^ { case _~k~v => Set(k,v) })
  
  def rule = "rule" ~ ident ~ opt(rule_mod*) ~ "{" ~ rule_body ~ "}" ^^ { case _~name~mods~_~list~_ => Rule(name,mods.getOrElse(List()),list) }
  def rule_mod = (("md"|"maxdepth") ~ numericLit ^^ { case _~v => MaxDepth(v.toInt)}
  							 |("w"|"weight") ~ numericLit ^^ { case _~v => Weight(v.toFloat)})
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
  										| ("h"|"hue")~numericLit ^^ { case _~v => ColorTransform(HSV(v.toFloat,1,1)) }
  										| ("sat")~numericLit ^^ { case _~v => ColorTransform(HSV(0,v.toFloat,1)) }
  										| ("b"|"brightness")~numericLit ^^ { case _~v => ColorTransform(HSV(0,1,v.toFloat)) }
  										| ("a"|"alpha")~numericLit ^^ { case _~v => Alpha(v.toFloat) }

  										| "x"~ident ^^ { case p~v => TransformV(p,v) }
  										| "y"~ident ^^ { case p~v => TransformV(p,v) }
  										| "z"~ident ^^ { case p~v => TransformV(p,v) }
  										| "rx"~ident ^^ { case p~v => TransformV(p,v) }
  										| "ry"~ident ^^ { case p~v => TransformV(p,v) }
  										| "rz"~ident ^^ { case p~v => TransformV(p,v) }
  										| "s"~ident ^^ { case p~v => TransformV(p,v) } )

  def primitive = ( rulecall | box | sphere | grid )

  def rulecall = ident ^^ {case id => RuleCall(id)}
  def box = "box" ^^^ Draw(Cube().mesh)
  def grid = "grid" ^^^ Draw(Cube().mesh) //TODO wireframe
  def sphere = "sphere" ^^^ Draw(Sphere().mesh)


  def apply( script: String ) = {

  	//val input = Source.fromFile("input.talk").getLines.reduceLeft[String](_ + '\n' + _)
  	val tokens = new lexical.Scanner(script)
 
  	val result = phrase(program)(tokens)

		result match {
		  case Success(tree, _) => new ModelInterpreter(tree)
		 
		  case e: NoSuccess => {
		    Console.err.println(e)
		    null
		  }
		}

  }
}


class ModelInterpreter(val tree: List[Statement]) {
	
	type SymTab = Map[String,String]
	type Rule = (SymTab,List[Action])

	var env = Map[String,String]()
	var gst = Map[String,Float]()

	var actionQueue = Queue[(Model,Action)]()
	var initialActions = List[Action]()
	var rules = Map[String,(ListBuffer[Rule],ListBuffer[Float])]()
	var ruleDepth = collection.mutable.Map[String,Int]()

	var model = new Model()
	var node = model

	var primCount = 0
	var depth = 0
	env = env + ("maxdepth"->"500")
	env = env + ("maxobjects"->"2000")

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

			case (a:Action) :: rest => {
				initialActions = initialActions :+ a
				this(rest)
			}

			case Rule(name,mods,list) :: rest => {
				val lst = Map[String,String]()
				var weight = 1.f

				mods.foreach{
					case Weight(w) => weight = w
					case MaxDepth(d) => ()
				}

				val rule = list.foldLeft((lst,List[Action]()))( (r:(SymTab,List[Action]),s:Statement) => {
					s match {
						case Set(k,v) => (r._1 + (k->v), r._2) 
						case a:Action => (r._1, r._2 :+ a)
					}
				})

				if(rules.isDefinedAt(name)){
					rules(name)._1 += rule
					rules(name)._2 += weight
				} else rules = rules + (name -> (ListBuffer(rule),ListBuffer(weight)))

				ruleDepth(name) = 0
				this(rest)
			}
			case _ => ()

		}
		this
	}


	def applyRule(name:String, n:Model){
		val rule = Random.decide(rules(name)._1, rules(name)._2)()
		val depth = ruleDepth(name)
		ruleDepth(name) = depth+1
		// if( depth >= env("maxdepth").toInt ) return
		// rule._2.foreach( applyAction(_).foreach(n.addChild(_)) )
		rule._2.foreach( (a) => actionQueue.enqueue((n,a)) )

		// ruleDepth(name) = depth
	}

	def applyAction(pair:(Model,Action)) = {
		pair match {
			case (mdl,Action(loop,list,call)) => {

				// merge transforms into single pose, scale, color
				val (rp,rs,hsv) = mergeTransformList(list)

				// aggregate nested transform loops (magic?)
				var res = loop.foldRight(Vector[Model]())( (tl,m) => {
					var times = 0
					var list:List[Statement] = List()
					tl match {
						case TransLoop(t,ts) => { times = t; list = ts }
						case TransLoopV(t,ts) => { times = gst(t).toInt; list = ts }
					}
					// merge loops transform
					val (p,s,h) = mergeTransformList(list)
					var pp = Pose(p) //copy
					var ss = Vec3(s) //copy
					var cc = HSV(h)
					var group = Vector[Model]()

					// each time through the loop make model and stack transform
					for( i <- (0 until times)){

						node = new Model{ pose = pp; scale = ss}
						node.colorTransform = cc
						m.foreach( node.addChild(_) ) // add existing models from outer loops as children?

						group = group :+ node
						pp = pp*p
						ss = ss*s
						cc = cc*h
					}
					group // return list of nodes
				})

				if( res.length == 0) res = Vector(new Model())
				res.foreach( _.getLeaves().foreach( (n) => {
				  n.pose = n.pose * rp
				  n.scale = n.scale * rs
				  n.colorTransform = n.colorTransform * hsv
				  applyPrimitive(call,n)
				}))

				res.foreach( mdl.addChild(_))
				res
			}
		}

	}
	def applyPrimitive(p:Primitive, n:Model){
		if( primCount > env("maxobjects").toInt ) return
		n.material = Material(model.material)
		p match {
			case RuleCall(r) => applyRule(r,n)
			case Draw(m) => n.mesh = m; /*n.addPrimitive( d )*/; primCount += 1
			case Dont() => ()
		}
	}
	def mergeTransformList(ts:List[Statement]) = {
		ts.foldLeft(Pose(),Vec3(1),HSV(0,1,1))( (m,t) => {
			var pose = Pose()
			var scale = Vec3(1)
			var hsv = HSV(0,1,1)

			t match {
				case Transform(p,s) => { pose = p; scale = s; } //(m._1*p, m._2*s)
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
				case ColorTransform(h) => hsv = h
			}

			(m._1*pose, m._2*scale, m._3*hsv)
		})
	}

	def set(s:String,v:Float) = gst = gst + (s->v)

	def buildModel(m:Model=Model()) = {
		model = m
		actionQueue = Queue[(Model,Action)]()
		// initialActions.foreach( (a) => applyAction(a).foreach(model.addChild(_)) )
		initialActions.foreach( (a) => actionQueue.enqueue((model,a)) )
		var iterations = 0
		while( !actionQueue.isEmpty && iterations < env("maxdepth").toInt ){
			val actions = actionQueue.toList
			actionQueue.clear

			actions.foreach( applyAction(_) )

			iterations += 1
		}
		model.applyColorTransform
	}


}
