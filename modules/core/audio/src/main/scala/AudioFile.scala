
package seer
package audio

import de.sciss.audiofile.{AudioFile => SAudioFile }
import de.sciss.audiofile._

import collection.mutable.ArrayBuffer

object AudioFile {

  def readDir(path:String):Array[Array[Float]] = {
    val file = new java.io.File(path)
    if(!file.isDirectory) {
      println("Invalid path..")
      return Array()
    }

    file.listFiles.filter{ case f => f.getPath.endsWith(".wav") }.map { case f =>
      read(f.getPath)
    }
  }

  def read(path:String):Array[Float] = {
    var file =  new java.io.File(path) 
    val in = SAudioFile.openRead(file)

    val size = 8192 
    val buf = in.buffer(size)

    val samples = ArrayBuffer[Float]()

    var remain  = in.numFrames
    while (remain > 0) {
      val chunk = math.min(size, remain).toInt
      in.read(buf, 0, chunk)
      samples ++=  buf(0).map(_.toFloat) // XXX handle multichannel data
      remain -= chunk
    }
    in.close
    samples.toArray
  }

  def write(path:String, samples:Array[Double]) = {
    val outSpec = new AudioFileSpec(fileType = AudioFileType.Wave, sampleFormat = SampleFormat.Int16, 1, 44100.toDouble, None, 0)
    var file = new java.io.File(path)
    file.mkdirs()
    val out = SAudioFile.openWrite(file, outSpec)
    out.write( Array(samples), 0, samples.length )
    out.close()
  }


}
