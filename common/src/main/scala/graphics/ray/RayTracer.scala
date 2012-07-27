package com.fishuyo
package ray
import maths.Vec3
import graphics._
import audio._

import actors.Actor
import actors.Actor._

object RayTracer extends Actor {
  def act() {
    val r = new util.Random
    loop{
      react{
        case Rays(n) => nrays = n
        case Step => {
          var n = nrays
          while( n > 0){
            val d = Vec3( r.nextFloat-.5f, r.nextFloat-.5f, r.nextFloat-.5f).normalize
            val ray = new Ray( Camera.position, d)
            RayTracer( ray ) match {
              case Some(i) => Convolver.addImpulse(i)
              case _ => None
            }
            n -= 1
          }
          RayTracer( new Ray( Camera.position, (Vec3(2.5f,0.f,-2.5f) - Camera.position) )) match {
            case Some(i) => Convolver.addImpulse(i)
            case _ => None
          }
          
          //Convolver ! Go
          println( "count: " + RayTracer.nrays + " " + RayTracer.max_depth + " " + RayTracer.in + " " + RayTracer.count )
          RayTracer.in = 0; RayTracer.count = 0;
        }

        case Stop => println( "Tracer going down.."); exit()
      }
    }
    
  }

  var nrays = 100
  var in:Int = 0
  var out:Int = 0
  var count:Int = 0
  
  var max_depth:Int = 10

  def apply( ray: Ray, depth: Int = 0 ) : Option[(Float,Float)] = {
    
    in += 1
    
    if( depth > max_depth) return None
    
    if( ray == null) return None

    val hit = Scene.intersect( ray );
    //println( hit ) 
    if( hit == null) return None

    hit.obj.material match {
      case Emmiter(c) => {
        count += 1
        Some((hit.t, 1.f))
      }
      case _ => {
        this( reflect( hit ), depth + 1) match {
          case Some((d,v)) => Some( (d+hit.t, v*.8f) )
          case None => None
        }
      }
    }
  }

  /*def shade( l: LightSource, hit: Hit, depth: Int ) : RGB = {

    val light = (l.p - hit.point).normalize
    val cl = l.c
    val cr = hit.obj.color
    val ca = cr * 0.0f
  
    
    val shadow = Scene.intersect( new Ray(hit.point + light * 0.001f, light) )    
    if( shadow != null && (shadow.obj.material match { case Emmiter(c) => false; case _ => true }) ){

      return RGB.black

    }else {

      val e = Vec3( 0,0, -1)
      val r = (hit.norm * 2 * (light dot hit.norm)) - light

      val diffuse = cl * math.max(0, hit.norm dot light)
      val specular = cl * math.pow( math.max( 0, e dot r ), 10).toFloat

      val color = cr * ( ca + diffuse) +  ( specular )

      //println( hit.point + " " + hit.norm + " " + r )
      //println( color + " " + cr )
      return color
    }

  }*/

  def reflect( hit: Hit ) : Ray = {
    var r = hit.ray.d - hit.ray.o
    r -=  hit.norm * 2 * ( r dot hit.norm )
    //r = r.normalize
    new Ray( hit.point + r * 0.01f, r )
  }

  def refract( hit: Hit, ior: Float ) : Ray = {

    val d = (hit.ray.d - hit.ray.o )
    val cosTheta = (hit.norm dot -d )
    //println( d + " " + hit.norm + " " + cosTheta )
    val sinPhi2 = ior * ior * ( 1 - cosTheta * cosTheta )
    if ( sinPhi2 > 1.0 ) return null

    val t = ( ((d + (hit.norm * cosTheta)) * ior) - ( hit.norm *  scala.math.sqrt( 1 - sinPhi2 ).toFloat ) )

    val o = hit.point + t * 0.01f
    
    return new Ray( o, t )
  }

}



