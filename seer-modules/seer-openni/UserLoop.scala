
package com.fishuyo.seer
package openni

import collection.mutable.ListBuffer
import collection.mutable.ArrayBuffer

import com.twitter.chill.KryoInjection
import scala.util.Success

class UserLoop {

  var (recording,playing,stacking,reversing,undoing) = (false,false,false,false,false)
  var frames = new ArrayBuffer[ListBuffer[User]]()
  var frame = 0f
  var speed = 1f
  var alpha = 0.3f
  var beta = 1f-alpha

  def play(){ playing = true; }
  def togglePlay() = playing = !playing
  def stop(){ playing = false; recording = false; stacking = false}
  def rewind(){ frame = 0f }
  def record(){ recording = true; }
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

  def stack() = { stacking = !stacking }
  def reverse() = reversing = !reversing
  def reverse(b:Boolean) = reversing = b
  def clear() = {
    stop()
    try{
    frames.foreach( _.clear )
    frames.clear
    } catch { case e:Exception => println(e.getMessage())}

    frames = new ArrayBuffer[ListBuffer[User]]()
    frame = 0f
  }

  def setSpeed(v:Float) = speed = v
  def setAlpha(a:Float) = {alpha = a; beta = 1f-alpha}
  def setAlphaBeta(a:Float,b:Float) = {alpha = a; beta = b }

  def io(in:ListBuffer[User], out:ListBuffer[User]){

    if( recording ){
      frames += in
    }

    if(playing){
      if( reversing ){
        frame -= speed
      } else {
        frame += speed
      }
    }
    
    if(frame < 0f) frame = frames.length-1
    else if(frame > frames.length-1) frame = 0f

    if(stacking){
      if( frames.length == 0) return
      var from = frame
      var to = (if(reversing) frame-speed else frame+speed)
      if( from > to){
        val tmp = from
        from = to
        to = tmp
      }
      for( i<-(from.toInt until to.toInt)){
        var idx = i
        if(i < 0f) idx = frames.length + i
        else if(i > frames.length-1) idx = i - frames.length

        if( frames.length > 0){
          // Core.addWeighted(in, alpha, images(idx), beta, 0.0, dest)
          // in.foreach( _.alpha *= alpha )
          // frames(idx).foreach( _.alpha *= beta)
          // frames(idx) ++= in
          frames(idx).append(in: _*)
        }
      }
    }

    if( frames.length > 0) out ++= frames(frame.toInt)
  }


  def load(filename:String){
    import java.io._
    val bis = new BufferedInputStream(new FileInputStream(filename))
    val aval = bis.available
    val buffer = new Array[Byte](aval)
    val red = bis.read(buffer)
    println(s"read $red bytes of $aval")

    var res:Option[ArrayBuffer[ListBuffer[User]]] = None
    // var user:Option[User] = None
    val decode = KryoInjection.invert(buffer)
    decode match {
      case Success(u:ArrayBuffer[ListBuffer[User]]) => res = Some(u.clone)
      case m => println("Invert failed!" + m + " " + m.getClass.getSimpleName)
    }
    if(res.isDefined) frames = res.get
  }
  def save(){
    import java.io._
    val form = new java.text.SimpleDateFormat("yyyy-MM-dd-HH.mm.ss")
    val filename = form.format(new java.util.Date()) + ".bin" 

    val bytes = KryoInjection(frames)
    val bos = new BufferedOutputStream(new FileOutputStream(filename))
    Stream.continually(bos.write(bytes))
    bos.close()
  }

}