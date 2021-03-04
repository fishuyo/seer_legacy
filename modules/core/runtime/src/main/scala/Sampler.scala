
package seer
package runtime

import collection.mutable.Buffer
import collection.mutable.ListBuffer
import collection.mutable.ArrayBuffer

import com.twitter.chill.KryoInjection
import scala.util.Success
import scala.reflect.ClassTag


class Sampler[T: ClassTag] {

  var (recording,playing,stacking,reversing) = (false,false,false,false)
  var frames = new ArrayBuffer[T]()
  var frame = 0f
  var frameSpeed = 1f

  def play() = playing = true
  def togglePlay() = playing = !playing
  def stop() = { playing = false; recording = false; stacking = false}
  def rewind() = frame = 0f
  def record() = recording = true
  
  def toggleRecord() = {
    if(!recording){
      recording = true
      playing = true
    }else{
      recording = false
      playing = true
    }
    recording
  }

  def stack() = stacking = !stacking 
  def reverse() = reversing = !reversing
  def reverse(b:Boolean) = reversing = b
  def speed(v:Float) = frameSpeed = v

  def clear() = {
    stop()
    frames.clear
    frame = 0f
  }

  def now():Option[T] = {
    if( frames.length > 0 && playing) Some(frames(frame.toInt))
    else None
  }

  def io(in:T):Option[T] = {

    if(recording) frames += in

    if(playing){
      if(reversing) frame -= frameSpeed
      else frame += frameSpeed
    }
    
    if(frame < 0f) frame = frames.length-1
    else if(frame > frames.length-1) frame = 0f

    if(stacking){
      //TODO 
    }

    if(frames.length > 0 && playing) Some(frames(frame.toInt))
    else None
  }


  // def load(filename:String){
  //   import java.io._
  //   val bis = new BufferedInputStream(new FileInputStream(filename))
  //   val aval = bis.available
  //   val buffer = new Array[Byte](aval)
  //   val red = bis.read(buffer)
  //   println(s"read $red bytes of $aval")

  //   var res:Option[ArrayBuffer[Buffer[User]]] = None
  //   // var user:Option[User] = None
  //   val decode = KryoInjection.invert(buffer)
  //   decode match {
  //     case Success(u:ArrayBuffer[Buffer[User]]) => res = Some(u.clone)
  //     case m => println("Invert failed!" + m + " " + m.getClass.getSimpleName)
  //   }
  //   if(res.isDefined) frames = res.get
  //   bis.close()
  // }

  // def save(){
  //   import java.io._
  //   val form = new java.text.SimpleDateFormat("yyyy-MM-dd-HH.mm.ss")
  //   val filename = form.format(new java.util.Date()) + ".k.bin" 

  //   val bytes = KryoInjection(frames)
  //   val bos = new BufferedOutputStream(new FileOutputStream(filename))
  //   Stream.continually(bos.write(bytes))
  //   bos.close()
  // }

}