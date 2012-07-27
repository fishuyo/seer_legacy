
package com.fishuyo
package audio

import ray._

import java.io._
import java.nio.FloatBuffer
import sun.audio._
import javax.sound.sampled._

import de.sciss.synth.io._

//import de.gulden.framework.jjack._

object AudioOut { //extends JJackAudioProcessor  {

  var source: SoundSource = null
  var line : Option[SourceDataLine] = None
  var format: Option[AudioFormat] = None


  var bytes: Array[Byte] = null
  
  //def process( e: JJackAudioEvent ) = {
    
    /*for ( i <- ( 0 until e.countChannels) ) {
      val in = e.getInput(i);
      val out = e.getOutput(i);
      val cap = in.capacity();
      for (j <- (0 until cap) ) {
        var a = in.get(j);
        //a *= v;
        if (a > 1.0) {
          a = 1.0f;
        } else if(a < -1.0) {
          a = -1.0f;
        }
        out.put(j, a);
      }
    }*/
  //}
  def setSource( s: SoundSource ) = {
    source = s
    open( s.format.getFormat )
  }

  def sampleRate = source.in.sampleRate
  def open( format: AudioFormat, size: Int = 1024 ) = {
    
    line = Some( AudioSystem.getLine( new DataLine.Info( classOf[SourceDataLine], format)).asInstanceOf[SourceDataLine] )
    this.format = Some( format )
    line.get.open( format )

  }

  def start = line.get.start

  def write( b: Array[Byte], off:Integer, len: Integer ) = line match { case Some(s) => s.write(b, off, len); case None => None }

  def write( samples: Array[Float], off:Integer, len:Integer ) = {
    //check line exists?

    val numBytes = len * (format.get.getSampleSizeInBits() / 8);
    if (bytes == null || numBytes > bytes.length)
      bytes = new Array[Byte](numBytes)

    // Convert doubles to bytes using format
    encodeSamples(samples, bytes, len);

    // write it
    line.get.write(bytes, off, numBytes);
  }

  def encodeSamples( audioData: Array[Float], audioBytes: Array[Byte], length: Int) = {
    var in: Int = 0
    if (format.get.getSampleSizeInBits() == 16) {
      if (format.get.isBigEndian()) {
        for (i <- (0 until length) ) {
          in = (audioData(i)*32767).toInt
          /* First byte is MSB (high order) */
          audioBytes.update(2*i, (in >> 8).toByte)
          /* Second byte is LSB (low order) */
          audioBytes.update(2*i+1, (in & 255).toByte)
        }
      } else {
        for (i <- (0 until length) ) {
          in = (audioData(i)*32767).toInt;
          /* First byte is LSB (low order) */
          audioBytes.update(2*i, (in & 255).toByte )
          /* Second byte is MSB (high order) */
          audioBytes.update(2*i+1, (in >> 8).toByte )
        }
      }
    } else if (format.get.getSampleSizeInBits() == 8) {
      if (format.get.getEncoding().toString().startsWith("PCM_SIGN")) {
        for (i <- (0 until length) ) {
          audioBytes.update(i, (audioData(i)*127).toByte)
        }
      } else {
        for (i <- (0 until length) ) {
          audioBytes.update(i, (audioData(i)*127+127).toByte)
        }
      }
    }
  }



}

/*class SAudioFile ( path: String) {
  
  //val stream = new AudioInputStream( new FileInputStream( path ) ) 
  val file = new File( path )
  val stream = AudioSystem.getAudioInputStream( file ) 
  val format = stream.getFormat

  def getNumSamples() : Long = ( stream.getFrameLength * format.getFrameSize * 8 ) / format.getSampleSizeInBits / format.getChannels

  def getSamples( ) : Array[Double] = {
    val nbSamples = getNumSamples().toInt //end - begin;
    println( nbSamples )
    // nbBytes = nbSamples * sampleSizeinByte * nbChannels
    val nbBytes = nbSamples * (format.getSampleSizeInBits() / 8) * format.getChannels();

    val samples = new Array[Double](nbSamples)
    val inBuffer = new Array[Byte](nbBytes)
    // read bytes from audio file
    stream.read(inBuffer, 0, inBuffer.length);
    // decode bytes into samples. Supported encodings are:
    // PCM-SIGNED, PCM-UNSIGNED, A-LAW, U-LAW
    decodeBytes(inBuffer, samples);
    return samples
  }

  // Extract samples of a particular channel from interleavedSamples and
  // copy them into channelSamples
  def getChannelSamples(channel: Int, interleavedSamples: Array[Float], channelSamples: Array[Float]) = {
    val nbChannels = format.getChannels();
    for (i <- (0 until channelSamples.length)) {
      channelSamples.update(i, interleavedSamples(nbChannels*i + channel));
    }
  } 

  // Convenience method. Extract left and right channels for common stereo
  // files. leftSamples and rightSamples must be of size getSampleCount()
 def getStereoSamples(double[] leftSamples, double[] rightSamples) = {
    sampleCount = getNumSamples();
    double[] interleavedSamples = new double[(int)sampleCount*2];
    getInterleavedSamples(0, sampleCount, interleavedSamples);
    for (int i = 0; i < leftSamples.length; i++) {
      leftSamples[i] = interleavedSamples[2*i];
      rightSamples[i] = interleavedSamples[2*i+1];
    }        
  } ///
  // Private. Decode bytes of audioBytes into audioSamples
  def decodeBytes( audioBytes: Array[Byte], audioSamples: Array[Double]) {
    val sampleSizeInBytes = format.getSampleSizeInBits() / 8;
    println( "sample size in bytes: " + sampleSizeInBytes )

    val sampleBytes = new Array[Int](sampleSizeInBytes)
    var k = 0; // index in audioBytes
    val ratio = Math.pow(2., format.getSampleSizeInBits() - 1)
    
    for (i <- (0 until audioSamples.length)) {
      // collect sample byte in big-endian order
      if (format.isBigEndian()) {
        // bytes start with MSB
        for ( j <- ( 0 until sampleSizeInBytes)) {
          sampleBytes.update(j, audioBytes(k))
          k += 1
        }
      } else {
        // bytes start with LSB
        var j = sampleSizeInBytes - 1
        while( j >= 0) {
          sampleBytes.update(j, audioBytes(k))
          k += 1
          if (sampleBytes(j) != 0)
            j = j + 0;
          j -= 1
        }
      }
      // get integer value from bytes
      var ival:Int = 0;
      for ( j <- (0 until sampleSizeInBytes) ) {
        ival += sampleBytes(j);
        if (j < sampleSizeInBytes - 1) ival <<= 8;
      }
      // decode value
      val value = ival.toDouble / ratio;
      audioSamples.update(i, value)
    }
  }

  def play = AudioPlayer.player.start( stream )
  def stop = AudioPlayer.player.stop( stream )
    
}*/


