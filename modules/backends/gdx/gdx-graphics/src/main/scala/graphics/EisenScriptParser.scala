// package seer
// package graphics

// import spatial._
// import util._

// import scala.io.Source
// import scala.util.parsing.combinator.RegexParsers
// import scala.util.parsing.combinator.JavaTokenParsers
// import scala.util.parsing.combinator.syntactical.StandardTokenParsers
// import scala.util.parsing.combinator.lexical._
// import scala.collection.mutable.ListBuffer
// import scala.collection.mutable.Queue

// // import com.badlogic.gdx.Gdx
// // import com.badlogic.gdx.graphics._

// object EisenScriptASTNodes {

//   sealed trait ASTNode

//   // statement top level line of code
//   case class VarDecl(k:String,v:Float) extends ASTNode
//   case class SetEnv(k:String,v:String) extends ASTNode

//   // Rule definition
//   case class RuleDef(name:String, mods:List[RuleMod], actions:List[Statement]) extends ASTNode
//   abstract trait RuleMod extends ASTNode
//   case class MaxDepth(d:Int) extends RuleMod
//   case class Weight(w:Float) extends RuleMod

//   // Action
//   abstract trait Statement extends ASTNode
//   case class Comment() extends Statement
//   case class Action(transforms:List[TransformExpr], call:Call) extends Statement

//   // Transforms
//   case class TransformExpr(times:Expr,list:List[Transform]) extends ASTNode
  
//   abstract trait Transform extends ASTNode
//   // case class TransformFull(p:Pose,s:Vec3) extends Statement
//   // case class TransformV(param:String,value:String) extends Statement
//   case class XMove(e:Expr) extends Transform
//   case class YMove(e:Expr) extends Transform
//   case class ZMove(e:Expr) extends Transform
//   case class XRot(e:Expr) extends Transform
//   case class YRot(e:Expr) extends Transform
//   case class ZRot(e:Expr) extends Transform
//   case class Scale(e:Expr) extends Transform
//   case class Scale3(x:Expr,y:Expr,z:Expr) extends Transform

//   case class Hue(e:Expr) extends Transform
//   case class Sat(e:Expr) extends Transform
//   case class Bright(e:Expr) extends Transform
//   case class Alpha(e:Expr) extends Transform
//   case class ColorTransform(h:HSV) extends Transform
//   case class Color(c:RGBA) extends Transform

//   // Literal / ident
//   abstract trait Expr extends ASTNode
//   case class Literal(v:Float) extends Expr
//   case class Ident(v:String) extends Expr

//   abstract trait Call extends ASTNode
//   case class RuleCall(name:String) extends Call
//   case class DrawBox() extends Call
//   case class DrawGrid() extends Call
//   case class DrawSphere() extends Call
//   case class DrawCylinder() extends Call
//   // case class Draw(m:Mesh) extends Primitive
//   // case class RuleCall(ident:String) extends Primitive
//   // case class Dont() extends Primitive
// }
// import EisenScriptASTNodes._

// class EisenLexer extends StdLexical {
//   override def digit = ( super.digit | hexDigit )
//   lazy val hexDigits = Array('0','1','2','3','4','5','6','7','8','9','.','-')
//   lazy val hexDigit = elem("hex digit", hexDigits.contains(_))
// }

// object EisenScriptParser extends StandardTokenParsers {

// 	override val lexical:EisenLexer = new EisenLexer()
 
//   lexical.delimiters ++= List("{","}","(", ")",",","*","=")
//  	lexical.reserved += ("var","set","rule","md","maxdepth","w","weight",
//  											"x","y","z","rx","ry","rz","s","scale",
//  											"hue","h","sat","brightness","b","value","v","alpha","a","color",
//  											"box","sphere","grid","cylinder")
 
//   // grammar starts here
//   def program = line*
 
//   def line = set | varDecl | action | ruleDef | comment

//   def comment = "//"~ ((ident | numericLit | stringLit )*) ~ "\n" ^^^ Comment()
//   def varDecl = "var" ~ ident ~ "=" ~ numericLit ^^ {
//     case _~name~_~value => VarDecl(name,value.toFloat)
//   }
//   def set = ("set" ~ "maxdepth" ~ numericLit ^^ { case _~k~v => SetEnv("maxdepth",v) }
//             |"set" ~ ident ~ numericLit ^^ { case _~k~v => SetEnv(k,v) })
  
//   // Rule
//   def ruleDef = "rule" ~ ident ~ opt(ruleMod*) ~ "{" ~ ruleBody ~ "}" ^^ {
//     case _~name~mods~_~list~_ => RuleDef(name,mods.getOrElse(List()),list)
//   }
//   def ruleMod = (("md"|"maxdepth") ~ numericLit ^^ { case _~v => MaxDepth(v.toInt)}
//   							 |("w"|"weight") ~ numericLit ^^ { case _~v => Weight(v.toFloat)})
//   def ruleBody = (action | comment)*

