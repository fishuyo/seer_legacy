
package com.fishuyo.seer
package audio

import akka.actor._
import akka.actor.Props
import com.typesafe.config.ConfigFactory

import collection.mutable.ListBuffer

import de.sciss.synth.io._

import org.jaudiolibs.jnajack._
import java.util.EnumSet

object JackAudio extends AudioInterface {
  val jack = Jack.getInstance
  var inPorts = Array[JackPort]()
  var outPorts = Array[JackPort]()

  val callback = new JackProcessCallback {
  	def process(client:JackClient, nframes:Int) = {
      
      try{ inPorts(0).getFloatBuffer.get(in(0)) }
      catch{ case e:Exception => ()}
  //       paIn.getFloatBuffer.get(in(0))

      // zero output buffers
      for( c <- (0 until channelsOut))
        for( i <- (0 until bufferSize)) out(c)(i) = 0f

      // call audio callbacks
      ioBuffer.reset
        sources.foreach{ case s=> s.audioIO( ioBuffer ); ioBuffer.reset }

      // if playThru set add input to output
      if( playThru ) AudioPass.audioIO( ioBuffer ) //in,out,channelsOut,bufferSize)

      // copy output buffers to interleaved
      // for( i<-(0 until bufferSize)){
      //   for( c<-(0 until channelsOut)){
      //     outInterleaved(channelsOut*i + c) = out(c)(i)*gain
      //   }
      // }

      // write samples to audio device
      // paOut.getFloatBuffer.put(outInterleaved)
      out.zip(outPorts).foreach { case (buf,port) =>
        port.getFloatBuffer.put(buf)
      }

      if(recording){
        // if recordThru set and not already done add input to output
        if( recordThru && !playThru ) AudioPass.audioIO( ioBuffer ) //in,out,channelsOut,bufferSize)
        outFile.write(out,0,bufferSize)
      }
      true
  	}
  }

  override def init(){
    var o = EnumSet.of(JackOptions.JackNullOption)
    var s = EnumSet.of(JackStatus.JackFailure)
    val client = jack.openClient("seer", o, s)

    client.setProcessCallback(callback)    

    inPorts = (1 to channelsIn).map { case i => client.registerPort(s"in$i", JackPortType.AUDIO, JackPortFlags.JackPortIsInput)}.toArray
    outPorts = (1 to channelsOut).map { case i => client.registerPort(s"out$i", JackPortType.AUDIO, JackPortFlags.JackPortIsOutput)}.toArray

    client.activate()

    var flags = EnumSet.of(JackPortFlags.JackPortIsInput, JackPortFlags.JackPortIsPhysical)
    val hwOuts = jack.getPorts(client, null, JackPortType.AUDIO, flags)
    flags = EnumSet.of(JackPortFlags.JackPortIsOutput, JackPortFlags.JackPortIsPhysical)
    val hwIns = jack.getPorts(client, null, JackPortType.AUDIO, flags)

    hwIns.zip(inPorts).foreach { case (src,dst) =>
      jack.connect(client, src, dst.getName)
      println(s"JACK: connecting $src to ${dst.getName}")
    }
    outPorts.zip(hwOuts).foreach { case (src,dst) =>
      jack.connect(client, src.getName, dst)
      println(s"JACK: connecting ${src.getName} to $dst")
    }

    super.init
  }

  // override def start() = JPA.startStream 
  // override def stop() = JPA.stopStream


}



