
package com.fishuyo
package examples.dla3d

import maths._
import graphics._
import ray._
import io._
import spatial._

import java.awt.event._
import javax.media.opengl._
import javax.media.opengl.fixedfunc.{GLLightingFunc => L}

import scala.collection.mutable.ListBuffer
//import scala.tools.nsc.Interpreter._

object Main extends App {


  GLScene.push( ParticleCollector )
  
  //ParticleCollector.seedLine( Vec3(0,-1,0), Vec3(0,1,0) )
  //ParticleCollector.seedLine( Vec3(0,.2,0), Vec3(-.2,.8,0) )
  //ParticleCollector.seedLine( Vec3(0,.4,0), Vec3(.3,.6,0) )
  //ParticleCollector.seedLine( Vec3(0,-.1,0), Vec3(0,.1,0) )
  //ParticleCollector.seedLine( Vec3(-.5,0,0), Vec3(.5,0,0) )
  //ParticleCollector.seedLine( Vec3(0,0,-.5), Vec3(0,0,.5) )
  //ParticleCollector.seedLine( Vec3(-.4,0,-.4), Vec3(.4,0,.4) )
  //ParticleCollector.seedLine( Vec3(.4,0,-.4), Vec3(-.4,0,.4) )

  ParticleCollector.seedPlane( Vec3( -.2,0,-.2), Vec3(.2,0,.2) )

  //var p = new Particle(Vec3(.01,0,.01),Vec3(1,0,1).normalize)
  //p.setAlignment("line")
  //ParticleCollector.insertPoint(p)
  //p = new Particle(Vec3(-.01,0,.01),Vec3(-1,0,1).normalize)
  //p.setAlignment("line")
  //ParticleCollector.insertPoint(p)
  //p = new Particle(Vec3(0,0,-.01),Vec3(0,0,-1).normalize)
  //p.setAlignment("line")
  //ParticleCollector.insertPoint(p)
  
  //ParticleCollector.insertPoint(new Particle)

  //ParticleCollector.particles.foreach( _.setAlignment("line") )

  val win = new GLRenderWindow
  win.addKeyMouseListener( Input )
  
  /*var count = ParticleCollector.collection.size
  while( count < 15000 ) {
    while( count == ParticleCollector.collection.size ) ParticleCollector.step( .2f )
    count = ParticleCollector.collection.size
    println( count )
  }*/

  //GLScene.push( ParticleCollector )

}

object Input extends KeyMouseListener {
  override def keyPressed(e:KeyEvent) = {
    val d = new java.util.Date
    val t = d.getTime
    val k=e.getKeyCode
    k match {
      case KeyEvent.VK_R => ParticleCollector.rotate = !ParticleCollector.rotate
      case KeyEvent.VK_F => ParticleCollector.thresh += .005f
      case KeyEvent.VK_V => ParticleCollector.thresh -= .005f 
      case KeyEvent.VK_G => ParticleCollector.pointSize += 1.f
      case KeyEvent.VK_B => ParticleCollector.pointSize -= 1.f
      case KeyEvent.VK_I => ParticleCollector.writePoints("points_" + t + ".point")
      case KeyEvent.VK_O => ParticleCollector.writeOrientedPoints("points_" + t + ".xyz")
      case KeyEvent.VK_P => ParticleCollector.writePoints2D("points2Dspun_" + t + ".xyz")
      case KeyEvent.VK_M => Main.win.toggleCapture
      case KeyEvent.VK_Z => ParticleCollector.showField = !ParticleCollector.showField
      case _ => null
    }
  }
}

class Particle( pos :Vec3=Vec3(0), var vel:Vec3=Vec3(0) ) extends Vec3(pos.x,pos.y,pos.z) {

  var c: RGB = RGB.green
  var f: (Particle)=>Unit = (p:Particle) => {
        if( util.Random.nextFloat < 1.9f){
          p.set( p.lerp(this + vel * .01f, .7f ))
          p.vel = p.vel.lerp( vel, .9f)
        }
        //println("line")
      }

