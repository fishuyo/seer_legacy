package com.fishuyo
package cv


import org.opencv.core._
import org.opencv.imgproc.Imgproc
import org.opencv.highgui.Highgui
import org.opencv.video._


class BackgroundSubtract {

	// var mog = new BackgroundSubtractorMOG(3,4,0.8)

  // var bg = new Mat(720,1280,CvType.CV_32FC1)
	var bg = new Mat()
	val mask = new Mat()
	val small = new Mat()

	var updateBG = true
	var adaptiveBG = false
	var threshold = 12.f
	def setThreshold(v:Float) = threshold = v

	def apply( frame:Mat, return_mask:Boolean=false ) = {

		// if multichannel image convert to grayscale
		var gray = new Mat()
		if( frame.channels() == 3) Imgproc.cvtColor(frame,gray, Imgproc.COLOR_BGR2GRAY)
		else gray = frame

		if(updateBG){
			bg = gray.clone
			updateBG = false
			// return
		}
		if(adaptiveBG){
			Imgproc.accumulateWeighted( gray, bg, .1f)
		}

		// Imgproc.resize(frame,small, new Size(), 0.5,0.5,0)

		// mog(small,mask,0.01)

		// var bgray = new Mat()
		// Imgproc.cvtColor(bg,bgray, Imgproc.COLOR_BGR2GRAY)

		// compute the abs difference between background image and frame
		val diff = new Mat()
		Core.absdiff(bg,gray,diff) 

		// val mat = new Mat(480,640,CvType.CV_8UC1)
		// val thresholdDiff = new Mat() //new Mat(480,640,CvType.CV_8UC1)


		// val bg8u = new Mat()
		// diff.convertTo(bg8u, CvType.CV_8UC1)

		Imgproc.threshold(diff,mask,threshold,255.f, Imgproc.THRESH_BINARY)
		// Imgproc.adaptiveThreshold(diff,mask,255.f, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 7, 10)

		if( return_mask ) mask
		else{
			val ret = new Mat()
			frame.copyTo( ret, mask)
			ret
		}

		// for( y<-(0 until 480); x<-(0 until 640)){
		// 	val d = ( if (mat.get(y,x)(0) > 0) 1.f else 0.f) //depthData(640*y+x).toFloat / 255.f else 0.f )
		// 	depthPix.setColor(d,d,d,1.f)
		// 	depthPix.drawPixel(x,y)

		// }

	}


	def updateBackgroundNextFrame = updateBG = true

}