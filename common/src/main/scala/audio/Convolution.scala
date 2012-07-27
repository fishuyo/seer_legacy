
package com.fishuyo
package audio
import ray._

import actors.Actor
import actors._
import edu.emory.mathcs.jtransforms.fft._

import de.sciss.synth.io._


object Convolver extends Actor {

  val fft = new FloatFFT_1D(2048)
  val impulses = Array.fill(10000){0.f}
  val samples = Array.fill(2048){0.f}
 
  val outSpec = AudioFile.readSpec( "test.aiff" )
  val outfile = AudioFile.openWrite( "output.aif", outSpec )

  def act() {
    //while(true){
    loop{
    react{
      case Go => { 
        AudioOut.source.read(samples); 
        //this(samples);
        AudioOut.write( samples, 0, 1024 )
      }
      case Write => {
        RayTracer ! Stop

    //for( i <- ( 0 until 2048 ) ){
    //  samples.update(i, (2048 - i ).toFloat / 2048.f )
    //}
        val f: Frames = Array[Array[Float]]( impulses )
        outfile.write( f )
      }

    }
    }
  }

  def addImpulse( i:(Float,Float) ) = {
    val speed = 343.f
    val t:Float = i._1 / speed * AudioOut.sampleRate.toFloat //delay in samples
    val v = i._2 //attenuation
    
    val idx = t.toInt
    if( idx >= 10000 || idx < 0 ) println( "sampleIdx: " + idx )
    else impulses.update(idx, v + impulses(idx)) 
  }

  def apply(buffer: Array[Float] ) = {
    
    //for( i <- ( 0 until 1024 ) ){
    //  samples.update(i, (1024 - i ).toFloat / 1024.f )
    //}
    fft.realForwardFull( impulses )
    fft.realForwardFull( buffer )

    for( i <- ( 0 until buffer.length by 2)){
      buffer.update( i, buffer(i) * impulses(i) - buffer(i+1) * impulses(i+1))
      buffer.update( i+1, buffer(i+1) * impulses(i) + buffer(i) * impulses(i+1))
    }

    fft.complexInverse( buffer, true )

    
  }

}

