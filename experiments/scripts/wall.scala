
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import io._
import cv._
import video._
import util._
import kinect._
import actor._
import trees._

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils._
import com.badlogic.gdx.graphics.{Texture => GdxTexture}

import org.opencv.core._
import org.opencv.highgui._
import org.opencv.imgproc._

import akka.actor._
import akka.event.Logging

import de.sciss.osc._
 
Shader.bg.set(0,0,0,1)

object Script extends SeerScript {

  var dirty = true
  var update = false

  var bytes:Array[Byte] = null
	var (w,h) = (660,490)

  var subRect = new Rect(0,0,w,h)

  var pix:Pixmap = null
  var texture:GdxTexture = null

  val lquad = Plane().translate(-3.6,0.1,0)
  val rquad = Plane().translate(-.7,0,0)
  lquad.material = Material.basic
  rquad.material = Material.basic
  lquad.material.color = RGB.black
  rquad.material.color = RGB.black
  lquad.material.textureMix = 1.f
  rquad.material.textureMix = 1.f

  var receiver:ActorRef = _

  val tree = Tree()
  tree.root.pose.pos.set(0,-2,-4)
  tree.branch()

  val depth = 7
  tree.setAnimate(true)
  tree.setReseed(true)
  tree.setDepth(depth)
  tree.branch(depth)

	override def onLoad(){
	}

	override def draw(){
    lquad.draw
    rquad.draw
    // tree.draw
	}

	override def onUnload(){
    // if( texture != null) texture.dispose
    receiver ! akka.actor.PoisonPill
	}

  
  override def animate(dt:Float){
    if( receiver == null) receiver = system.actorOf(Props(new RecvActor ), name = "puddle")

    if( dirty ){  // resize everything if using sub image
      pix = new Pixmap(w,h, Pixmap.Format.RGB888)
      bytes = new Array[Byte](w*h*3)
      val s1 = Vec3(1.f,-(h/w.toFloat), 1.f)
      val s2 = Vec3(-1.f,-(h/w.toFloat), 1.f)
      lquad.scale.set(s1)
      rquad.scale.set(s2)
      if(texture != null) texture.dispose
      texture = new GdxTexture(pix)
      lquad.material.texture = Some(texture) 
      rquad.material.texture = Some(texture) 
      dirty = false
    }

    if(update){
      try{
        val bb = pix.getPixels()
        bb.put(bytes)
        bb.rewind()
      } catch { case e:Exception => "Error updating: probably size mismatch!"}

      // update texture from pixmap
      texture.draw(pix,0,0)
      update = false
      receiver ! "free"
    }

    tree.animate(dt)

  }


}


// recv byte array from floor machine
class RecvActor extends Actor with akka.actor.ActorLogging {
  var busy = false

  override def preStart() = {
    log.debug("Starting")
  }
  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.error(reason, "Restarting due to [{}] when processing [{}]",
      reason.getMessage, message.getOrElse(""))
  }

  def receive = {
    case "free" => busy = false
    case msg if !busy =>
      msg match{
        case b:Array[Byte] =>
          busy = true
          Script.bytes = b
          Script.update = true
      }
    case ("resize",w:Int,h:Int) =>
      log.error("resize")
      Script.w = w
      Script.h = h
      Script.dirty = true
    case _ => ()
  }
}

// input events
ScreenCaptureKey.use()
Keyboard.clear()
Keyboard.use()

Mouse.clear()
Mouse.use()
Mouse.bind("drag", (i)=>{
  val x = i(0) / (1.0*Window.width)
  val y = i(1) / (1.0*Window.height)
  val speed = (400 - i(1)) / 100.0
  val decay = (i(0) - 400) / 100.0

})

OSC.disconnect
OSC.clear()
OSC.listen(8008)
OSC.bindp {
  case Message("/gnarl/pose", x:Float,y:Float,z:Float,qx:Float,qy:Float,qz:Float,w:Float) =>
      val q = Quat(w,qx,qy,qz).toEulerVec()
      val t = Script.tree
      t.bAngle.y.setMinMax( 0.05, q.y,false )
      t.sAngle.x.setMinMax( 0.05, q.x, false )
      t.bAngle.x.setMinMax( 0.05, q.x, false )
      t.sAngle.z.setMinMax( 0.05, q.z, false )
      t.bAngle.z.setMinMax( 0.05, q.z, false )
      t.refresh()
  
      val p = Vec3(x,y,z)
      Script.lquad.material.textureMix = -z
      Script.rquad.material.textureMix = -z
  case Message("/test",f:Float) => println(s"test: $f")
  case _ => ()
}

// Trackpad.bind( (i,f) => {
//   val t = Script.tree

//   i match {
//     case 1 =>
//       val ur = Vec3(1,0,0) //Camera.nav.ur()
//       val uf = Vec3(0,0,1) //Camera.nav.uf()

//       t.root.applyForce( ur*(f(0)-0.5) * 2.0*f(4) )
//       t.root.applyForce( uf*(f(1)-0.5) * -2.0*f(4) )
//     case 2 =>
//       mx += f(2)*0.05  
//       my += f(3)*0.05
//     case 3 =>
//       ry = ry + f(2)*0.05  
//       mz = mz + f(3)*0.01
//       if (mz < 0.08) mz = 0.08
//       if (mz > 3.0) mz = 3.0 
//     case 4 =>
//       rz = rz + f(3)*0.05
//       rx = rx + f(2)*0.05
//     case _ => ()
//   }

//   // t.root.pose.pos.set(mx,my,0)

//   if(i > 2){
//     t.bAngle.y.setMinMax( 0.05, ry,false )
//     // ##t.bAngle.y.set(mx)
//       t.sRatio.setMinMax( 0.05, mz, false )
//       // ## t.sRatio.set( mz )
//       t.bRatio.setMinMax( 0.05, mz, false )
//       // ##t.bRatio.set( my )
//       t.sAngle.x.setMinMax( 0.05, rx, false )
//       t.bAngle.x.setMinMax( 0.05, rx, false )
//       // ##t.sAngle.x.set( rx )
//       t.sAngle.z.setMinMax( 0.05, rz, false )
//       t.bAngle.z.setMinMax( 0.05, rz, false )
//       // ##t.sAngle.z.set( rz )
//       // ##t.branch(depth)
//       t.refresh()

//       // # t.root.accel.zero
//       // # t.root.euler.zero
//   }
// })



// must return this from script
Script