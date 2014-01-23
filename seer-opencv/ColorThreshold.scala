package com.fishuyo.seer
package cv


import org.opencv.core._
import org.opencv.imgproc.Imgproc
import org.opencv.highgui.Highgui
import org.opencv.video._


class ColorThreshold( val hue:(Int,Int), val sat:(Int,Int)=(100,255), val value:(Int,Int)=(100,255) ) {

	var twoChecks = false
	var min = new Scalar(hue._1,sat._1,value._1)
	var max = new Scalar(hue._2,sat._2,value._2)

	if ( hue._1 > hue._2){ //red? bridging hue seem
		twoChecks = true
	}

	val mask = new Mat()
	val hsv = new Mat()
	val out = new Mat()

	def apply( frame:Mat, return_mask:Boolean=true ) = {

		Imgproc.cvtColor(frame,hsv, Imgproc.COLOR_BGR2HSV)

		if( twoChecks ){

			val max1 = new Scalar( 180, sat._2, value._2 )
			val min1 = new Scalar( 0, sat._1, value._1 )

			val mask1 = new Mat()
			val mask2 = new Mat()
			Core.inRange(hsv,min,max1,mask1) 
			Core.inRange(hsv,min1,max,mask2) 
			Core.bitwise_or(mask1,mask2,mask)

		} else {
			Core.inRange(hsv,min,max,mask) 
		}

		if( return_mask ) mask
		else{
			frame.copyTo( out, mask)
			out
		}

	}
}