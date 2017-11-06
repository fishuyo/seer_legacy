
package com.fishuyo.seer
package cv

import org.opencv.core._
import org.opencv.videoio._
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

  def width() = this.get(Videoio.CAP_PROP_FRAME_WIDTH).toInt
  def height() = this.get(Videoio.CAP_PROP_FRAME_HEIGHT).toInt

}