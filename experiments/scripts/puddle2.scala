
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
 
Shader.bg.set(1,0,0,1)

object Script extends SeerScript {

  // OpenCV.loadLibrary()

  var loop = new VideoLoop
  var dirty = true
  var update = false

	var bytes:Array[Byte] = null
	var (w,ww,h,hh) = (100.0,0.0,100.0,0.0)

  ww = w
  hh = h
  println( s"starting capture w: $w $h")

  var subRect = new Rect(0,0,w.toInt,h.toInt)

  var pix:Pixmap = null
  var texture:GdxTexture = null
  var loopNode:TextureNode = null

  val quad = Plane()
  quad.material = Material.basic

  var scale = 1.
  // resize(0,0,1280,720)
  // resize(100,120,480,340)

	override def onLoad(){
	}

	override def draw(){
    // quad.draw
	}

	override def onUnload(){
    // if( texture != null) texture.dispose
    loop.clear
    ScreenNode.inputs.clear
    SceneGraph.removeNode(loopNode)
	}

  def resizeC(x1:Float,y1:Float, x2:Float, y2:Float){
    implicit def f2i(f:Float) = f.toInt
    val c = clamper(0.f,1.f)_
    val (l,r) = (if(x1>x2) (c(x2),c(x1)) else (c(x1),c(x2)) )
    val (t,b) = (if(y1>y2) (c(y2),c(y1)) else (c(y1),c(y2)) )
    // println(s"resizeC: ${l*w} ${t*h} ${(r-l)*w} ${(b-t)*h}")
    resize( l*w, t*h, (r-l)*ww, (b-t)*hh )
  }
  
  def resize(x:Int, y:Int, width:Int, height:Int){
    var wid = width
    var hit = height
    if(x+wid > ww) wid = ww.toInt-x
    if( wid % 2 == 1) wid -= 1

    if(y+hit > hh) hit = hh.toInt-y
    w = wid.toDouble
    h = hit.toDouble
    println(s"resize: ${x} ${y} ${wid} ${hit}")
    loop.clear()
    dirty = true
    subRect = new Rect(x,y,wid,hit)
  }

  def resizeFull(){
    resize(0,0,ww.toInt,hh.toInt)
  }

  override def animate(dt:Float){
    if( pix == null){
      loopNode = new TextureNode( texture )
      loopNode.outputTo(ScreenNode)
      SceneGraph.addNode(loopNode)
      ScreenNode.scene.clear
      ScreenNode.scene.push(Plane().scale(-1,-1,1))
    }
    if( dirty ){  // resize everything if using sub image
      pix = new Pixmap((w*scale).toInt,(h*scale).toInt, Pixmap.Format.RGB888)
      bytes = new Array[Byte]((h.toInt*scale*w.toInt*scale*3).toInt)
      // quad.scale.set(1.f, (h/w).toFloat, 1.f)
      if(texture != null) texture.dispose
      texture = new GdxTexture(pix) 
      loopNode.texture = texture 
      dirty = false
    }

    if(update){
      val bb = pix.getPixels()
      bb.put(bytes)
      bb.rewind()

      // update texture from pixmap
      texture.draw(pix,0,0)
      update = false
      myactor ! "free"
    }
  //   // copy MAT to pixmap
  // 	out.get(0,0,bytes)
		// 

  }

  val myactor = system.actorOf(Props(new SActor), name = "qq")

}

class SActor extends Actor with akka.actor.ActorLogging {
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
    case _ => ()
  }
}

ScreenCaptureKey.use()
Keyboard.clear()
Keyboard.use()
Keyboard.bind("r",()=>{ Script.loop.toggleRecord() })
Keyboard.bind("c",()=>{ Script.loop.stop(); Script.loop.clear() })
Keyboard.bind("x",()=>{ Script.loop.stack() })
Keyboard.bind("t",()=>{ Script.loop.togglePlay() })
Keyboard.bind("v",()=>{ Script.loop.reverse() })
Keyboard.bind("z",()=>{ Script.loop.rewind() })
Keyboard.bind("1",()=>{ Script.resizeFull();})

var x = 0
var y = 0
var w = Script.w.toInt
var h = Script.h.toInt
Keyboard.bind("y",()=>{ x -= 10; Script.resize(x,y,w,h);})
Keyboard.bind("u",()=>{ x += 10;  Script.resize(x,y,w,h);})
Keyboard.bind("h",()=>{ w -= 10; Script.resize(x,y,w,h);})
Keyboard.bind("j",()=>{ w += 10;  Script.resize(x,y,w,h);})
Keyboard.bind("i",()=>{ y -= 10; Script.resize(x,y,w,h);})
Keyboard.bind("k",()=>{ y += 10;  Script.resize(x,y,w,h);})
Keyboard.bind("o",()=>{ h -= 10; Script.resize(x,y,w,h);})
Keyboard.bind("l",()=>{ h += 10;  Script.resize(x,y,w,h);})

Mouse.clear()
Mouse.use()
Mouse.bind("drag", (i)=>{
  val x = i(0) / (1.0*Window.width)
  val y = i(1) / (1.0*Window.height)
  val speed = (400 - i(1)) / 100.0
  val decay = (i(0) - 400) / 100.0

  // # Main.loop.setSpeed(1.0) #speed)
  // # Main.loop.setAlphaBeta(decay, speed)
  Script.loop.setAlpha(decay)
})

// var l=0.0
// var t=0.0
// Mouse.bind("down", (i)=>{
//   l = i(0) / (1.0*Window.width)
//   t = i(1) / (1.0*Window.height)
// })
// Mouse.bind("up", (i)=>{
//   val x = i(0) / (1.0*Window.width)
//   val y = i(1) / (1.0*Window.height)
//   Script.resizeC(l,t,x,y)
// })


Script




