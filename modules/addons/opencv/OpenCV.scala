
package com.fishuyo.seer
package cv

import org.opencv.core._
import org.opencv.highgui._
import org.opencv.imgproc._

object OpenCV {
  loadLibrary()
  
	def loadLibrary(){
		try{ System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)}
		catch{ case e:Exception => println(e) }
	}

}

object Webcam {
  def apply(device:Int = 0) = new Webcam(device)
}
class Webcam(device:Int) extends VideoCapture(device) {

  def width() = this.get(Highgui.CV_CAP_PROP_FRAME_WIDTH).toInt
  def height() = this.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT).toInt

}