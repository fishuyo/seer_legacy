
package com.fishuyo.seer
package examples.opencv

import graphics._
import io._
import spatial._
import particle._
import dynamic._
import cv._
import video._
import audio._
import util._

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.{ Texture => GdxTexture }
import com.badlogic.gdx.graphics.glutils._

import org.opencv.core._
import org.opencv.highgui._
import org.opencv.imgproc._

import concurrent.duration._

object VideoLooper extends SeerApp {

  OpenCV.loadLibrary()
  
  var capture: VideoCapture = _

  var bgsub = new BackgroundSubtract
  var blob = new BlobTracker
  var loop = new VideoLoop
  
  var bg = false
  var subtract = false
  var output = "loop"

  var bytes:Array[Byte] = null
  var (w,h) = (0.0,0.0)

  val quad = Plane()

  var pix:Pixmap = null
  var texture:GdxTexture = null
  var inited = false

  printHelp()

  // val audioLoop = new Loop(10.f)
  // Audio().push(audioLoop)

  override def init(){

    capture = new VideoCapture(0)

    Thread.sleep(1000)
    if(!capture.isOpened()) println("Capture device failed to open.")

    w = capture.get(Highgui.CV_CAP_PROP_FRAME_WIDTH)
    h = capture.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT)
    println( s"dim: $w x $h")

    pix = new Pixmap(w.toInt/2,h.toInt/2, Pixmap.Format.RGB888)
    bytes = new Array[Byte](h.toInt/2*w.toInt/2*3)

    quad.scale.set(-1f, (-h/w).toFloat, 1f)

    texture = new GdxTexture(pix) 

    quad.material = Material.basic
    quad.material.texture = Some(texture)
    quad.material.textureMix = 1f
    inited = true

    // record inital loop and start stacking
    Schedule.after(1 second){
      loop.toggleRecord()
      loop.setAlphaBeta(0.25,0.89)
      Schedule.after(1 second){
        loop.toggleRecord()
        loop.stack()
      }
    }
  }

  override def draw(){
    quad.draw()
  }

  override def animate(dt:Float){
 
    if(!inited) init()

    val img = new Mat()
    val read = capture.read(img)  // read from camera

    if( !read ) return

    val small = new Mat()
    val rgb = new Mat()

    Imgproc.resize(img,small, new Size(), 0.5,0.5,0)   // scale down
    // Core.flip(small,rsmall,1)   // flip so mirrored
    Imgproc.cvtColor(small,rgb, Imgproc.COLOR_BGR2RGB)   // convert to rgb

    var sub = rgb
    if( subtract ){       // do bgsubtraction and blob masking
      sub = bgsub(rgb)

      // val diff = bgsub(small, true)
      // blob(diff)
      // sub = new Mat()
      // small.copyTo(sub, blob.mask)
    }

    var out = new Mat()
    loop.videoIO( sub, out)  // pass frame to loop get next output
    if( out.empty()) return

    if( bg ){  // if subtracting copy background to blank pixels
      val bgmask = new Mat()
      Core.compare(out, new Scalar(0.0), bgmask, Core.CMP_EQ)
      bgsub.bg.copyTo(out,bgmask)
      bgmask.release
    }

    output match {
      case "live" => out = small
      case "loop" => ()
      case "bg" => out = bgsub.bg
      case "sub" => Imgproc.cvtColor(bgsub.mask, out, Imgproc.COLOR_GRAY2RGB)
      case "blob" => Imgproc.cvtColor(blob.mask, out, Imgproc.COLOR_GRAY2RGB)
      case _ => ()
    }

    // copy MAT to pixmap
    out.get(0,0,bytes)  
    val bb = pix.getPixels()
    try{
      bb.put(bytes)
      bb.rewind()
    } catch{ case e:Exception => println(e); }

    // update texture from pixmap
    texture.draw(pix,0,0)

    img.release
    small.release
    rgb.release
    out.release
  }


  Keyboard.bind("r", () => loop.toggleRecord() )
  Keyboard.bind("t", () => loop.togglePlay() )
  Keyboard.bind("x", () => loop.stack() )
  Keyboard.bind("c", () => loop.clear() )
  Keyboard.bind(" ", () => loop.reverse() )
  Keyboard.bind("j", () => loop.setAlphaBeta(1f,.99f) )
  Keyboard.bind("b", () => bg = !bg )
  Keyboard.bind("v", () => subtract = !subtract )
  Keyboard.bind("z", () => bgsub.updateBackgroundNextFrame )

  Keyboard.bind("p", () => com.fishuyo.seer.video.ScreenCapture.toggleRecord )
  Keyboard.bind("o", () => loop.writeToFile("",1.0,"mpeg4") )


  Mouse.bind("drag", (i) => {
    val y = (Window.height - i(1)*1f) / Window.height
    val x = (i(0)*1f) / Window.width
    // # decay = (decay + 4)/8
    // # Loop.loop.setSpeed(speed)
    // loop.setAlphaBeta(decay, speed)
    println(s"$x $y")
    // loop.setAlpha(x)
    loop.setAlphaBeta(x,y)
  })


  def printHelp(){
    println("""
        VideoLooper key bindings:
          r -> toggle Record (appends to video buffer)
          t -> toggle Play
          x -> toggle Stack (enable blended overwriting of video buffer)
          c -> clear buffer
          space -> reverse loop
          v -> toggle bg substraction (record only foreground image)
          z -> update bg image
          Mouse drag x -> alpha blend constant (amount of input image)
          Mouse drag y -> beta blend constant (amount of feedback image)
      """)
  }
}