  def setAlignment(s:String) = {
    s match {
      case "line" => f = (p:Particle) => {
        if( util.Random.nextFloat < 1.9f){
          p.set( p.lerp(this + vel * .01f, .99f ))
          p.vel = p.vel.lerp( vel, .99f)
        }
        println("line")
      }
      case "curve" => f = (p:Particle)=>{
        p.set( p.lerp(this + vel *.01f, .7f))
        p.vel = vel.lerp( Vec3(-x,-1,-z).normalize, .1f )
      }
      case _ => f = null; println("NO MATCH!!!")
    }
  }

  def step( dt: Float ) = {
    //pos += Particle.randomVec(.01f) //random walk
    this += vel * dt //linear motion
  }

  def onDraw( gl: GL2 ) = {
    gl.glVertex3f( x, y, z )
    //GLDraw.cube( pos, .01f, false, c )(gl)
  }

}

object Particle {
  val rand = util.Random
  
  def apply() = new Particle( randomVec(1.f), randomVec(1.f) );
  def randomVec(s: Float) : Vec3 = (new Vec3( rand.nextFloat * 2 - 1, rand.nextFloat * 2 - 1, rand.nextFloat * 2 - 1)).normalize * s
}

object ParticleCollector extends GLAnimatable {

  var pointSize = 5.f
  var showField = false
  var changeSpeed = .01f;
  var thresh = .01f;
  var rot = 0.f
  var rotate = false;

  var particles = generateParticles(5000)
  var collection = new ListBuffer[Particle]()
  var octree = Octree( Vec3(0), 2.f)

  val n = 10
  var field = new VecField3D( n )
  for( z<-(0 until n); y<-(0 until n); x<-(0 until n)){
    val cen = field.centerOfBin(x,y,z).normalize
    //field.set(x,y,z, Vec3(0) )
    //field.set(x,y,z, cen * -.1f)
    //field.set(x,y,z, Vec3(x,y,z).normalize * .1f)
    //field.set(x,y,z, Vec3( -cen.z + -cen.x*.1f, -cen.y, cen.x ).normalize * .1f )
    //field.set(x,y,z, Vec3( math.sin(cen.x*3.14f), 0, math.cos(cen.z*3.14f) ).normalize * .1f)  
    //field.set(x,y,z, Vec3( cen.x, y/10.f, cen.z).normalize * .1f )
    //field.set(x,y,z, Vec3(0,.1f,0) )
    field.set(x,y,z, (Vec3(0,1,0)-cen).normalize * .1f )
  }


  def generateParticles( n: Int): ListBuffer[Particle] = {
    var c = n
    val l = new ListBuffer[Particle]()
    while( c > 0 ){
      val p = Particle()
      l += p
      c -= 1
    }
    l
  }

  def insertPoint( p:Particle ) = { collection += p; octree.insertPoint(p) }
  def seedLine( from:Vec3, to:Vec3, d:Float = .01f ) = {

    val dir = to - from
    var dist = dir.mag
    val r = new Ray( from, dir.normalize )
    var p:Particle = null
    while( dist >= 0.f ){
      val v = r(dist)
      val p = new Particle(v, (v*Vec3(1,0,1)).normalize )
      insertPoint(p)
      dist -= d
    }

  }
  def seedPlane( from:Vec3, to:Vec3, d:Float = .01f ) = {

    val dirx = (to-from)*Vec3(1,0,0)
    val dirz = (to-from)*Vec3(0,0,1)
    var distz = dirz.mag
    val rz = new Ray( from, dirz.normalize )
    while( distz >= 0.f ){
      var rx = new Ray( rz(distz), dirx.normalize )
      var distx = dirx.mag
      while( distx >= 0.f ){
        val v = rx(distx)
        val p = new Particle(v, (v+Vec3(0,.05,0)).normalize )
        insertPoint(p)
        distx -= d
      }
      distz -= d
    }

  }




