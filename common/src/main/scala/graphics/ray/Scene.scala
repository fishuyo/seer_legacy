
package com.fishuyo
package ray
import maths._
import graphics._

import scala.collection.mutable.ListBuffer
import javax.media.opengl._

import de.sciss.synth.io._
import sun.audio._
import javax.sound.sampled._

/**
* Camera
*/
object RCamera { //? demolish this...
  val v = 2.f
  val a = 90.f
  val rad = scala.math.Pi / 180.f 
  var position = Vec3(0,0,2)
  var velocity = Vec3(0,0,0)
  var direction = Vec3(0,0,0)
  var upDirection = Vec3(0,1,0)

  var elevation=0.0f
  var azimuth=0.0f
  var roll=0.0f
  var w = Vec3( 0,0,0 ) //angluar velocity

  def forward() = velocity = Vec3( math.sin( azimuth * rad),0, -math.cos(azimuth*rad) ).normalize * v
  def backward() = velocity = Vec3( math.sin( (180.f + azimuth) * rad),0, -math.cos((180.f+azimuth)*rad) ).normalize * v
  def left() = velocity = Vec3( math.sin( (270.f + azimuth) * rad),0, -math.cos((270.f+azimuth)*rad) ).normalize * v
  def right() = velocity = Vec3( math.sin( (90.f + azimuth) * rad),0, -math.cos((90.f+azimuth)*rad) ).normalize * v
  def up() = velocity = Vec3(0,v,0)
  def down() = velocity = Vec3(0,-v,0)

  def lookUp = w = Vec3(-a,0,0) 
  def lookDown = w = Vec3(a,0,0)
  def lookLeft = w = Vec3(0,-a,0)
  def lookRight = w = Vec3(0,a,0)

  def stop = velocity = Vec3(0)
  def stopLook = w = Vec3(0)

  def step( dt: Float ) = {
    position += velocity * dt
    val a = w * dt
    elevation += a.x; azimuth += a.y; roll += a.z;
    if( elevation > 180.f ) elevation = -180.f
    if( elevation < -180.f ) elevation = 180.f
    if( azimuth > 180.f ) azimuth = -180.f
    if( azimuth < -180.f ) azimuth = 180.f
  }
}
/**
* Singleton scene object to contain list of scene geometries
*/
object Scene {

  val objects = new ListBuffer[Geometry]
  val lights = new ListBuffer[LightSource]
  val sounds = new ListBuffer[SoundSource]

  //var drawSoundSources = true

  def push( o: Geometry) = objects += o
  def push( l: LightSource) = { lights += l; if(l.g != null) objects += l.g }
  def push( s: SoundSource) = { sounds += s; objects += s }

  def intersect( ray: Ray ) : Hit = {

    //var min_hit = new Hit( null, null, java.lang.Double.MAX_VALUE )

    /*for( i <- 0 until objects.size ){
      var hit = objects(i).intersect(ray) 
      if( hit != null && hit < min_hit ) min_hit = hit
    }
    if( min_hit.ray != null ) return min_hit
    null*/

    val hits = objects.map( _.intersect(ray) ).collect({case h:Hit => h})
    //println( "Scene intersect hits: " + hits.size )
    hits.size match {
      case 0 => null
      case _ =>  hits.min //( Ordering[Double].on[Hit]( { case h:Hit => h.t; case _ => java.lang.Double.MAX_VALUE }) ) 
    }
    
  }

  def onDraw( gl: GL2 ) = {
    objects.foreach( o => o.onDraw(gl) )
    //if( drawSoundSources ) sounds.foreach( s => s.onDraw(gl) )
  }

  def initCubeRoom( s:Float = 10.f, p:Vec3 = Vec3( 0,0,0 ) ) = {
    val x=p.x-s/2; val y=p.y-s/2; val z=p.z+s/2;

    val mesh = new TriangleMesh( Mirror() )
    mesh.vertices += Vec3( x,y,z )
    mesh.vertices += Vec3( x+s,y,z )
    mesh.vertices += Vec3( x+s,y+s,z )
    mesh.vertices += Vec3( x,y+s,z )
    mesh.vertices += Vec3( x,y+s,z-s )
    mesh.vertices += Vec3( x,y,z-s )
    mesh.vertices += Vec3( x+s,y,z-s )
    mesh.vertices += Vec3( x+s,y+s,z-s )
    mesh.addFace( 1,2,3 )
    mesh.addFace( 1,3,4 )
    mesh.addFace( 4,3,8 )
    mesh.addFace( 4,8,5 )
    mesh.addFace( 5,8,7 )
    mesh.addFace( 5,7,6 )
    mesh.addFace( 6,1,4 )
    mesh.addFace( 6,4,5 )
    mesh.addFace( 2,1,6 )
    mesh.addFace( 2,6,7 )
    mesh.addFace( 3,2,7 )
    mesh.addFace( 3,7,8 )
    
    push( mesh )

    //val sound = new SoundSource( Vec3(2.5f,0,-2.5), "acoustics.wav" )
    //push( sound )

    //AudioOut.setSource( sound )
  }

}

class SoundSource( pos: Vec3, path: String) extends Sphere(pos, .1f, Emmiter(RGB.green)) {
 
  val in = AudioFile.openRead( path )
  val format = AudioSystem.getAudioFileFormat( in.file.get )

  val b = in.buffer( 1024 )
  var left = in.numFrames
  //val impulses = new Array[Float](1024)

  def read(out: Array[Float] ) = {
    in.read( b )
    Array.copy( b(0), 0, out, 0, 1024 )
  }
  
  /*def addImpulse( i:(Float,Float) ) = {
    val speed = 343.f
    val t = i._1 / speed * AudioOut.sampleRate //delay in samples
    val v = i._2 //attenuation
  
    val i = t.toInt
    impulses.update(i, v + impulses(i))
  } */

} 
