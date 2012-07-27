package com.fishuyo
package ray
import maths._
import graphics._

import scala.collection.mutable.ListBuffer
import javax.media.opengl.{GL, GL2}

/**
* Ray/Geometry intersection
*/
class Hit(val obj: Geometry, val ray:Ray, val t: Float, val inside: Boolean = false) extends Ordered[Hit] {
  def point() = ray.o + ray.d * t
  def norm() = if (!inside) obj.normalAt( point ); else -obj.normalAt( point )
  def compare(h:Hit) = t compare h.t
}

/**
* Materials
*/
abstract class Material

case class Plastic( c: RGB ) extends Material
case class Mirror() extends Material
case class Glass( ior: Float ) extends Material
case class Diffuse( f: Float ) extends Material
case class Emmiter( c: RGB ) extends Material 

/**
* Geometries
*/
abstract class Geometry(var material: Material ) extends GLDrawable {

  var color = RGB.red
  material match { 
    case Plastic(c) => color = c 
    case _ => color = RGB.black
  }

  def intersect( ray: Ray ) : Hit 
  def normalAt( p: Vec3 ) : Vec3 = Vec3(0) 
  def translate( d: Vec3 ) = ()
  def setColor( c: RGB ) = color = c
  
}


class Sphere( var cen: Vec3, val r: Double, material: Material ) extends Geometry( material ){

  
  override def intersect( ray: Ray ) : Hit = {
    val o_c = ray.o - cen
    val A = ray.d dot ray.d
    val B = 2 * ( ray.d dot o_c )
    val C = (o_c dot o_c) - r*r
    val det = B*B - 4*A*C

    if( det > 0 ){
      val t1 = (-B - math.sqrt(det).toFloat ) / (2*A)
      if ( t1 > 0f ) return new Hit( this, ray, t1 )
      val t2 = (-B + math.sqrt(det).toFloat ) / (2*A)
      if ( t2 > 0f ) return new Hit( this, ray, t2, true )

    } else if ( det == 0 ){
      val t = -B / (2*A)
      if ( t > 0f ) return new Hit( this, ray, t )
    }
    null
  }

  override def normalAt( p: Vec3) : Vec3 = (p - cen).normalize
  override def translate( d: Vec3 ) = (cen = cen + d)

  override def onDraw( gl: GL2){
    GLDraw.cube( cen, Vec3(2.f*r.toFloat) )(gl)
  }

}

class Triangle( val vertices:(Vec3,Vec3,Vec3), material: Material ) extends Geometry( material ){
 
  val normal: Vec3 = (( vertices._2 - vertices._1 ) cross ( vertices._3 - vertices._1 )).normalize

  override def intersect( ray: Ray ) : Hit = {
    val n = normalAt( vertices._1 )
    val dn = ray.d dot n
    
    if( dn == 0) return null

    val t = -(( ray.o - vertices._1 ) dot n ) / dn
    if( t < 0.f) return null
    val x = ray(t)

    if( (((vertices._2 - vertices._1) cross ( x - vertices._1 )) dot n) < 0 ||
        (((vertices._3 - vertices._2) cross ( x - vertices._2 )) dot n) < 0 ||
        (((vertices._1 - vertices._3) cross ( x - vertices._3 )) dot n) < 0 ) return null

    return new Hit( this, ray, t )
    
  }
  
  override def normalAt( p: Vec3) : Vec3 = normal

  override def onDraw( gl: GL2 ) = {
    gl.glNormal3f( normal.x, normal.y, normal.z)  
    gl.glVertex3f( vertices._1.x, vertices._1.y, vertices._1.z )
    gl.glVertex3f( vertices._2.x, vertices._2.y, vertices._2.z )
    gl.glVertex3f( vertices._3.x, vertices._3.y, vertices._3.z )
  }

  override def toString = vertices._1 + " " + vertices._2 + " " + vertices._3
}

/**
* Lights
*/
abstract class LightSource( val p: Vec3, val c: RGB, val g: Geometry = null){
  
