
package com.fishuyo
package trans.dronefabric
import maths._
import graphics._
import spatial._
import trees._

import de.sciss.osc._
import java.awt.event._
import javax.media.opengl._

object Main extends App{
  
  var d = 2.3f
  var cd = -1.3f
  var w = 4.f
  var h = 3f
  var dd = .1f

  val track = new TransTrack

  //var trees = TreeNode( Vec3(0), .1f ) :: TreeNode( Vec3(3.f,0,0), .3f ) :: TreeNode( Vec3( 6.f,0,0), .3f) :: TreeNode( Vec3(9.f,0,0), .1f) :: List()
  var f = Fabric( Vec3(d,0,0), w,h,dd,"yz") :: Fabric( Vec3(0,0,d),w,h,dd) :: Fabric( Vec3(-d,0,0),w,h,dd,"yz") :: List()
  //f.foreach( _.addField(DroneWindField) )

  //trees(0).branch( 6, 45.f, .8f, 0)
  //trees(1).branch( 5, 10.f, .9f, 0)
  //trees(2).branch( 10, 20.f, .5f, 0)
  //trees(3).branch( 8, 20.f, .5f, 0)

  //build scene by pushing objects to singleton GLScene (GLRenderWindow renders it by default)
  //trees.foreach( t => GLScene.push( t ) )
  //fabrics.foreach( f => GLScene.push( f ) 
  val window = new GLRenderWindow( new GLScene, new Camera( Vec3(cd,0,0), 90.f) )
  window.scene.push(f(0))
  val window2 = new GLRenderWindow( new GLScene, new Camera( Vec3(0,0,cd), -180.f) )
  window2.scene.push(f(1))
  window2.scene.push(DroneWindField)
  val window3 = new GLRenderWindow( new GLScene, new Camera( Vec3(-cd,0,0), -90.f) )
  window3.scene.push(f(2))
  window.addKeyMouseListener( Input )
  window2.addKeyMouseListener( Input )
  window3.addKeyMouseListener( Input )


}

object Input extends io.KeyMouseListener {

  var g = true

  override def keyPressed( e: KeyEvent ){
    val k = e.getKeyCode
    k match {
      case KeyEvent.VK_G => 
        g = !g
        if(g) Fabric.g = -10.f else Fabric.g = 0.f
        println( "Gravity: " + Fabric.g )

      case _ => null
    }
  }
}

object DroneWindField extends VecField3D(10,Vec3(.05f,1.3f,-.05f),3f) {
  var go = false
  var visible = false
  var dronePos = Vec3(0)

  for( i <- (0 until n*n*n)) this.set(i, Vec3(0))

  def updateDrone( p: Vec3 ):Any ={
    clear
    dronePos = p
    var bin = Vec3(0)
    binAt(p) match {
      case Some(v:Vec3) => bin = v
      case None => return null
    }

    var x = bin.x.toInt;
    var z = bin.z.toInt;
    var y = bin.y.toInt;
    while( y > 0 ){
      val d = 1.f - (y*1.f / bin.y)
      val r = Vec3(util.Random.nextFloat*.2f -.1f,util.Random.nextFloat*.2f - .1f,util.Random.nextFloat*.2f -.1f)
      set( x,y,z, (Vec3(0,-1,0)+r) )
      sset( x-1,y,z, (Vec3(-.1f,-.8f,0)+r).lerp( Vec3(-1,.2,0)+r,d) )
      sset( x+1,y,z, (Vec3(.1f,-.8f,0)+r).lerp( Vec3(1,.2,0)+r,d) )
      sset( x,y,z-1, (Vec3(0,-.8f,-.1f)+r).lerp( Vec3(0,.2,-1)+r,d) )
      sset( x,y,z+1, (Vec3(0,-.8f,.1f)+r).lerp( Vec3(0,.2,1)+r,d) )
      sset( x-1,y,z-1, (Vec3(-.1f,-.5f,-.1f)+r).lerp( Vec3(-.8,.2,-.8)+r,d) )
      sset( x+1,y,z-1, (Vec3(.1f,-.5f,-.1f)+r).lerp( Vec3(.8,.2,-.8)+r,d) )
      sset( x-1,y,z+1, (Vec3(-.1f,-.5f,.1f)+r).lerp( Vec3(-.8,.2,.8)+r,d) )
      sset( x+1,y,z+1, (Vec3(.1f,-.5f,.1f)+r).lerp( Vec3(.8,.2,.8)+r,d) )
      sset( x-2,y,z, (Vec3(-.3f,-.3f,0)+r).lerp( Vec3(-.5,.2,0)+r,d) )
      sset( x+2,y,z, (Vec3(.3f,-.3f,0)+r).lerp( Vec3(.5,.2,0)+r,d) )
      sset( x,y,z-2, (Vec3(0,-.3f,-.3f)+r).lerp( Vec3(0,.2,-.5)+r,d) )
      sset( x,y,z+2, (Vec3(0,-.3f,.3f)+r).lerp( Vec3(0,.2,.5)+r,d) )
      y -= 1
    }
    set( x,0,z, Vec3(0,10.f,0) )
    sset( x-1,0,z, Vec3(-1f,7f,0) )
    sset( x+1,0,z, Vec3(1f,7f,0) )
    sset( x,0,z-1, Vec3(0,7f,-1f) )
    sset( x,0,z+1, Vec3(0,7f,1f) )
    sset( x-1,0,z-1, Vec3(-1f,4f,-1f) )
    sset( x+1,0,z-1, Vec3(1f,4f,-1f) )
    sset( x-1,0,z+1, Vec3(-1f,4f,1f) )
    sset( x+1,0,z+1, Vec3(1f,4f,1f) )
    sset( x-2,0,z, Vec3(-1f,2f,0) )
    sset( x+2,0,z, Vec3(1f,2f,0) )
    sset( x,0,z-2, Vec3(0,2f,-1f) )
    sset( x,0,z+2, Vec3(0,2f,1f) )

  }

  override def onDraw(gl: GL2 ) = if(visible) super.onDraw(gl) 

  override def step( dt: Float ) = {
    if( go ){
      for( i <- (0 until 3) ){
        val f = Main.f(i)
        f.particles.foreach( (p:VParticle) => {
          p.applyForce( this(p.pos)* (util.Random.nextFloat*30.f+30.f) )
        })
      }
    }
  }
}

class TransTrack {

  val d2r = math.Pi/180.f
  val cfg         = UDP.Config()
  cfg.localPort   = 10000  // 0x53 0x4F or 'SO'
  val rcv         = UDP.Receiver( cfg )
  val sync = new AnyRef
  
  println( "TransTrack listening on UDP " + cfg.localPort )

  //rcv.dump( Dump.Both )
  rcv.action = {
    case (Message( "/drone/setposh", x:Float,y:Float,z:Float,w:Float, _ @ _* ), _) =>
      DroneWindField.updateDrone( Vec3(x,y,z) )
    case (Message( "/on", _ @ _* ), _) => DroneWindField.go = true 
    case (Message( "/off", _ @ _* ), _) => DroneWindField.go = false
  }

  rcv.connect()
  //sync.synchronized( sync.wait() )
 
}

