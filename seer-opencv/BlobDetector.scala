
// package com.fishuyo
// package cv

// import graphics._
// import maths.Vec3 
// import java.nio.ByteBuffer


// import com.badlogic.gdx.graphics.Pixmap
// import com.badlogic.gdx.graphics.glutils._

// import org.opencv.core._
// import org.opencv.imgproc.Imgproc
// import org.opencv.highgui.Highgui

// import scala.collection.JavaConversions._
// import collection.mutable.ListBuffer


// object OpenCV {
//   try{ System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME) }
//   catch{ case e:Exception => println(e) }
// }

// class BlobDetector {

// 	var threshold = Vec3(0.f,0.8f,0.f)
// 	var sizeThreshold = 20
// 	def setSizeThreshold(v:Int) = sizeThreshold = v

// 	val depthPix = new Pixmap(640,480, Pixmap.Format.RGBA8888)
// 	val videoPix = new Pixmap(640,480, Pixmap.Format.RGBA8888)

// 	var bg:Mat = null
// 	var getBG = 20
// 	def setBGImage() = { getBG = 20; bg.setTo( new Scalar(0.f)) }

//     bg = new Mat(480,640,CvType.CV_32FC1)
	

// 	val depthHandler = new DepthHandler{ 
// 		override def onFrameReceived(mode:FrameMode, frame:ByteBuffer, timestamp:Int) {
// 			// println( "depth wy: " + mode.getWidth + " " + mode.getHeight + " " + mode.format )

// 			val mat = new Mat(480,640,CvType.CV_8UC1)
// 			val diff = new Mat(480,640,CvType.CV_8UC1)
// 			val depthData = new Array[Byte](640*480)
// 			val flo = new Array[Float](640*480)

// 			for( y<-(0 until 480); x<-(0 until 640)){
// 				val i = 2*(640*y + x)

// 				val lb = (frame.get(i) & 0xFF).toShort
// 				val gb = (frame.get(i+1) & 0xFF).toShort
// 				val raw:Int = (gb) << 8 | lb
// 				var depth:Float = raw / 2048.f //gamma(raw) // / 2048.f
// 				//if( depth > .5f) depth = 1.f


// 				depthData(640*y+x) = (if(depth > threshold.x && depth < threshold.y) 255.toByte else 0.toByte )
// 				// depthData(640*y+x) = (depth*255.f).toByte //(if(depth > threshold.x && depth < threshold.y) 255.toByte else 0.toByte )
// 				flo(640*y+x) = depth
// 			}
// 			//println ( flo.max )

// 			mat.put(0,0, depthData)
// 			if( getBG > 0 ){
// 				Imgproc.accumulateWeighted( mat, bg, .1f) //mat.clone
// 				getBG -= 1
// 				for( y<-(0 until 480); x<-(0 until 640)){
// 					val d = bg.get(y,x)(0).toFloat / 255.f
// 					videoPix.setColor(d,d,d,1.f)
// 					videoPix.drawPixel(x,y)
// 				}
// 			}

// 			val tmp = new Mat()
// 			val bg8u = new Mat()
// 			bg.convertTo(bg8u, CvType.CV_8UC1)
// 			Core.absdiff(bg8u,mat,tmp)
// 			Imgproc.threshold(tmp,diff,threshold.y,255.f, Imgproc.THRESH_BINARY)

// 			for( y<-(0 until 480); x<-(0 until 640)){
// 				val d = ( if (mat.get(y,x)(0) > 0) 1.f else 0.f) //depthData(640*y+x).toFloat / 255.f else 0.f )
// 				depthPix.setColor(d,d,d,1.f)
// 				depthPix.drawPixel(x,y)

// 			}

// 			//Highgui.imwrite("depthimage.png",mat)

// 			val contours = new java.util.ArrayList[MatOfPoint]()
// 			Imgproc.findContours(mat, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

