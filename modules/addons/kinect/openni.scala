
// package com.fishuyo.seer 
// package openni

// import scala.collection.JavaConversions._
// import scala.collection.mutable.ArrayBuffer
// import scala.collection.mutable.Map

// import java.nio.ByteBuffer

// import org.openni._
// import com.primesense.nite._


// object Kinect {

// 	var devices = ArrayBuffer[Device]()
// 	var streams = Map[Device,ArrayBuffer[VideoStream]]()
// 	var trackers = ArrayBuffer[UserTracker]()
// 	var initialized = false

// 	def connect(id:Int=0) = {
// 		OpenNI.initialize
// 		NiTE.initialize
// 		val deviceInfos = OpenNI.enumerateDevices
// 		devices += Device.open(deviceInfos.get(id).getUri)
// 		devices.last
// 	}

// 	def connectAll() = {
// 		OpenNI.initialize
// 		val deviceInfos = OpenNI.enumerateDevices
// 		deviceInfos.foreach( (di) => devices += Device.open(di.getUri))
// 		devices
// 	}

// 	def disconnect(){
// 		devices.foreach( _.close() )
// 		NiTE.shutdown
// 		OpenNI.shutdown
// 	}

// 	def trackUser(){
// 		val tracker = UserTracker.create
// 		tracker.addNewFrameListener(new UserListener())
// 	}

// 	def startDepth( f:(Array[Int])=>Unit, id:Int=0 ) = {
// 		val stream = VideoStream.create(devices(id), SensorType.DEPTH)
// 		stream.addNewFrameListener(new FrameListener(f))
// 		// streams(devices(id)) += stream
// 		stream.start
// 		stream
// 	}

// 	def startRGB( f:(Array[Int])=>Unit, id:Int=0 ) = {
// 		val stream = VideoStream.create(devices(id), SensorType.COLOR)
// 		stream.addNewFrameListener(new FrameListener(f))
// 		// streams(devices(id)) += stream
// 		stream.start
// 		stream
// 	}

// }

// object Util{

//   def calcHist(depthBuffer:ByteBuffer, hist:Array[Float]) = {
//   	var histogram = hist
//     // make sure we have enough room
//     if (histogram == null){ // || histogram.length < vstream.getMaxPixelValue()) {
//       histogram = new Array[Float](10000) //vstream.getMaxPixelValue())
//     }
    
//     // reset
//     for (i <- 0 until histogram.length) histogram(i) = 0;

//     var points = 0;
//     while(depthBuffer.remaining() > 0) {
//       val depth = depthBuffer.getShort() & 0xFFFF;
//       if (depth != 0) {
//         histogram(depth) += 1
//         points += 1
//       }
//     }

//     for (i <- 1 until histogram.length) histogram(i) += histogram(i - 1)

//     if (points > 0) {
//     	for (i <- 1 until histogram.length)
//     		histogram(i) = (256 * (1.0f - (histogram(i) / (1f*points))));
//     }
//     histogram
//   }
// }

// class UserListener() extends UserTracker.NewFrameListener {

// 	override def onNewFrame(tracker:UserTracker){
// 		this.synchronized{
// 			println("track frame")
// 		}
// 	}
// }

// class FrameListener(f:(Array[Int])=>Unit) extends VideoStream.NewFrameListener {

// 	var frame:VideoFrameRef = null
// 	var pixels:Array[Int] = null
// 	var histogram:Array[Float] = null
// 	var vstream:VideoStream = null

//   override def onFrameReady(stream:VideoStream){
//   	this.synchronized {
//     if (frame != null) {
//       frame.release();
//       frame = null;
//     }
    
//     vstream = stream
//     frame = stream.readFrame();
//     val data = frame.getData().order(java.nio.ByteOrder.LITTLE_ENDIAN);
    
//     // make sure we have enough room
//     if (pixels == null || pixels.length < frame.getWidth() * frame.getHeight()) {
//       pixels = new Array[Int](frame.getWidth() * frame.getHeight());
//     }
    
//     import PixelFormat._

//     frame.getVideoMode().getPixelFormat() match {
//       case DEPTH_1_MM | DEPTH_100_UM | SHIFT_9_2 | SHIFT_9_3 =>
//         histogram = Util.calcHist(data,histogram);
//         data.rewind();
//         var pos = 0;
//         while(data.remaining() > 0) {
//           val depth = data.getShort().toInt & 0xFFFF;
//           val pixel = histogram(depth).toShort;
//           pixels(pos) = 0xFF000000 | (pixel << 16) | (pixel << 8);
//           pos += 1
//         }
//       case RGB888 =>
//         var pos = 0;
//         while (data.remaining() > 0) {
//           val red = data.get().toInt & 0xFF;
//           val green = data.get().toInt & 0xFF;
//           val blue = data.get().toInt & 0xFF;
//           pixels(pos) = 0xFF000000 | (red << 16) | (green << 8) | blue;
//           pos += 1
//         }
//       case _ =>
//         // don't know how to draw
//         frame.release();
//         frame = null;
//     }
//     f(pixels)
//   	}
//   }


// }