//   // Action
//   def action = (texpr*) ~ call ^^ { case ts~ca => Action(ts,ca)}
  
//   def texpr = ( numericLit ~ "*" ~ tlist ^^ { case t~_~list => TransformExpr(Literal(t.toInt), list)}
//  						| ident ~ "*" ~ tlist ^^ { case id~_~list => TransformExpr(Ident(id), list)}
//             | tlist ^^ { case list => TransformExpr(Literal(1), list)} )

//   def tlist = "{" ~ (transformation*) ~ "}" ^^ { case _~list~_ => list }

//   def transformation = (
//       "x"~expr ^^ { case _~e => XMove(e) }
//     | "y"~expr ^^ { case _~e => YMove(e) }
//     | "z"~expr ^^ { case _~e => ZMove(e) }
//     | "rx"~expr ^^ { case _~e => XRot(e)  }
//     | "ry"~expr ^^ { case _~e => YRot(e)  }
//     | "rz"~expr ^^ { case _~e => ZRot(e)  }
//     | ("s"|"scale")~expr~expr~expr ^^ { case _~x~y~z => Scale3(x,y,z) }
//   	| ("s"|"scale")~expr ^^ { case _~e => Scale(e) }
//     | ("h"|"hue")~expr ^^ { case _~e => Hue(e) }
//     | ("sat")~expr ^^ { case _~e => Sat(e) }
//   	| ("b"|"brightness")~expr ^^ { case _~e => Bright(e) }
//   	| ("a"|"alpha")~expr ^^ { case _~e => Alpha(e) }
//   )

//   def call = ( rulecall | box | sphere | grid | cylinder )
//   def rulecall = ident ^^ {case id => RuleCall(id)}
//   def box = "box" ^^^ DrawBox() //Draw(Cube().mesh)
//   def grid = "grid" ^^^ DrawGrid() //Draw(Cube().mesh) //TODO wireframe
//   def sphere = "sphere" ^^^ DrawSphere()
//   def cylinder = "cylinder" ^^^ DrawCylinder()

//   def expr = (  ident ^^ { case id => Ident(id) } 
//               | numericLit ^^ { case n => Literal(n.toFloat)} )



//   def apply( script: String ) = {

//   	//val input = Source.fromFile("input.talk").getLines.reduceLeft[String](_ + '\n' + _)
//   	val tokens = new lexical.Scanner(script)
 
//   	val result = phrase(program)(tokens)

// 		result match {
// 		  case Success(tree, _) => new ModelInterpreter(tree)
		 
// 		  case e: NoSuccess => {
// 		    Console.err.println(e)
// 		    null
// 		  }
// 		}

//   }
// }


// class ModelInterpreter(val tree: List[ASTNode]) {
	
// 	type SymTab = Map[String,String]
// 	type Rule = (SymTab,List[Action])

// 	var env = Map[String,String]()
// 	var gst = Map[String,Float]()

// 	var actionQueue = Queue[(Model,Action)]()
// 	var initialActions = List[Action]()
// 	var rules = Map[String,(ListBuffer[Rule],ListBuffer[Float])]()
// 	var ruleDepth = collection.mutable.Map[String,Int]()

// 	var model = new Model()
// 	var node = model

// 	var primCount = 0
// 	var depth = 0
// 	env = env + ("maxdepth"->"500")
// 	env = env + ("maxobjects"->"2000")

// 	var seed = Random.int().toLong

// 	this(tree)

// 	def apply(tree: List[ASTNode]):ModelInterpreter = {

// 		tree.foreach {
// 			case VarDecl(n,v) => gst = gst + (n->v)
//       case SetEnv(k,v) => env = env + (k->v)
// 			case (a:Action) => initialActions = initialActions :+ a
			
// 			case RuleDef(name,mods,list) => {
// 				val lst = Map[String,String]()
// 				var weight = 1f

// 				mods.foreach{
// 					case Weight(w) => weight = w
// 					case MaxDepth(d) => ()
// 				}

// 				val rule = list.foldLeft((lst,List[Action]()))( (r:(SymTab,List[Action]),s:Statement) => {
// 					s match {
// 						// case Set(k,v) => (r._1 + (k->v), r._2) 
// 						case a:Action => (r._1, r._2 :+ a)
// 					}
// 				})

// 				if(rules.isDefinedAt(name)){
// 					rules(name)._1 += rule
// 					rules(name)._2 += weight
// 				} else rules = rules + (name -> (ListBuffer(rule),ListBuffer(weight)))

