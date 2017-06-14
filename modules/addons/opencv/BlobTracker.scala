
// package com.fishuyo.seer
// package cv

// import spatial.Vec3

// import org.opencv.core._
// import org.opencv.imgproc.Imgproc
// import org.opencv.highgui.Highgui
// import org.opencv.video._

// import collection.mutable.ListBuffer
// import scala.collection.JavaConversions._

// import com.badlogic.gdx.graphics.Pixmap


// class TRect(val x:Float, val y:Float, val w:Float, val h:Float, val angle:Float) {
// 	val pos = Vec3(x,y,0)
// }

// class BlobTracker {

// 	type Callback = (Array[TRect]) => Any
//   val callbacks = new ListBuffer[Callback]()

// 	val blobs = new ListBuffer[TRect]()
// 	var mask:Mat = _

// 	var areaThreshold = 100
// 	var dimThreshold = 10

// 	def setThreshold(area:Int,dim:Int){ areaThreshold = area; dimThreshold = dim }

// 	def apply( binary:Mat, pix:Pixmap=null ) = {

// 		val contours = new java.util.ArrayList[MatOfPoint]()
// 		Imgproc.findContours(binary, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

// 		blobs.clear
// 		mask = Mat.zeros(binary.size(),CvType.CV_8UC1)

// 		var offset = 0
// 		for( i<-(0 until contours.length)){

// 			if( contours.get(i).size().area() >= areaThreshold ){

// 				val mMOP2f = new MatOfPoint2f()
// 				contours.get(i).convertTo(mMOP2f, CvType.CV_32FC2);
// 				val rotRect = Imgproc.fitEllipse(mMOP2f)

// 				if( rotRect.size.width > dimThreshold && rotRect.size.height > dimThreshold){
// 					val (x,y) = (rotRect.center.x, rotRect.center.y)
// 					val blob = new TRect(x.toFloat,y.toFloat,rotRect.size.width.toFloat,rotRect.size.height.toFloat,rotRect.angle.toFloat)
// 					blobs += blob

// 					Imgproc.drawContours(mask, contours, i, new Scalar(255), -1);
// 					// val i = blobs.indexWhere( (_.pos - blob.pos).magSq < 5f )
// 					// if( i > -1){

// 					// } else{

// 					// }
					
// 					if( pix != null){
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
// 						pix.setColor(0f,1f,0f,1f)
// 						// pix.drawRectangle((x-w).toInt,(y-h).toInt,(2*w).toInt,(2*h).toInt)
// 						pix.drawLine(x1.toInt,y1.toInt,x2.toInt,y2.toInt)
// 						pix.drawLine(x2.toInt,y2.toInt,x3.toInt,y3.toInt)
// 						pix.drawLine(x3.toInt,y3.toInt,x4.toInt,y4.toInt)
// 						pix.drawLine(x1.toInt,y1.toInt,x4.toInt,y4.toInt)
// 					}

// 				}else offset += 1
// 			}
// 		}

// 		try{ callbacks.foreach(_(blobs.toArray)) }
// 		catch{ case e:Exception => println(e) }
// 		mask
// 	}

// 	def clear() = { callbacks.clear()}
//   def bind(f:Callback) = callbacks += f

// }


