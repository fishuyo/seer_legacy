
// package com.fishuyo.seer
// package io
// package kinect

// import cv._
// import graphics._
// import spatial.Vec3 

// import java.nio.ByteBuffer

// import org.openkinect.freenect._

// import com.badlogic.gdx.Gdx
// import com.badlogic.gdx.graphics.Pixmap
// import com.badlogic.gdx.graphics.PixmapIO
// import com.badlogic.gdx.graphics.glutils._

// import org.opencv.core._
// import org.opencv.imgproc.Imgproc
// import org.opencv.highgui.Highgui

// import scala.collection.JavaConversions._
// import collection.mutable.ListBuffer



// object Kinect extends Animatable {
  
//   var bgsub:BackgroundSubtract = _
//   var blob:BlobTracker = _

//   type Callback = (Int, Array[Float]) => Any
//   val callbacks = new ListBuffer[Callback]()

// 	var context:Option[Context] = None
// 	var device:Option[Device] = None

// 	var connected = false

// 	var depthTextureID = 0
// 	val cube = Cube() //Primitive3D.cube()
// 	cube.scale.set(1.f, 480.f/640.f, .1f)

// 	val depthPix = new Pixmap(640,480, Pixmap.Format.RGBA8888)
// 	val videoPix = new Pixmap(640,480, Pixmap.Format.RGB888)
	
// 	val detectPix = new Pixmap(640,480, Pixmap.Format.RGBA8888)

// 	val depthData = new Array[Byte](640*480)
// 	val flo = new Array[Float](640*480)	
// 	val col = new Array[Int](640*480)
// 	var bytes = new Array[Byte](640*480*3)


// 	var mat:Mat = _
// 	var videoMat:Mat = _

// 	val gamma = new Array[Float](2048)
// 	for (i<-(0 until 2048)){
// 		var v = i/2047.0
// 		if( v == 1.f) v = 0.0
// 		else v = math.pow(v, 3)*6;
// 		gamma(i) = v.toFloat;
// 	}


// 	def connect(){
// 		if(connected) return

//     System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)

//     bgsub =  new BackgroundSubtract
//     blob = new BlobTracker

//     mat = new Mat(480,640,CvType.CV_8UC1)
//     videoMat = new Mat(480,640,CvType.CV_8UC3)

// 		val c = Freenect.createContext()
    
//     // c.setLogHandler(new Jdk14LogHandler())
//     // c.setLogLevel(LogLevel.SPEW)

//     if (c.numDevices() > 0){
//     	val dev = c.openDevice(0)
//     	dev.setDepthFormat( DepthFormat.D11BIT )
//     	context = Some(c)
//     	device = Some(dev)
//     	connected = true
//     } else {
//       println("WARNING: No kinects detected.");
//       c.shutdown
//     }
// 	}

// 	def disconnect(){
// 		device.foreach( (d) => { d.stopVideo; d.stopDepth; d.close } )
// 		context.foreach(_.shutdown)
// 	}

// 	def setAngle(v:Double) = device.foreach( _.setTiltAngle(v) )

// 	def startDepth = device.foreach( _.startDepth(depthHandler) )
	
// 	val depthHandler = new DepthHandler{ 
// 		override def onFrameReceived(mode:FrameMode, frame:ByteBuffer, timestamp:Int) {
// 			// println( "depth wy: " + mode.getWidth + " " + mode.getHeight + " " + mode.format )

// 		  val histogram = Util.calcHist(frame);
//       frame.rewind();
//       var pos = 0;
//       while(frame.remaining() > 0) {
//         val depth = frame.getShort().toInt & 0xFFFF;
//         val pixel = histogram(depth).toShort;
//         col(pos) = 0xFF000000 | (pixel << 16) | (pixel << 8);
// 				depthData(pos) = (pixel).toByte
// 				flo(pos) = depth / 255.f //histogram(depth)/255.f
//         pos += 1
//       }

// 			// for( i<-(0 until 480*640)){

// 			// 	val lb = (frame.get(2*i) & 0xFF).toShort
// 			// 	val gb = (frame.get(2*i+1) & 0xFF).toShort
// 			// 	val raw:Int = (gb) << 8 | lb
// 			// 	var depth:Float = gamma(raw) 
// 			// 	//var depth:Float = raw / 2048.f

// 			// 	case class Color(r:Int,g:Int,b:Int)
// 			// 	var color = gb match {
// 			// 		case 0 => Color(255,255-lb,255-lb)
// 			// 		case 1 => Color(255,lb,0)
// 			// 		case 2 => Color(255-lb,255,0)
// 			// 		case 3 => Color(0,255,lb)
// 			// 		case 4 => Color(0,255-lb,255)
// 			// 		case 5 => Color(0,0,255-lb)
// 			// 		case _ => Color(0,0,0)
// 			// 	}
// 			// 	val c = color.r << 24 | color.g << 16 | color.b << 8 | 0xFF
// 			// 	col(i) = c

// 			// 	depthData(i) = (depth*255.f).toByte
// 			// 	flo(i) = depth
// 			// }

// 			//println ( flo.max )