// 			var offset = 0
// 			for( i<-(0 until contours.length)){
// 				// val rect = Imgproc.boundingRect(contours(i))
// 				// val rect = Imgproc.minAreaRect(contours(i))

// 				// if( rect.width > sizeThreshold && rect.height > sizeThreshold){
// 				// 	val x = rect.x + rect.width/2
// 				// 	val y = rect.y + rect.height/2
// 				// 	depthPix.setColor(0.f,1.f,0.f,1.f)
// 				// 	depthPix.drawRectangle(rect.x,rect.y,rect.width,rect.height)
// 				// 	callbacks.foreach(_(i-offset,Array(x,y,rect.width,rect.height)))
// 				// }else offset += 1

// 				if( contours.get(i).size().area() >= 5 ){
// 					val mMOP2f = new MatOfPoint2f()
// 					contours.get(i).convertTo(mMOP2f, CvType.CV_32FC2);
// 					val rotRect = Imgproc.fitEllipse(mMOP2f)
// 					if( rotRect.size.width > sizeThreshold && rotRect.size.height > sizeThreshold){
// 						val (x,y) = (rotRect.center.x, rotRect.center.y)
// 						val (w,h) = (rotRect.size.width/2, rotRect.size.height/2)
// 						val cosa = math.cos(rotRect.angle.toRadians)
// 						val sina = math.sin(rotRect.angle.toRadians)

// 						val x1 = cosa*w - sina*h + x
// 						val y1 = sina*w + cosa*h + y
// 						val x2 = cosa*w + sina*h + x
// 						val y2 = sina*w - cosa*h + y
// 						val x3 = -cosa*w + sina*h + x
// 						val y3 = -sina*w - cosa*h + y
// 						val x4 = -cosa*w - sina*h + x
// 						val y4 = -sina*w + cosa*h + y
// 						depthPix.setColor(0.f,1.f,0.f,1.f)
// 						// depthPix.drawRectangle((x-w).toInt,(y-h).toInt,(2*w).toInt,(2*h).toInt)
// 						depthPix.drawLine(x1.toInt,y1.toInt,x2.toInt,y2.toInt)
// 						depthPix.drawLine(x2.toInt,y2.toInt,x3.toInt,y3.toInt)
// 						depthPix.drawLine(x3.toInt,y3.toInt,x4.toInt,y4.toInt)
// 						depthPix.drawLine(x1.toInt,y1.toInt,x4.toInt,y4.toInt)
// 						callbacks.foreach(_(i-offset,Array(x.toFloat,y.toFloat,rotRect.size.width.toFloat,rotRect.size.height.toFloat,rotRect.angle.toFloat)))
// 					}else offset += 1
// 				}
// 			}
// 		}
// 	}

// 	def startVideo = device.foreach( _.startVideo(videoHandler))
// 	val videoHandler = new VideoHandler{ 
// 		override def onFrameReceived(mode:FrameMode, frame:ByteBuffer, timestamp:Int) {
// 			// println( "video wh: " + mode.getWidth + " " + mode.getHeight + " " + mode.format )
// 			for( y<-(0 until 480); x<-(0 until 640)){
// 				val i = 3*(640*y+x)
// 				val color:Int = frame.get(i) << 24 | frame.get(i+1) << 16 | frame.get(i+2) << 8 | 0xFF
// 				videoPix.drawPixel(x,y,color)
// 			} 
// 		}
// 	}

// 	override def init(){
// 		depthTextureID = Texture(depthPix)
// 	}
// 	override def draw(){
// 		val t = Texture(depthTextureID).getTextureObjectHandle
// 		Texture(depthTextureID).bind(t)
// 		Shader().setUniformi("u_texture0", t );
// 		cube.draw()
// 	}
// 	override def step(dt:Float){
// 		Texture(depthTextureID).draw(depthPix,0,0)
// 	}

// 	def clear() = { callbacks.clear()}
//   def bind(f:Callback) = callbacks += f
// }