package com.fishuyo.seer
package graphics

import maths._
import spatial._

import scala.io.Source
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.combinator.JavaTokenParsers
import scala.collection.mutable.ListBuffer

import com.badlogic.gdx.Gdx
// import com.badlogic.gdx.graphics._

object Obj {
	def apply(file:String) = Model(ObjParser(file))
}

object ObjParser extends JavaTokenParsers {

	// implicit def s2f(s:String) = s.toFloat

	abstract class Statement
	case class Vertex(v:Vec3) extends Statement
	case class Normal(n:Vec3) extends Statement
	case class Face(u:String,v:String,w:String) extends Statement
	case class TexCoord(u:Vec2) extends Statement
	case class Empty extends Statement
 
 	def FP = floatingPointNumber
 	def WN = wholeNumber

 	// protected override val whiteSpace = """(\s|#.*|(?m)/\*(\*(?!/)|[^*])*\*/)+""".r

  def comment = """#.*""".r ^^ { case c => Empty() }
  def v = "v"~>FP~FP~FP ^^ { case x~y~z => Vertex(Vec3(x.toFloat,y.toFloat,z.toFloat)) }  
  def vn = "vn"~>FP~FP~FP ^^ { case x~y~z => Normal(Vec3(x.toFloat,y.toFloat,z.toFloat)) }  
  def f_v= "f"~>WN~WN~WN ^^ { case u~v~w => Face(u,v,w) } 
  def f_vt= "f"~>WN~"/"~WN~WN~"/"~WN~WN~"/"~WN ^^ { case u~_~_~v~_~_~w~_~_ => Face(u,v,w) } 
  def f_vn= "f"~>WN~"//"~WN~WN~"//"~WN~WN~"//"~WN ^^ { case u~_~x~v~_~y~w~_~z => Face(u,v,w) } 
  def f_vtvn= "f"~>WN~"/"~WN~"/"~WN~WN~"/"~WN~"/"~WN~WN~"/"~WN~"/"~WN ^^ { case u~_~_~_~_~v~_~_~_~_~w~_~_~_~_ => Face(u,v,w) } 

  def vt = "vt"~>FP~FP ^^ { case u~v => TexCoord(Vec2(u.toFloat,v.toFloat)) }
  
  def usemtl = "usemtl" ^^ { case vt => Empty() }
  def s = "s" ^^ { case vt => Empty() }

  def statement : Parser[Statement] = v | vn | f_v | f_vt | f_vn | vt | s | usemtl | comment
  def file : Parser[List[Statement]] = statement*


  def parseLine( line: String ){
    println ( parse( statement, line ))
  }


  def apply(filename: String, normalize:Boolean=true):Mesh = {
    val mesh = new Mesh()
    val file = Source.fromFile( filename )

    def addFace(u:Int,v:Int,w:Int){
    	val l = List(u,v,w).map( (i) => (i - 1).toShort )
    	mesh.indices ++= l
    	mesh.wireIndices ++= l.combinations(2).flatten
    }

    file.getLines.foreach( (l) => {
      parse( statement, l.trim ) match {
        case Success( s, _ ) => s match {
          case Vertex(v) => mesh.vertices += v
          case Normal(n) => mesh.normals += n 
          case Face(u,v,w) => addFace(u.toInt,v.toInt,w.toInt)
          case _ => None
        }
        case x => println( x )
      }
    })
    file.close

		if( normalize ) mesh.normalize

		mesh
  }
}