  override def step( dt: Float )= {

    if( rotate ) rot += 2.f
    if( rot > 180.f) rot = -180.f

    
    val count = collection.size
    particles.foreach( ( p : Particle) => {

      p.vel += field(p)
      //p.vel = p.vel.lerp( field( p ), changeSpeed );
      p.step(dt)

      val points = octree.getPointsInSphere( p, thresh )
      if( points != null ){
        points.headOption match {
          case Some(q:Particle) => insertPoint(p); particles -= p;
            if( collection.size < 100000 ){
              val newp = Particle()
              //newp.setAlignment("line")
              particles += newp
            }
            if(q.f != null) q.f(p)
          case None => None
        }
      }

      if( p.mag > 1.1f ){
        particles -= p;
        particles += Particle()
      }
      
    })
    if( count != collection.size ) println ("Particles Collected: " + collection.size )
   

  }

  override def onDraw( gl: GL2 ) = {

    gl.glDisable(L.GL_LIGHTING)
    gl.glEnable(GL3.GL_PROGRAM_POINT_SIZE)
    gl.glEnable (GL.GL_BLEND);
    gl.glBlendFunc (GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

    gl.glRotatef(rot,0.f,1.f,0.f)

    gl.glPointSize( pointSize )
    gl.glColor4f( 0.f, 1.f, 0.f, .2f)
    gl.glBegin( GL.GL_POINTS )
      particles.foreach( _.onDraw( gl ) )
      gl.glColor4f( 0.f, 0.f, 1.f, .2f)
      collection.foreach( _.onDraw( gl ) )
    gl.glEnd
    
    if(showField) field.onDraw(gl)

    gl.glDisable(GL3.GL_PROGRAM_POINT_SIZE)
    gl.glEnable(L.GL_LIGHTING)
  }
  
  def writePoints( file: String ) = {
    
    //val out = new java.io.FileWriter( file )
    val fs = new java.io.FileOutputStream(file);
    val ds = new java.io.DataOutputStream(fs);
    collection.foreach( (p) => {
      ds.writeFloat(p.x)
      ds.writeFloat(p.y)
      ds.writeFloat(p.z)
    })
    ds.flush
    ds.close
  }
  def writeOrientedPoints( file: String ) = {
    
    val out = new java.io.FileWriter( file )

    collection.foreach( (p) => {
      val s = .005f
      val x = p.x; val y = p.y; val z = p.z;

      out.write( x + " " + y + " " + (z+s) + " 0 0 1\n" )
      out.write( x + " " + y + " " + (z-s) + " 0 0 -1\n" )
      out.write( x + " " + (y+s) + " " + z + " 0 1 0\n" )
      out.write( x + " " + (y-s) + " " + z + " 0 -1 0\n" )
      out.write( (x+s) + " " + y + " " + z + " 1 0 0\n" )
      out.write( (x-s) + " " + y + " " + z + " -1 0 0\n" )

    })

    out.close
  }
  
  def writePoints2D( file: String) = {
    
    val out = new java.io.FileWriter( file )  
    
    var r=0.f
    var list = List[Particle]()

    while( r < 2 * math.Pi ){
      val tx = math.cos( r ).toFloat
      val tz = math.sin( r ).toFloat
      collection.foreach( (p) => {
        list = new Particle( Vec3( tx*p.x, p.y, tz*p.x ), Vec3(0) ) :: list
      })
      r += .5f;
    }

    
    list.foreach( (p) => {
      val s = .01f
      val x = p.x; val y = p.y; val z = p.z;

      out.write( x + " " + y + " " + (z+s) + " 0 0 1\n" )
      out.write( x + " " + y + " " + (z-s) + " 0 0 -1\n" )
      out.write( x + " " + (y+s) + " " + z + " 0 1 0\n" )
      out.write( x + " " + (y-s) + " " + z + " 0 -1 0\n" )
      out.write( (x+s) + " " + y + " " + z + " 1 0 0\n" )
      out.write( (x-s) + " " + y + " " + z + " -1 0 0\n" )

    })

    out.close
  }

}

