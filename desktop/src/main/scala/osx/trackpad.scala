package com.fishuyo
package io

import maths._

import collection.mutable.ListBuffer

import java.util.Observer;
import java.util.Observable;

import com.alderstone.multitouch.mac.touchpad.TouchpadObservable;
import com.alderstone.multitouch.mac.touchpad.Finger;
import com.alderstone.multitouch.mac.touchpad.FingerState;

object Trackpad extends Observer{
  
  type Callback = (Int, Array[Float]) => Any
  val callbacks = new ListBuffer[Callback]()
  val callbacksEach = new ListBuffer[Callback]()


  var down = ListBuffer[(Int,Vec3)]()
  val pos = new Array[Vec3](20)

  var connected = false

	def connect(){
    if( connected ) return
	  val tpo = TouchpadObservable.getInstance()
	  tpo.addObserver(this)
    connected = true
	}
  def disconnect(){
    if(!connected) return
    val tpo = TouchpadObservable.getInstance()
    tpo.deleteObserver(this)
    connected = false
  }

  // Touchpad Multitouch update event handler, 
  // called on single MT Finger event
  def update( obj:Observable ,arg:Object ) {
          
          // The event 'arg' is of type: com.alderstone.multitouch.mac.touchpad.Finger
          val f = arg.asInstanceOf[Finger]
          
          val frame:Int = f.getFrame();
          val timestamp:Double = f.getTimestamp();
          val id:Int = f. getID(); 
          val state:FingerState = f.getState();
          val size = f.getSize();
          val angRad = f.getAngleInRadians();
          val angle:Int = f.getAngle(); // return in Degrees
          val majorAxis = f.getMajorAxis();
          val minorAxis = f.getMinorAxis();
          val x = f.getX();
          val y = f.getY();
          val dx = f.getXVelocity();
          val dy = f.getYVelocity();


          state match {
            case FingerState.PRESSED =>           
                val indx = down.indexWhere( _._1 == id );
                if( indx != -1) down(indx) = ((id,Vec3(x,y,0.f))) else down += ((id,Vec3(x,y,0.f)))
                
            case FingerState.RELEASED => down = down.filterNot( _._1 == id )
            case _ => false
          }
          // pos(id) = Vec3(x,y,0.f)

          // val indices = down.zipWithIndex.collect{ case (true,i) => i }
          // val p = indices.map( pos(_) )

          val p = down.map( _._2 )

          val centroid = p.sum / p.length

          val coords = p.flatMap( (v:Vec3) => List(v.x,v.y) )

          val data = Array.concat( Array(centroid.x,centroid.y,dx,dy,size), coords.toArray)

          try{
            callbacks.foreach( _(p.length, data) )
            callbacksEach.foreach( _(id,Array(x,y,dx,dy,size,angle)) )
          } catch { case e:Exception => println(e) }

          // println( "frame="+frame + 
          //                "\ttimestamp=" + timestamp + 
          //                "\tid=" +  id + 
          //                "\tstate=" + state +
          //                "\tsize=" + size  +
          //                "\tx,y=(" + x+ "," +  y+ 
          //                ")\tdx,dy=(" + dx + "," + dy +")\t" +
          //                "angle=" + angle  + 
          //                "majAxis=" + majorAxis  +
          //                "\tminAxis=" + minorAxis);
  } 

  def clear() = { callbacks.clear(); callbacksEach.clear() }
  def bind(f:Callback) = callbacks += f
  def bindEach(f:Callback) = callbacksEach += f
}