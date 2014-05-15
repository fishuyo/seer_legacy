package com.fishuyo.seer
package io

import maths._

import collection.mutable.Map
import collection.mutable.ListBuffer

import java.util.Observer;
import java.util.Observable;

import com.alderstone.multitouch.mac.touchpad.TouchpadObservable;
import com.alderstone.multitouch.mac.touchpad.Finger;
import com.alderstone.multitouch.mac.touchpad.FingerState;

import com.badlogic.gdx.input.TGestureDetector
import com.badlogic.gdx.math.Vector2

object Trackpad extends Observer{
  
  type Callback = (Int,Array[Float])=>Unit
  var callbacks = Map[String,List[Callback]]()
  
  // val callbacks = new ListBuffer[Callback]()
  val callbacksEach = new ListBuffer[Callback]()


  var down = ListBuffer[(Int,Vec3)]()
  val pos = new Array[Vec3](20)

  var connected = false

  // val down = ListBuffer.fill(20)(false)

  callbacks += ("multi" -> List()) // num [pos]
  callbacks += ("tap" -> List())
  callbacks += ("long" -> List())
  callbacks += ("fling" -> List())
  callbacks += ("pan" -> List())
  callbacks += ("zoom" -> List())
  callbacks += ("pinch" -> List())

  class Gesture extends TGestureDetector.GestureAdapter {
    override def tap (x:Float, y:Float, count:Int, button:Int) = {
      try { callbacks("tap").foreach( _(count,Array(x,y)) ) }
      catch { case e:Exception => println(e) }
      false;
    }
    override def longPress (x:Float, y:Float) = {
      try { callbacks("long").foreach( _(0,Array(x,y)) ) }
      catch { case e:Exception => println(e) }
      false;
    }
    override def fling (velocityX:Float, velocityY:Float, button:Int) = {
      try { callbacks("fling").foreach( _(button,Array(velocityX,velocityY)) ) }
      catch { case e:Exception => println(e) }
      false;
    }
    override def pan (x:Float, y:Float, deltaX:Float, deltaY:Float) = {
      try { callbacks("pan").foreach( _(0,Array(x,y,deltaX,deltaY)) ) }
      catch { case e:Exception => println(e) }
      false;
    }
    override def zoom (originalDistance:Float, currentDistance:Float) = {
      try { callbacks("zoom").foreach( _(0,Array(originalDistance,currentDistance)) ) }
      catch { case e:Exception => println(e) }
      false;
    }
    override def pinch (initialFirstPointer:Vector2, initialSecondPointer:Vector2, firstPointer:Vector2, secondPointer:Vector2) = {
      try { callbacks("pinch").foreach( _(0,Array(initialFirstPointer.x,initialFirstPointer.y,initialSecondPointer.x,initialSecondPointer.y,firstPointer.x,firstPointer.y,secondPointer.x,secondPointer.y)) ) }
      catch { case e:Exception => println(e) }
      false;
    }
  }
  val gesture = new TGestureDetector(new Gesture())


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

          val ts = (timestamp * 1000000).toLong
          // println(ts)
          // println(timestamp)

          state match {
            case FingerState.PRESSED =>           
                val indx = down.indexWhere( _._1 == id );
                if( indx != -1){
                  down(indx) = ((id,Vec3(x,y,0.f)))
                  // gesture.touchDown(x*800.f,y*800.f,indx,0,ts)
                } else {
                  down += ((id,Vec3(x,y,0.f)))
                  // gesture.touchDragged(x*800.f,y*800.f,down.length-1,ts)
                }

                
            case FingerState.RELEASED => 
              // val indx = down.indexWhere( _._1 == id );
              down = down.filterNot( _._1 == id )
              // if( indx != -1 ) gesture.touchUp(x*800.f,y*800.f,indx,0,ts)
            case _ => ()
          }
          // pos(id) = Vec3(x,y,0.f)

          // val indices = down.zipWithIndex.collect{ case (true,i) => i }
          // val p = indices.map( pos(_) )

          val p = down.map( _._2 )

          val centroid = p.sum / p.length

          val coords = p.flatMap( (v:Vec3) => List(v.x,v.y) )

          val data = Array.concat( Array(centroid.x,centroid.y,dx,dy,size), coords.toArray)

          try{
            callbacks("multi").foreach( _(p.length, data) )
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

  def non()() = {}

  def clear() = { 
    callbacks.keys.foreach(callbacks(_) = List())
    callbacksEach.clear()
    // Inputs.removeProcessor(this)
    // Inputs.removeProcessor(gesture)
  }
  // def use() = { Inputs.addProcessor(this); Inputs.addProcessor(gesture) }

  def bind( s:String, f:Callback ) = callbacks(s) = f :: callbacks.getOrElseUpdate(s,List())

  def bind(f:Callback) = callbacks("multi") = f :: callbacks.getOrElseUpdate("multi",List())
  def bindEach(f:Callback) = callbacksEach += f
}

// class TrackpadState{

//   var fingers = Map[Int,FingerState]
//   var down = ListBuffer[(Int,Vec3)]()
//   val pos = new Array[Vec2](20)
// }

// class Fing{
//   var xy = Vec2()

// }