// 			// put kinect gamma corrected depthData into 8UC1 Mat
// 			mat.put(0,0, depthData)

// 			// copy background into depth shadows to effectively ignore their detection
// 			if( !bgsub.updateBG ){
// 				val bgmask = new Mat()
// 	      Core.compare(mat, new Scalar(0.0), bgmask, Core.CMP_EQ)
// 	      bgsub.bg.copyTo(mat,bgmask)
// 			}

// 			// do background subtraction
// 			val diff = bgsub(mat, true)

// 			// blob tracking
// 			blob(diff, detectPix)

// 			for( y<-(0 until 480); x<-(0 until 640)){
// 				val d = blob.mask.get(y,x)(0).toFloat / 255.f //( if (diff.get(y,x)(0) > 0) 1.f else 0.f) //depthData(640*y+x).toFloat / 255.f else 0.f )
// 				detectPix.setColor(d,d,d,1.f)
// 				detectPix.drawPixel(x,y)

// 				// val v = flo(640*y+x) //depthData(640*y+x).toFloat / 255.f
// 				val v = flo(640*y+x) //depthData(640*y+x).toFloat / 255.f
// 				depthPix.setColor(v,v,v,1.f)
// 				// depthPix.setColor(col(640*y+x))
// 				depthPix.drawPixel(x,y)

// 			}

// 			// blob.mask.get(0,0,bytes)
// 			// val bb = depthPix.getPixels()
// 			// bb.put(bytes)
// 			// bb.rewind()

// 		}
// 	}

// 	def startVideo = device.foreach( _.startVideo(videoHandler))
// 	val videoHandler = new VideoHandler{ 
// 		override def onFrameReceived(mode:FrameMode, frame:ByteBuffer, timestamp:Int) {
// 			// println( "video wh: " + mode.getWidth + " " + mode.getHeight + " " + mode.format )
// 			frame.get(bytes)
// 			frame.rewind()
// 			val bb = videoPix.getPixels()
// 			bb.put(bytes)
// 			bb.rewind()

// 			videoMat.put(0,0,bytes)



// 			// for( y<-(0 until 480); x<-(0 until 640)){
// 			// 	val i = 3*(640*y+x)
// 			// 	val color:Int = frame.get(i) << 24 | frame.get(i+1) << 16 | frame.get(i+2) << 8 | 0xFF
// 			// 	videoPix.drawPixel(x,y,color)
// 			// } 
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
// 	override def animate(dt:Float){
// 		Texture(depthTextureID).draw(depthPix,0,0)
// 	}

// 	def clear() = { callbacks.clear()}
//   def bind(f:Callback) = callbacks += f


//   def captureDepthImage(){
//   	Gdx.files.external("SeerData/image").file().mkdirs()
//   	var file = Gdx.files.external("SeerData/image/depth-" + (new java.util.Date()).toLocaleString().replace(' ','-').replace(':','-') + ".png" )
//     PixmapIO.writePNG(file, depthPix)
//   }

//   def captureDepthPoints(scale:Float = 1000.f){
//   	Gdx.files.external("SeerData/points").file().mkdirs()
//   	var file = Gdx.files.external("SeerData/points/kinect-" + (new java.util.Date()).toLocaleString().replace(' ','-').replace(':','-') + ".xyz" ).file()

//     val w = 640
//     val h = 480
    
//     val buf = new Array[Byte](w*h*4)
    
//     val out = new java.io.FileWriter( file )

//     for( x<-(0 until w); y<-(0 until h)){
//     	val d = flo(640*y+x)
//       val off = .33f

//       val z = d * -scale

//       if( d > 0.f){
// 	      out.write( x + " " + y + " " + (z+off) + " 0 0 1\n" )
// 	      out.write( x + " " + y + " " + (z-off) + " 0 0 -1\n" )
// 	      out.write( x + " " + (y+off) + " " + z + " 0 1 0\n" )
// 	      out.write( x + " " + (y-off) + " " + z + " 0 -1 0\n" )
// 	      out.write( (x+off) + " " + y + " " + z + " 1 0 0\n" )
// 	      out.write( (x-off) + " " + y + " " + z + " -1 0 0\n" )
//     	}
//     }

//     out.close
//   }
// }

// object Util{

// 	var histogram = new Array[Float](100000)
//   def calcHist(depthBuffer:ByteBuffer) = {
    
//     // reset
//     for (i <- 0 until histogram.length) histogram(i) = 0;

//     var (min,max) = (999999999, -99999999)
//     var points = 0;
//     while(depthBuffer.remaining() > 0) {
//       val depth = depthBuffer.getShort() & 0xFFFF;
//       if(depth > max) max = depth
//       if(depth < min) min = depth
//       if (depth != 0) {
//         histogram(depth) += 1
//         points += 1
//       }
//     }
//     println( max + " " + min)

//     for (i <- 1 until histogram.length) histogram(i) += histogram(i - 1)

//     if (points > 0) {
//     	for (i <- 1 until histogram.length)
//     		histogram(i) = (256 * (1.0f - (histogram(i) / (1.f*points))));
//     }
//     histogram
//   }
// }