  def shade( hit: Hit ) : RGB = {

    val light = (p - hit.point).normalize
    val cr = hit.obj.color
    val ca = cr * 0.0f


    val shadow = Scene.intersect( new Ray(hit.point + light * 0.01f, light) )
    if( shadow != null && (shadow.obj.material match { case Emmiter(c) => false; case _ => true }) ){

      return RGB.black

    }else {

      val e = Vec3( 0,0, -1)
      val r = (hit.norm * 2 * (light dot hit.norm)) - light

      val diffuse = c * math.max(0, hit.norm dot light)
      val specular = c * math.pow( math.max( 0, e dot r ), 10).toFloat

      val color = cr * ( ca + diffuse) +  ( specular )
      
      return color
    }

  }
}

class PointLight( p: Vec3, c: RGB ) extends LightSource(p, c, new Sphere( p, 1.0, Emmiter(c)))

class AreaLight( p: Vec3, c: RGB, val w: Float ) extends LightSource( p, c ){
  
  override def shade( hit: Hit ) : RGB = {

    val rand = new util.Random
    var color = RGB.black

    for( i <- 0 until 100 ){
      val r = Vec3( (rand.nextFloat - .5) * w, 0, (rand.nextFloat - .5) * w)
      
      val light = (p + r - hit.point).normalize
      val cr = hit.obj.color
      val ca = cr * 0.0f
    
      val shadow = Scene.intersect( new Ray(hit.point + light * 0.01f, light) )
      if( shadow != null && (shadow.obj.material match { case Emmiter(c) => false; case _ => true }) ){

      }else {

        val e = Vec3( 0,0, -1)
        val r = (hit.norm * 2 * (light dot hit.norm)) - light

        val diffuse = c * math.max(0, hit.norm dot light)
        val specular = c * math.pow( math.max( 0, e dot r ), 10).toFloat

        val col = cr * ( ca + diffuse) +  ( specular )
       
        color += col / 100
      }
    }
    color
  }
}

class TriangleMesh( material: Material) extends Geometry(material) {

  val vertices = new ListBuffer[Vec3] 
  val normals = new ListBuffer[Vec3]
  val textcoords = new ListBuffer[Vec3]
  val triangles = new ListBuffer[Triangle]

  def addFace( u: Int, v:Int, w:Int) = triangles += new Triangle( (vertices(u-1), vertices(v-1), vertices(w-1)), material ) 

  def normalize = {
  
    var min = Vec3( java.lang.Double.MAX_VALUE )
    var max = Vec3( java.lang.Double.MIN_VALUE )
    vertices.foreach( {
      case Vec3( x,y,z ) => {
        min = Vec3( math.min( min.x, x ), math.min( min.y, y ), math.min( min.z, z ))
        max = Vec3( math.max( max.x, x ), math.max( max.y, y ), math.max( max.z, z ))
      }
    })

    val half = Vec3(.5)
    val diff = max - min
    val maxDiff = math.max( math.max( diff.x, diff.y ), diff.z)
    for (i <- 0 until vertices.length ) {
      vertices(i) -= min
      vertices(i) *= 1.0f / maxDiff
      vertices(i) -= half
    }
    println( min + " " + max )
  }

  override def translate( v: Vec3 ) = for (i <- 0 until vertices.length ) vertices(i) += v

  override def intersect( ray: Ray ) : Hit = {
    //println("TriMesh: intersect")
    val hits = triangles.map( _.intersect(ray) ).collect({case h:Hit => h})
    //println( hits.size + " " + hits.first )
    hits.size match {
      case 0 => null
      case _ =>  hits.min //( Ordering[Double].on[Hit]( { case h:Hit => h.t; case _ => java.lang.Double.MAX_VALUE }) )
    }
  }

  override def onDraw( gl: GL2 ) = {
    gl.glBegin(GL.GL_TRIANGLES)
      gl.glColor3f( .8f, 0, 0)
      triangles.foreach( t => t.onDraw( gl ) )
    gl.glEnd
  }

  override def toString = vertices.toString //( (v:Vec3) => println( v )) 

}
