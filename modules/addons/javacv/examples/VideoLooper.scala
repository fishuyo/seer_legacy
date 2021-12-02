package seer
package video

import graphics._
import io._
import spatial._
// import particle._
// import dynamic._
import cv._
import video._
import audio._
import util._

import scala.collection.mutable.ListBuffer
// import scala.collection.JavaConversions._

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils._

import org.bytedeco.javacv._
import org.bytedeco.opencv.global.opencv_core._
import org.bytedeco.opencv.global.opencv_highgui._
import org.bytedeco.opencv.global.opencv_imgcodecs._
import org.bytedeco.opencv.global.opencv_imgproc._
import org.bytedeco.opencv.global.opencv_videoio._
import org.bytedeco.opencv.opencv_videoio._
import org.bytedeco.opencv.opencv_core._


object VideoLooper extends SeerApp {

  // System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)

	// var capture: VideoCapture = _
	var capture: FFmpegFrameGrabber = _
  var bgsub = new BackgroundSubtract
  // var blob = new BlobTracker
	var loop = new VideoLoop
  var subRect:Rect = _
  var dirty = false

  val converterToMat = new OpenCVFrameConverter.ToMat()


  var subtract = false
  def setSubtract(v:Boolean) = subtract = v
  var output = "loop"
  def setOutput(s:String) = output = s

  var (w,ww,h,hh) = (0.0f,0.0f,0.0f,0.0f)

  // val s = new SpringMesh( Sphere.generateMesh(),1.0f ) //Plane.generateMesh(2,2,10,10), 1.0f) //Sphere()
  // s.particles.take(s.particles.length/2).foreach( (p) => s.pins += AbsoluteConstraint(p, p.position))
  // s.particles.takeRight(10).foreach( (p) => s.pins += AbsoluteConstraint(p, p.position))
  // val cube = Model(s)

  val cube = Cube() //Model(Cube())
  // cube.color.set(1,0,0,1)
  // cube.material = new BasicMaterial
  // cube.material.textureMix = 1.0f
  // Scene.push(cube)

  var pix:Image = _
  var tex:Texture = _
  
  val bytes = new Array[Byte](1920*1080*3)
  // val live = new Ruby("videoLoop.rb")

  val audioLoop = new Loop(10.0f)
  Audio().push(audioLoop)


  override def init(){
    // capture = new VideoCapture(0)
    // capture = FrameGrabber.createDefault(0)
    println("pre capture")
    capture = new FFmpegFrameGrabber("default");
    // capture = new FFmpegFrameGrabber("/Users/fishuyo/Downloads/entrance.mp4");
    // capture.setFormat("mp4");
    capture.setFormat("avfoundation");
    capture.start();
    println("post capture start")


    // Thread.sleep(2000)

    w = capture.getImageWidth() //1080 //capture.get(CAP_PROP_FRAME_WIDTH).toFloat
    h = capture.getImageHeight() //1920 //capture.get(CAP_PROP_FRAME_HEIGHT).toFloat
    ww=w
    hh=h

    println( s"starting capture w: $w $h")

    subRect = new Rect(0,0,w.toInt,h.toInt)

    pix = Image(w.toInt, h.toInt, 3, 1)
    // pix = Image(w.toInt/2,h.toInt/2, 3, 1)
    tex = Texture(pix)
    // pix = new Pixmap(w.toInt/2,h.toInt/2, Pixmap.Format.RGB888)

    cube.scale.set(1.0f, (h/w).toFloat, 1.0f)
  	// cube.translate(0,0,-1.0f) //.set(1.0f, (h/w).toFloat, 1.0f)
    cube.material.loadTexture(tex)
    // SceneGraph.root.camera = new OrthographicCamera(800,800)

  }

  def resizeC(x1:Float,y1:Float, x2:Float, y2:Float){
    implicit def f2i(f:Float) = f.toInt
    val c = clamper(0.0f,1.0f)_
    val (l,r) = (if(x1>x2) (c(x2),c(x1)) else (c(x1),c(x2)) )
    val (t,b) = (if(y1>y2) (c(y2),c(y1)) else (c(y1),c(y2)) )
    println(s"resize: ${l*w} ${t*h} ${(r-l)*w} ${(b-t)*h}")
    resize( l*w, t*h, (r-l)*w, (b-t)*h )
  }
  
  def resize(x:Int, y:Int, width:Int, height:Int){
    w = width.toFloat
    h = height.toFloat
    subRect = new Rect(x,y,width,height)
    loop.clear()
    dirty = true
  }

  def resizeFull(){
    resize(0,0,ww.toInt,hh.toInt)
  }

