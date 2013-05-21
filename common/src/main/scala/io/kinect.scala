
package com.fishuyo
package io

import graphics._
import maths.Vec3 
import java.nio.ByteBuffer
import org.openkinect.freenect._

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils._

object Kinect extends GLAnimatable {

	var context:Option[Context] = _
	var device:Option[Device] = _

	var connected = false

	var depthTextureID = 0
	val cube = GLPrimitive.cube()
	cube.scale.set(1.f, 480.f/640.f, .1f)

	val depthPix = new Pixmap(640,480, Pixmap.Format.RGBA8888)
	val videoPix = new Pixmap(640,480, Pixmap.Format.RGBA8888)

	val gamma = new Array[Float](2048)
	for (i<-(0 until 2048)){
		var v = i/2048.0
		v = math.pow(v, 3)*6;
		gamma(i) = v.toFloat;
	}


	def connect(){
		if(connected) return

		val c = Freenect.createContext()
    
    // c.setLogHandler(new Jdk14LogHandler())
    // c.setLogLevel(LogLevel.SPEW)

    if (c.numDevices() > 0){
    	val dev = c.openDevice(0)
    	dev.setDepthFormat( DepthFormat.D11BIT )
    	context = Some(c)
    	device = Some(dev)
    	connected = true
    } else {
      println("WARNING: No kinects detected.");
      c.shutdown
    }
	}

	def disconnect(){
		device.foreach( (d) => { d.stopVideo; d.stopDepth; d.close } )
		context.foreach(_.shutdown)
	}


	def setAngle(v:Double) = device.foreach( _.setTiltAngle(v) )

	def startDepth = device.foreach( _.startDepth(depthHandler) )
	val depthHandler = new DepthHandler{ 
		override def onFrameReceived(mode:FrameMode, frame:ByteBuffer, timestamp:Int) {
			// println( "depth wy: " + mode.getWidth + " " + mode.getHeight + " " + mode.format )

			for( y<-(0 until 480); x<-(0 until 640)){
				val i = 2*(640*y + x)

				val lb = (frame.get(i) & 0xFF).toShort
				val gb = (frame.get(i+1) & 0xFF).toShort
				val raw:Int = (gb) << 8 | lb
				var depth:Float = gamma(raw) // / 2048.f
				if( depth > .5f) depth = 1.f

				case class Color(r:Int,g:Int,b:Int)

				var color = gb match {
					case 0 => Color(255,255-lb,255-lb)
					case 1 => Color(255,lb,0)
					case 2 => Color(255-lb,255,0)
					case 3 => Color(0,255,lb)
					case 4 => Color(0,255-lb,255)
					case 5 => Color(0,0,255-lb)
					case _ => Color(0,0,0)
				}
				val c = color.r << 24 | color.g << 16 | color.b << 8 | 0xFF
				depthPix.setColor(depth,depth,depth,1.f)
				depthPix.drawPixel(x,y)
			}
		}
	}

	def startVideo = device.foreach( _.startVideo(videoHandler))
	val videoHandler = new VideoHandler{ 
		override def onFrameReceived(mode:FrameMode, frame:ByteBuffer, timestamp:Int) {
			// println( "video wh: " + mode.getWidth + " " + mode.getHeight + " " + mode.format )
			for( y<-(0 until 480); x<-(0 until 640)){
				val i = 3*(640*y+x)
				val color:Int = frame.get(i) << 24 | frame.get(i+1) << 16 | frame.get(i+2) << 8 | 0xFF
				videoPix.drawPixel(x,y,color)
			} 
		}
	}

	override def init(){
		depthTextureID = Texture(depthPix)
	}
	override def draw(){
		val t = Texture(depthTextureID).getTextureObjectHandle
		Texture(depthTextureID).bind(t)
		Shader().setUniformi("u_texture0", t );
		cube.draw()
	}
	override def step(dt:Float){
		Texture(depthTextureID).draw(depthPix,0,0)
	}
}