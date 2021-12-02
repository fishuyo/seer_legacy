
package seer
package cv

import org.bytedeco.javacv._
import org.bytedeco.opencv.global.opencv_core._
import org.bytedeco.opencv.global.opencv_highgui._
import org.bytedeco.opencv.global.opencv_imgcodecs._
import org.bytedeco.opencv.global.opencv_imgproc._
import org.bytedeco.opencv.opencv_core._


class BackgroundSubtract {

	// var mog = new BackgroundSubtractorMOG(3,4,0.8)

  // var bg = new Mat(720,1280,CvType.CV_32FC1)
  var bg = new Mat()
	var bgGray = new Mat()
	val mask = new Mat()
	val small = new Mat()

	var updateBG = true
	var adaptiveBG = false
	var thresh = 20f

	def setThreshold(v:Float) = thresh = v

	def apply( frame:Mat, return_mask:Boolean=false ) = {

		// if multichannel image convert to grayscale
		var gray = new Mat()
		if( frame.channels() == 3) cvtColor(frame,gray, COLOR_BGR2GRAY)
		else gray = frame

		if(updateBG){
			bg = frame.clone
			bgGray = gray.clone
			updateBG = false
			// return
		}
		if(adaptiveBG){
			accumulateWeighted( gray, bgGray, .1f)
		}

		// resize(frame,small, new Size(), 0.5,0.5,0)

		// mog(small,mask,0.01)

		// var bgray = new Mat()
		// cvtColor(bg,bgray, COLOR_BGR2GRAY)

		// compute the abs difference between background image and frame
		val diff = new Mat()
		absdiff(bgGray,gray,diff) 

		// val mat = new Mat(480,640,CvType.CV_8UC1)
		// val thresholdDiff = new Mat() //new Mat(480,640,CvType.CV_8UC1)


		// val bg8u = new Mat()
		// diff.convertTo(bg8u, CvType.CV_8UC1)

		threshold(diff,mask,thresh,255f, THRESH_BINARY)
		// adaptiveThreshold(diff,mask,255f, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 7, 10)

		if( return_mask ) mask
		else{
			val ret = new Mat()
			frame.copyTo( ret, mask)
			ret
		}

		// for( y<-(0 until 480); x<-(0 until 640)){
		// 	val d = ( if (mat.get(y,x)(0) > 0) 1f else 0f) //depthData(640*y+x).toFloat / 255f else 0f )
		// 	depthPix.setColor(d,d,d,1f)
		// 	depthPix.drawPixel(x,y)

		// }

	}


	def updateBackgroundNextFrame = updateBG = true

}

