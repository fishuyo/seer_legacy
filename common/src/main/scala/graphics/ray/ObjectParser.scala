package com.fishuyo
package ray
import maths._
import graphics._

import scala.io.Source
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.combinator.JavaTokenParsers
import scala.collection.mutable.ListBuffer

abstract trait Statement
case class Vertex(x:Float,y:Float,z:Float) extends Statement
case class Face(u:Int,v:Int,w:Int) extends Statement
case class VT extends Statement
case class Empty extends Statement

object ObjectParser extends JavaTokenParsers {
 
  def comment = """#\.*""".r ^^ { case c => Empty() }
  def v = "v"~>floatingPointNumber~floatingPointNumber~floatingPointNumber ^^ { case x~y~z => Vertex(x.toFloat,y.toFloat,z.toFloat) }  
  def f = "f"~>wholeNumber~wholeNumber~wholeNumber ^^ { case u~v~w => Face(u.toInt,v.toInt,w.toInt) }  
  def vt = "vt" ^^ { case vt => VT() }
  def usemtl = "usemtl" ^^ { case vt => Empty() }
  def s = "s" ^^ { case vt => Empty() }

  def statement : Parser[Statement] = v | f | vt | s | usemtl | comment
  def file : Parser[List[Statement]] = statement*


  def parseLine( line: String ){
    println ( parse( statement, line ))
  }

  def apply( filename: String ) : TriangleMesh = {

    val obj = new TriangleMesh( Plastic(RGB.white) )
    val file = Source.fromFile( filename )

    file.getLines.foreach( (l) => {
      parse( statement, l.trim ) match {
        case Success( s, _ ) => s match {
          case Vertex(x,y,z) => obj.vertices += Vec3(x,y,z) 
          case Face(u,v,w) => obj.addFace(u,v,w)
          case _ => None
        }
        case x => println( x )
      }
    })
    file.close
    obj
  }
}


