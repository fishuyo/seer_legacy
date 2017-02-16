package com.fishuyo.seer
package spatial
package geometry


trait Pickable {
  def intersect(r:Ray):Option[Hit] = None
  var onPick = (hit:Hit)=>{}
}

/**
* Ray/Geometry intersection
*/
class Hit(val obj: Pickable, val ray:Ray, val t: Float, val inside: Boolean = false) extends Ordered[Hit] {
  obj.onPick(this)
  def point() = ray(t)
  // def norm() = if (!inside) obj.normalAt( point ); else -obj.normalAt( point )
  def compare(h:Hit) = t compare h.t
}



/**
* Geometries
*/
trait Geometry extends Pickable {
  // def intersect( ray: Ray ) : Option[Hit] = None
  def normalAt( p: Vec3 ) : Vec3 = Vec3(0) 
}


class Sphere( val center: Vec3, val radius: Double ) extends Geometry {

  
  override def intersect( ray: Ray ) : Option[Hit] = {
    val o_c = ray.o - center
    val A = ray.d dot ray.d
    val B = 2 * ( ray.d dot o_c )
    val C = (o_c dot o_c) - radius*radius
    val det = B*B - 4*A*C

    if( det > 0 ){
      val t1 = (-B - math.sqrt(det).toFloat ) / (2*A)
      if ( t1 > 0f ) return Some(new Hit( this, ray, t1 ))
      val t2 = (-B + math.sqrt(det).toFloat ) / (2*A)
      if ( t2 > 0f ) return Some(new Hit( this, ray, t2, true ))

    } else if ( det == 0 ){
      val t = -B / (2*A)
      if ( t > 0f ) return Some(new Hit( this, ray, t ))
    }
    None
  }

  override def normalAt( p: Vec3) : Vec3 = (p - center).normalize
}

class Triangle( val vertices:(Vec3,Vec3,Vec3)) extends Geometry {
 
  val normal: Vec3 = (( vertices._2 - vertices._1 ) cross ( vertices._3 - vertices._1 )).normalize

  override def intersect( ray: Ray ) : Option[Hit] = {
    val n = normalAt( vertices._1 )
    val dn = ray.d dot n
    
    if( dn == 0) return None

    val t = -(( ray.o - vertices._1 ) dot n ) / dn
    if( t < 0f) return None
    val x = ray(t)

    if( (((vertices._2 - vertices._1) cross ( x - vertices._1 )) dot n) < 0 ||
        (((vertices._3 - vertices._2) cross ( x - vertices._2 )) dot n) < 0 ||
        (((vertices._1 - vertices._3) cross ( x - vertices._3 )) dot n) < 0 ) return None

    return Some(new Hit( this, ray, t ))
    
  }  
  override def normalAt( p: Vec3):Vec3 = normal
}

class Quad( val vertices:(Vec3,Vec3,Vec3,Vec3)) extends Geometry {

  def this(center:Vec3, w:Float, h:Float) = this((center + Vec3(-w,-h,0), center + Vec3(w,-h,0), center + Vec3(w,h,0), center + Vec3(-w,h,0)))
 
  val normal: Vec3 = (( vertices._2 - vertices._1 ) cross ( vertices._3 - vertices._1 )).normalize
  
  override def intersect(ray:Ray): Option[Hit] = {
    val n = normal
    val dn = ray.d dot n
    
    if( dn == 0) return None

    val t = -(( ray.o - vertices._1 ) dot n ) / dn
    if( t < 0f) return None
    val x = ray(t)

    if( (((vertices._2 - vertices._1) cross ( x - vertices._1 )) dot n) < 0 ||
        (((vertices._3 - vertices._2) cross ( x - vertices._2 )) dot n) < 0 ||
        (((vertices._4 - vertices._3) cross ( x - vertices._3 )) dot n) < 0 ||
        (((vertices._1 - vertices._4) cross ( x - vertices._4 )) dot n) < 0 ) return None

    val hit = new Hit(this, ray, t)
    onPick(hit)
    return Some(hit)
  }

  override def normalAt( p: Vec3):Vec3 = normal
}

