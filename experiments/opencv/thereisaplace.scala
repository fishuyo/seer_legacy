package com.fishuyo.seer
package thereisaplace

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
	var loop = new VideoLoop
  var subRect:Rect = _
  var dirty = false

	var bytes:Array[Byte] = null
	var (w,ww,h,hh) = (0.0,0.0,0.0,0.0)

  val cube = Model(Cube())
  Scene.push(cube)

  var pix:Pixmap = null
  var player:VideoPlayer = null
  
  val live = new Ruby("thereisaplace.rb")

  var looper = new Looper
  var l = 0

  Audio.push( looper )

  // val audioLoop = new Loop(10.f)
  // Audio.push(audioLoop)

  var loopNode:TextureNode = null

  DesktopApp()  

  override def init(){
    capture = new VideoCapture(0)

    Thread.sleep(2000)

    w = capture.get(Highgui.CV_CAP_PROP_FRAME_WIDTH)
    h = capture.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT)
    ww = w
    hh = h

    println( s"starting capture w: $w $h")

    subRect = new Rect(0,0,w.toInt,h.toInt)
    // resize(160,0,960,720)

    pix = new Pixmap(w.toInt/2,h.toInt/2, Pixmap.Format.RGB888)
    bytes = new Array[Byte](h.toInt/2*w.toInt/2*3)
  	cube.scale.set(1.f, (h/w).toFloat, 1.f)

    // player = new VideoPlayer("/Users/fishuyo/Desktop/o.mp4")
    player = new VideoPlayer("/Users/fishuyo/projects/Documentation/feedback_puddle/1.mov")


  	Texture(pix) 
  	val id = Texture(player.pixmap) 

  	loopNode = new TextureNode( Texture(0) )
  	val playerNode = new TextureNode( Texture(id) )
  	val compNode = new RenderNode
    compNode.shader = "composite"
    // compNode.clear = false
    val quag = new Drawable {
      val m = Plane.generateMesh() //Mesh(Primitive2D.quad)
      m.texCoords.foreach( (t) => t.y = 1.f-t.y )
      m.update
      override def draw(){
        // Shader("composite").setUniformf("u_blend0", 1.0f)
        // Shader("composite").setUniformf("u_blend1", 1.0f)
        // Shader("composite").setUniformMatrix("u_projectionViewMatrix", new Matrix4())
        m.draw()
      }
    }
    compNode.scene.push( quag )
    loopNode.outputTo(compNode)
    playerNode.outputTo(compNode)
  	SceneGraph.addNode(compNode)

    looper.loops(0).load("Desktop/introS.wav")
  	looper.loops(1).load("Desktop/boop.wav")
  	loop.setAlpha(1.f)
  	live.init()
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
  	// Texture.bind(0)
  	// cube.draw()

  }

  override def animate(dt:Float){

  	player.animate(dt)
  	Texture(1).draw(player.pixmap,0,0)

    if( dirty ){  // resize everything if using sub image
      pix = new Pixmap(w.toInt/2,h.toInt/2, Pixmap.Format.RGB888)
      bytes = new Array[Byte](h.toInt/2*w.toInt/2*3)
      cube.scale.set(1.f, (h/w).toFloat, 1.f)
      Texture.update(0, pix)
      loopNode.texture = Texture(0) 
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

  	var out = new Mat()
  	loop.videoIO( sub, out)  // pass frame to loop get next output
    if( out.empty()) return

    // copy MAT to pixmap
  	out.get(0,0,bytes)
		val bb = pix.getPixels()
		bb.put(bytes)
		bb.rewind()

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



