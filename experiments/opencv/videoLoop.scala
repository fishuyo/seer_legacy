package com.fishuyo.seer
package examples.opencv.loop

import graphics._
import io._
import maths._
import particle._
import dynamic._
import cv._
import video._
import audio._
import util._

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils._

import org.opencv.core._
import org.opencv.highgui._
import org.opencv.imgproc._

object Main extends App with Animatable{

  DesktopApp.loadLibs()
  System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)
  Scene.push(this)

	var capture: VideoCapture = _
  var bgsub = new BackgroundSubtract
  var blob = new BlobTracker
	var loop = new VideoLoop
  var subRect:Rect = _
  var dirty = false

  var subtract = false
  def setSubtract(v:Boolean) = subtract = v
  var output = "loop"
  def setOutput(s:String) = output = s

	var bytes:Array[Byte] = null
  var (w,ww,h,hh) = (0.0,0.0,0.0,0.0)

  val s = new SpringMesh( Sphere.generateMesh(),1.f ) //Plane.generateMesh(2,2,10,10), 1.f) //Sphere()
  s.particles.take(s.particles.length/2).foreach( (p) => s.pins += AbsoluteConstraint(p, p.position))
  // s.particles.takeRight(10).foreach( (p) => s.pins += AbsoluteConstraint(p, p.position))
  // val cube = Model(s)

  val cube = Model(Cube())
  // cube.color.set(1,0,0,1)
  // cube.material = new BasicMaterial
  cube.material.textureMix = 1.f
  Scene.push(cube)

  var pix:Pixmap = null
  var tId = 0
  
  val live = new Ruby("videoLoop.rb")

  val audioLoop = new Loop(10.f)

  Audio.push(audioLoop)

  DesktopApp()  

  override def init(){
    capture = new VideoCapture(0)

    Thread.sleep(2000)

    w = capture.get(Highgui.CV_CAP_PROP_FRAME_WIDTH)
    h = capture.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT)
    ww=w
    hh=h

    println( s"starting capture w: $w $h")

    subRect = new Rect(0,0,w.toInt,h.toInt)

    pix = new Pixmap(w.toInt/2,h.toInt/2, Pixmap.Format.RGB888)
    bytes = new Array[Byte](h.toInt/2*w.toInt/2*3)
    cube.scale.set(1.f, (h/w).toFloat, 1.f)
  	// cube.translate(0,0,-1.f) //.set(1.f, (h/w).toFloat, 1.f)

    // SceneGraph.root.camera = new OrthographicCamera(800,800)

  	tId = Texture(pix)
  }

  def resizeC(x1:Float,y1:Float, x2:Float, y2:Float){
    implicit def f2i(f:Float) = f.toInt
    val c = clamper(0.f,1.f)_
    val (l,r) = (if(x1>x2) (c(x2),c(x1)) else (c(x1),c(x2)) )
    val (t,b) = (if(y1>y2) (c(y2),c(y1)) else (c(y1),c(y2)) )
    println(s"resize: ${l*w} ${t*h} ${(r-l)*w} ${(b-t)*h}")
    resize( l*w, t*h, (r-l)*w, (b-t)*h )
  }
  
  def resize(x:Int, y:Int, width:Int, height:Int){
    w = width.toDouble
    h = height.toDouble
    subRect = new Rect(x,y,width,height)
    loop.clear()
    dirty = true
  }

  def resizeFull(){
    resize(0,0,ww.toInt,hh.toInt)
  }

  override def draw(){
    Shader.lightingMix = 0.f
  	Shader.textureMix = 1.f
  	Texture.bind(0)
  	cube.draw()
  }

  override def animate(dt:Float){
 
    s.animate(dt)

    if( dirty ){  // resize everything if using sub image
      pix = new Pixmap(w.toInt/2,h.toInt/2, Pixmap.Format.RGB888)
      bytes = new Array[Byte](h.toInt/2*w.toInt/2*3)
      cube.scale.set(1.f, (h/w).toFloat, 1.f)
      Texture.update(0, pix) 
    }

  	val img = new Mat()
  	val read = capture.read(img)  // read from camera

    if( !read ) return

    val subImg = new Mat(img, subRect )   // take sub image

    val rsmall = new Mat()
  	val small = new Mat()

  	Imgproc.resize(subImg,small, new Size(), 0.5,0.5,0)   // scale down
    Core.flip(small,rsmall,1)   // flip so mirrored
    Imgproc.cvtColor(rsmall,small, Imgproc.COLOR_BGR2RGB)   // convert to rgb

    var sub = small
    if( subtract ){  // do bgsubtraction and blob masking
      sub = bgsub(small)

      // val diff = bgsub(small, true)
      // blob(diff)
      // sub = new Mat()
      // small.copyTo(sub, blob.mask)
    }

  	var out = new Mat()
  	loop.videoIO( sub, out)  // pass frame to loop get next output
    if( out.empty()) return

    if( subtract ){  // if subtracting copy background to blank pixels
      val bgmask = new Mat()
      Core.compare(out, new Scalar(0.0), bgmask, Core.CMP_EQ)
      bgsub.bg.copyTo(out,bgmask)
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
    } catch{ case e:Exception => println(e); println(w+ " " +h); println( bytes.length); println(bb.capacity) }

    // update texture from pixmap
		Texture(0).draw(pix,0,0)

    live.animate(dt)

    img.release
    subImg.release
    small.release
    rsmall.release
    out.release
  }

}



