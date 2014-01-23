// package com.fishuyo.seer
// package graphics
// import maths._
// import spatial._

// import scala.io.Source
// import scala.util.parsing.combinator.RegexParsers
// import scala.util.parsing.combinator.JavaTokenParsers
// import scala.collection.mutable.ListBuffer

// import com.badlogic.gdx.Gdx
// import com.badlogic.gdx.graphics._

// abstract trait Statement
// case class Vertex(x:Float,y:Float,z:Float) extends Statement
// case class Normal(x:Float,y:Float,z:Float) extends Statement
// case class Face(u:Short,v:Short,w:Short) extends Statement
// case class VT extends Statement
// case class Empty extends Statement

// object ObjParser extends JavaTokenParsers {
 
//   def comment = """#\.*""".r ^^ { case c => Empty() }
//   def v = "v"~>floatingPointNumber~floatingPointNumber~floatingPointNumber ^^ { case x~y~z => Vertex(x.toFloat,y.toFloat,z.toFloat) }  
//   def vn = "vn"~>floatingPointNumber~floatingPointNumber~floatingPointNumber ^^ { case x~y~z => Normal(x.toFloat,y.toFloat,z.toFloat) }  
//   def f_v= "f"~>wholeNumber~wholeNumber~wholeNumber ^^ { case u~v~w => Face(u.toShort,v.toShort,w.toShort) } 
//   def f_vt= "f"~>wholeNumber~"/"~wholeNumber~wholeNumber~"/"~wholeNumber~wholeNumber~"/"~wholeNumber ^^ { case u~_~x~v~_~y~w~_~z => Face(u.toShort,v.toShort,w.toShort) } 
//   def f_vn= "f"~>wholeNumber~"//"~wholeNumber~wholeNumber~"//"~wholeNumber~wholeNumber~"//"~wholeNumber ^^ { case u~_~x~v~_~y~w~_~z => Face(u.toShort,v.toShort,w.toShort) } 

//   def vt = "vt" ^^ { case vt => VT() }
//   def usemtl = "usemtl" ^^ { case vt => Empty() }
//   def s = "s" ^^ { case vt => Empty() }

//   def statement : Parser[Statement] = v | vn | f_v | f_vt | f_vn | vt | s | usemtl | comment
//   def file : Parser[List[Statement]] = statement*


//   def parseLine( line: String ){
//     println ( parse( statement, line ))
//   }

//   def apply( filename: String, mode:String="lines", normalize:Boolean=true ) : GLPrimitive = {

//     val obj = new TriangleMesh()
//     val file = Source.fromFile( filename )

//     file.getLines.foreach( (l) => {
//       parse( statement, l.trim ) match {
//         case Success( s, _ ) => s match {
//           case Vertex(x,y,z) => obj.vertices += Vec3(x,y,z)
//           case Normal(x,y,z) => obj.normals += Vec3(x,y,z) 
//           case Face(u,v,w) => obj.addFace((u-1).toShort,(v-1).toShort,(w-1).toShort)
//           case _ => None
//         }
//         case x => println( x )
//       }
//     })
//     file.close

// 		if( normalize ) obj.normalize

// 		val mesh = new Mesh(true, obj.vertices.length, obj.indices.length, VertexAttribute.Position, VertexAttribute.Normal)
// 		val vertices = new Array[Float](3*2*obj.vertices.length)
// 		for( i<-(0 until obj.vertices.length)){
// 			vertices(6*i) = obj.vertices(i).x
// 			vertices(6*i+1) = obj.vertices(i).y
// 			vertices(6*i+2) = obj.vertices(i).z
// 			vertices(6*i+3) = obj.normals(i).x
// 			vertices(6*i+4) = obj.normals(i).y
// 			vertices(6*i+5) = obj.normals(i).z
// 		}
// 		val indices = obj.indices.toArray
// 		mesh.setVertices(vertices)
// 		mesh.setIndices(indices)

// 		var draw = () => { mesh.render(Shader(), GL10.GL_LINES)}
// 		mode match {
// 			case "triangles" => draw = () => { mesh.render(Shader(), GL10.GL_TRIANGLES)}
// 			case _ => 
// 		}
//     new GLPrimitive(Pose(),Vec3(1.f),mesh,draw)
//   }
// }


