
package com.fishuyo
package media

import java.awt.image.BufferedImage
import java.util.concurrent.TimeUnit._
import com.xuggle.xuggler.Global.DEFAULT_TIME_UNIT
import com.xuggle.mediatool._

import com.xuggle.xuggler.ICodec.ID._

class MediaWriter {

  var w=600
  var h=600
  var file = "ouput.mov"
  var framerate = DEFAULT_TIME_UNIT.convert( 33, MILLISECONDS )
  var t = 0L;
  
  val writer = ToolFactory.makeWriter( file )

  writer.addVideoStream( 0, 0, w, h )

  def addFrame( i: BufferedImage ) = {
    writer.encodeVideo(0, i, t, DEFAULT_TIME_UNIT )
    t += framerate
  }

  def close() = writer.close()
}