// 				// ruleDepth(name) = 0
// 			}
// 			case _ => ()

// 		}
// 		this
// 	}


// 	def applyRule(name:String, n:Model){
// 		val rule = Random.decide(rules(name)._1, rules(name)._2)()
// 		val depth = ruleDepth.getOrElse(name,0)
// 		ruleDepth(name) = depth+1
// 		// if( depth >= env("maxdepth").toInt ) return
// 		// rule._2.foreach( applyAction(_).foreach(n.addChild(_)) )
// 		rule._2.foreach( (a) => actionQueue.enqueue((n,a)) )

// 		// ruleDepth(name) = depth
// 	}

// 	def applyAction(pair:(Model,Action)) = pair match {
//     case (mdl, Action(transformExprs, call)) => 

//       // aggregate nested transform loops right to left
//       var res = transformExprs.foldRight(Vector[Model]()) {
//         case (TransformExpr(times, transforms),m) =>
          
//         // merge loops transform
//         val (p,s,c) = mergeTransformList(transforms)
//         var pp = Pose(p) //copy
//         var ss = Vec3(s) //copy
//         var cc = HSV(c)
//         var group = Vector[Model]()

//         // each time through the loop make model and stack transform
//         for( i <- (0 until eval(times).toInt )){
//           node = new Model{ pose = pp; scale = ss}
//           node.colorTransform = cc

//           // add existing models from outer right most loops as children
//           m.foreach( node.addChild(_) ) 

//           group = group :+ node
//           pp = pp*p
//           ss = ss*s
//           cc = cc*c
//         }
//         group // return list of nodes
//       }

//       if( res.length == 0) res = Vector(new Model())
//       res.foreach( _.getLeaves().foreach( (n) => {
//        applyPrimitive(call,n)
//       }))

//       res.foreach( mdl.addChild(_))
//       res
// 	}
// 	def applyPrimitive(p:Call, n:Model){
// 		if( primCount > env("maxobjects").toInt ) return
// 		n.material = Material(model.material)
// 		p match {
// 			case RuleCall(r) => applyRule(r,n)
//       case DrawBox() => n.mesh = Cube().mesh; primCount += 1
//       case DrawGrid() => n.mesh = Cube().mesh; primCount += 1 //XXX
//       case DrawSphere() => n.mesh = Sphere().mesh; primCount += 1
//       case DrawCylinder() => n.mesh = Cylinder().mesh; primCount += 1

// 			// case Draw(m) => n.mesh = m; /*n.addPrimitive( d )*/; primCount += 1
// 			// case Dont() => ()
// 		}
// 	}

// 	def mergeTransformList(ts:List[Transform]) = {
//     val p = Pose()
//     val rot = Vec3(0)
//     val s = Vec3(1)
//     val c = HSV(0,1,1)
// 		ts.foreach( (t) => {
// 			t match {
//         case XMove(e) => p.pos.x += eval(e)
//         case YMove(e) => p.pos.y += eval(e)
//         case ZMove(e) => p.pos.z += eval(e)
//         case XRot(e) => rot.x += eval(e).toRadians
//         case YRot(e) => rot.y += eval(e).toRadians
//         case ZRot(e) => rot.z += eval(e).toRadians
//         case Scale(e) => s *= Vec3(eval(e))
//         case Scale3(x,y,z) => s *= Vec3(eval(x),eval(y),eval(z))
//         case Hue(e) => c.h = (c.h + eval(e)) % 1f
//         case Sat(e) => c.s *= eval(e)
//         case Bright(e) => c.v *= eval(e)
//         case Alpha(e) => () //c.a *= eval(e)
// 			}
// 		})
//     p.quat = Quat().fromEuler(rot)
//     (p,s,c)
// 	}

//   def eval(e:Expr):Float = e match {
//     case Literal(v) => v
//     case Ident(v) => gst(v)
//   }

// 	def set(s:String,v:Float) = gst = gst + (s->v)

// 	def buildModel(m:Model=Model()) = {
// 		Random.seed(seed)
// 		ruleDepth.clear
// 		primCount = 0

// 		model = m
// 		actionQueue = Queue[(Model,Action)]()
// 		// initialActions.foreach( (a) => applyAction(a).foreach(model.addChild(_)) )
// 		initialActions.foreach( (a) => actionQueue.enqueue((model,a)) )
// 		var iterations = 0
// 		while( !actionQueue.isEmpty && iterations < env("maxdepth").toInt ){
// 			val actions = actionQueue.toList
// 			actionQueue.clear

// 			actions.foreach( applyAction(_) )

// 			iterations += 1
// 		}
// 		model.applyColorTransform
// 	}


// }