  override def draw(){
    // Shader.lightingMix = 1.0f
  	// Shader.textureMix = 1.0f
  	// Texture.bind(0)
  	cube.draw()
    // Sphere().draw
  }

  override def animate(dt:Float){
 
    // s.animate(dt)

    if( dirty ){  // resize everything if using sub image
      pix = Image(w.toInt,h.toInt, 3, 1)
      // pix = Image(w.toInt/2,h.toInt/2, 3, 1)
      tex = Texture(pix)
      cube.scale.set(1.0f, (h/w).toFloat, 1.0f)
      // Texture.update(0, pix) 
      cube.material.loadTexture(tex)
    }

    //val read = false //capture.read(img)  // read from camera
    
    val frame = capture.grabFrame()

    if( frame.image == null ) return

  	val img = converterToMat.convertToMat(frame)
    // println(frame.image(0)) //img.data().asBuffer().get(10))
    // println(s"${img.rows()} x ${img.cols()} x ${img.channels()} - ${img.dims()}")
    println(img.getByteBuffer())
    println(tex.byteBuffer)

    // val subImg = new Mat(img, subRect )   // take sub image

    // val rsmall = new Mat()
  	// val small = new Mat()

  	// org.bytedeco.opencv.global.opencv_imgproc.resize(subImg,small, new Size(), 0.5, 0.5, 0)   // scale down
    // flip(small,rsmall,1)   // flip so mirrored
    // flip(small,rsmall,1)   // flip so mirrored
    // cvtColor(rsmall,small, COLOR_BGR2RGB)   // convert to rgb

    // var sub = small
    // if( subtract ){  // do bgsubtraction and blob masking
    //   sub = bgsub(small)

    //   // val diff = bgsub(small, true)
    //   // blob(diff)
    //   // sub = new Mat()
    //   // small.copyTo(sub, blob.mask)
    // }

  	// var out = new Mat()
  	// loop.videoIO( sub, out)  // pass frame to loop get next output
    // if( out.empty()) return

    // if( subtract ){  // if subtracting copy background to blank pixels
    //   val bgmask = new Mat()
    //   compare(out, new Mat(1, 1, CV_32SC1, new Scalar(0.0)), bgmask, CMP_EQ)
    //   compare(out, new Mat(1, 1, CV_32SC1, new Scalar(0.0)), bgmask, CMP_EQ)
    //   compare(out, new Mat(1, 1, CV_32SC1, new Scalar(0.0)), bgmask, CMP_EQ)
    //   bgsub.bg.copyTo(out,bgmask)
    // }

    // output match {
    //   case "live" => out = small
    //   case "loop" => ()
    //   case "bg" => out = bgsub.bg
    //   case "sub" => cvtColor(bgsub.mask, out, COLOR_GRAY2RGB)
    //   // case "blob" => cvtColor(blob.mask, out, COLOR_GRAY2RGB)
    //   case _ => ()
    // }

    try{
      // img.get(0,0, bytes)
      // tex.byteBuffer.put(bytes)
      // tex.byteBuffer.put(frame.image(0).asInstanceOf[java.nio.ByteBuffer])
      // tex.byteBuffer.put(img.data().asBuffer())
      val bb = img.getByteBuffer()
      bb.limit(tex.byteBuffer.limit())
  		tex.byteBuffer.put(bb)
      tex.update()
      
    } catch{ case e:Exception => println(e); println(w+ " " +h); }

    // update texture from pixmap
		// Texture(0).draw(pix,0,0)

    // live.animate(dt)

    img.release
    // subImg.release
    // small.release
    // rsmall.release
    // out.release
  }


  Keyboard.clear
  Keyboard.listen{
  case 'r' => loop.toggleRecord() 
  case 't' => loop.togglePlay() 
  case 'x' => loop.stack() 
  case 'c' => loop.clear() 
  case ' ' => loop.reverse() 
  case 'j' => loop.setAlphaBeta(1f,.99f) 
  // case 'b' => bg = !bg 
  case 'v' => subtract = !subtract 
  // case 'z' => depth = !depth 

  // case 'p' => com.fishuyo.seer.video.ScreenCapture.toggleRecord 
  // case 'o' => loop.writeToFile("",1.0,"mpeg4") 
  }

  // Mouse.clear()
  // Mouse.use()
  // Mouse.bind("drag", (i) => {
  //   val y = (Window.height - i(1)*1f) / Window.height
  //   val x = (i(0)*1f) / Window.width
  //   // # decay = (decay + 4)/8
  //   // # Loop.loop.setSpeed(speed)
  //   // loop.setAlphaBeta(decay, speed)
  //   println(s"$x $y")
  //   // loop.setAlpha(x)
  //   loop.setAlphaBeta(x,y)
  // })
}



