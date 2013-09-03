package com.fishuyo
package cv


import org.opencv.core._
import org.opencv.imgproc.Imgproc
import org.opencv.highgui.Highgui


class BackgroundSubtract {

	var bg = new Mat()
	var updateBG = true
	var threshold = 127.f

	def apply( frame:Mat ) = {

		// if multichannel image convert to grayscale
		var gray = new Mat()
		if( frame.channels() == 3) Imgproc.cvtColor(frame,gray, Imgproc.COLOR_BGR2GRAY)
		else gray = frame

		if(updateBG){
			bg = gray.clone
			updateBG = false
			// return
		}

		// compute the abs difference between background image and frame
		val diff = new Mat()
		Core.absdiff(bg,gray,diff) 

		// val mat = new Mat(480,640,CvType.CV_8UC1)
		val thresholdDiff = new Mat() //new Mat(480,640,CvType.CV_8UC1)


		// val bg8u = new Mat()
		// bg.convertTo(bg8u, CvType.CV_8UC1)
		Imgproc.threshold(diff,thresholdDiff,threshold,255.f, Imgproc.THRESH_BINARY)

		thresholdDiff

		// for( y<-(0 until 480); x<-(0 until 640)){
		// 	val d = ( if (mat.get(y,x)(0) > 0) 1.f else 0.f) //depthData(640*y+x).toFloat / 255.f else 0.f )
		// 	depthPix.setColor(d,d,d,1.f)
		// 	depthPix.drawPixel(x,y)

		// }

	}

	def updateBackgroundNextFrame = updateBG = true

}