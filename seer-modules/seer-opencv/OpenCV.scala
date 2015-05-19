
package com.fishuyo.seer
package cv

object OpenCV {
  loadLibrary()
  
	def loadLibrary(){
		try{ System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)}
		catch{ case e:Exception => println(e) }
	}
}