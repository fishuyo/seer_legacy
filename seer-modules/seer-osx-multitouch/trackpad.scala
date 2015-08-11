package com.fishuyo.seer
package io

import spatial._

import collection.mutable.Map
import collection.mutable.ListBuffer

import java.util.Observer;
import java.util.Observable;

import com.alderstone.multitouch.mac.touchpad.TouchpadObservable;
import com.alderstone.multitouch.mac.touchpad.{Finger => TFinger};
import com.alderstone.multitouch.mac.touchpad.FingerState;

// import com.badlogic.gdx.input.TGestureDetector
// import com.badlogic.gdx.math.Vector2


case class Finger(id:Int, pos:Vec2, vel:Vec2, size:Float, angle:Float)

class TrackpadState {
  var fingers = ListBuffer[Finger]()
  var pos = Vec2()
  var vel = Vec2()
  var size = 0f

  def count = fingers.length
}


object Trackpad extends Observer {
  

  type Callback = (Int,Array[Float])=>Unit

  var callbacks = Map[String,List[Callback]]()
  
  // val callbacks = new ListBuffer[Callback]()
  val callbacksEach = new ListBuffer[Callback]()


  val callbacksNew = new ListBuffer[(TrackpadState)=>Unit]()

  val state = new TrackpadState

  var down = ListBuffer[(Int,Vec3)]()
  val pos = new Array[Vec3](20)

  var connected = false

  connect()
  
  // val down = ListBuffer.fill(20)(false)

  callbacks += ("multi" -> List()) // num [pos]
  callbacks += ("tap" -> List())
  callbacks += ("long" -> List())
  callbacks += ("fling" -> List())
  callbacks += ("pan" -> List())
  callbacks += ("zoom" -> List())
  callbacks += ("pinch" -> List())

  // class Gesture extends TGestureDetector.GestureAdapter {
  //   override def tap (x:Float, y:Float, count:Int, button:Int) = {
  //     try { callbacks("tap").foreach( _(count,Array(x,y)) ) }
  //     catch { case e:Exception => println(e) }
  //     false;
  //   }
  //   override def longPress (x:Float, y:Float) = {
  //     try { callbacks("long").foreach( _(0,Array(x,y)) ) }
  //     catch { case e:Exception => println(e) }
  //     false;
  //   }
  //   override def fling (velocityX:Float, velocityY:Float, button:Int) = {
  //     try { callbacks("fling").foreach( _(button,Array(velocityX,velocityY)) ) }
  //     catch { case e:Exception => println(e) }
  //     false;
  //   }
  //   override def pan (x:Float, y:Float, deltaX:Float, deltaY:Float) = {
  //     try { callbacks("pan").foreach( _(0,Array(x,y,deltaX,deltaY)) ) }
  //     catch { case e:Exception => println(e) }
  //     false;
  //   }
  //   override def zoom (originalDistance:Float, currentDistance:Float) = {
  //     try { callbacks("zoom").foreach( _(0,Array(originalDistance,currentDistance)) ) }
  //     catch { case e:Exception => println(e) }
  //     false;
  //   }
  //   override def pinch (initialFirstPointer:Vector2, initialSecondPointer:Vector2, firstPointer:Vector2, secondPointer:Vector2) = {
  //     try { callbacks("pinch").foreach( _(0,Array(initialFirstPointer.x,initialFirstPointer.y,initialSecondPointer.x,initialSecondPointer.y,firstPointer.x,firstPointer.y,secondPointer.x,secondPointer.y)) ) }
  //     catch { case e:Exception => println(e) }
  //     false;
  //   }
  // }
  // val gesture = new TGestureDetector(new Gesture())


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
          
          val f = arg.asInstanceOf[TFinger]
          
          val frame:Int = f.getFrame();
          val timestamp:Double = f.getTimestamp();

          val id:Int = f. getID(); 
          val fstate:FingerState = f.getState();
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

          val finger = Finger(id,Vec2(x,y),Vec2(dx,dy),size,angRad)

          fstate match {
            case FingerState.PRESSED =>           
                // val indx = down.indexWhere( _._1 == id );
                val indx = state.fingers.indexWhere( _.id == id );
                if( indx != -1){
                  // down(indx) = ((id,Vec3(x,y,0f)))
                  state.fingers(indx) = finger
                  // gesture.touchDown(x*800f,y*800f,indx,0,ts)
                } else {
                  // down += ((id,Vec3(x,y,0f)))
                  state.fingers += finger
                  // gesture.touchDragged(x*800f,y*800f,down.length-1,ts)
                }

                
            case FingerState.RELEASED => 
              // val indx = down.indexWhere( _._1 == id );
              // down = down.filterNot( _._1 == id )
              state.fingers = state.fingers.filterNot( _.id == id )
              // if( indx != -1 ) gesture.touchUp(x*800f,y*800f,indx,0,ts)
            case _ => ()
          }

          val pos = Vec2()
          val vel = Vec2()
          var sumsize = 0f
          state.fingers.foreach { case f =>
            pos += f.pos
            vel += f.vel
            sumsize += f.size
          }
          if(state.count > 0){
            state.pos = pos / state.count
            state.vel = vel / state.count
            state.size = sumsize / state.count
          }

          // pos(id) = Vec3(x,y,0f)

          // val indices = down.zipWithIndex.collect{ case (true,i) => i }
          // val p = indices.map( pos(_) )

          // val p = down.map( _._2 )

          // val centroid = p.sum / p.length

          // val coords = p.flatMap( (v:Vec3) => List(v.x,v.y) )

          // val data = Array.concat( Array(centroid.x,centroid.y,dx,dy,size), coords.toArray)

          try{
            callbacksNew.foreach( _(state) )
            // callbacks("multi").foreach( _(p.length, data) )
            // callbacksEach.foreach( _(id,Array(x,y,dx,dy,size,angle)) )
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
    callbacksNew.clear()
    callbacks.keys.foreach(callbacks(_) = List())
    callbacksEach.clear()
    // Inputs.removeProcessor(this)
    // Inputs.removeProcessor(gesture)
  }
  // def use() = { Inputs.addProcessor(this); Inputs.addProcessor(gesture) }

  def bind(f:(TrackpadState)=>Unit) = callbacksNew += f
  // def bind(f:PartialFunction[TrackpadState,Unit]) = callbacksNew += f

  // def bind( s:String, f:Callback ) = callbacks(s) = f :: callbacks.getOrElseUpdate(s,List())

  // def bind(f:Callback) = callbacks("multi") = f :: callbacks.getOrElseUpdate("multi",List())
  // def bindEach(f:Callback) = callbacksEach += f


}


