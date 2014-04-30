package com.fishuyo.seer
package examples.kinect

import graphics._
import io._
import io.kinect._
import maths._
import dynamic._
import audio._
import cv._

import scala.collection.mutable.ListBuffer

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils._

import org.opencv.core._
import org.opencv.highgui._
import org.opencv.imgproc._

object Loop extends App with Animatable{

  DesktopApp.loadLibs()
  System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)
  Scene.push(this)

  var bgsub = new BackgroundSubtract
  var blob = new BlobTracker
	var loop = new VideoLoop

	var subtract = true
  def setSubtract(v:Boolean) = subtract = v

  val cube = Model(Cube())
  cube.scale.set(1.f, (2*480.f)/640.f, 1.f)
  Scene.push(cube)

  val bigpix = new Pixmap(640,2*480, Pixmap.Format.RGBA8888)
  val pix = new Pixmap(640,480, Pixmap.Format.RGB888)
  pix.setColor(1.f,1.f,1.f,0)
  pix.fill()

  val bytes = new Array[Byte](640*480*3)

  val live = new Ruby("kinectLoop.rb")

  DesktopApp()  

  override def init(){
  	Texture(bigpix)
  }

  override def draw(){
  	Shader.lightingMix = 0.f
  	Shader.textureMix = 1.f
		// Texture(0).draw(Kinect.depthPix,0,480)
  	Texture.bind(0)
  	cube.draw()
  }

  override def animate(dt:Float){
		
  	val img = Kinect.videoMat //new Mat()
  	// try{
  		// Kinect.videoMat.copyTo(img, Kinect.blob.mask)
  	// } catch { case e:Exception => println(e); return }
  	if( img.empty() ) return


  //   val rsmall = new Mat()
  // 	val small = new Mat()
  // 	Imgproc.resize(img,small, new Size(), 0.5,0.5,0)
  //   Core.flip(small,rsmall,1)
  //   Imgproc.cvtColor(rsmall,small, Imgproc.COLOR_BGR2RGB)

    var sub = img
  	if( subtract ){
  		val diff = bgsub(img, true)
			blob(diff)
			sub = new Mat()
			img.copyTo(sub, blob.mask)
		}

  	val out = new Mat()
  	loop.videoIO( sub, out)
    if( out.empty()) return

    // if( subtract ){
    //   val bgmask = new Mat()
    //   Core.compare(out, new Scalar(0.0), bgmask, Core.CMP_EQ)
    //   bgsub.bg.copyTo(out,bgmask)
    // }

  	out.get(0,0,bytes)
		val bb = pix.getPixels()
		bb.put(bytes)
		bb.rewind()

		Texture(0).draw(pix,0,0)


    live.animate(dt)
  }

}







