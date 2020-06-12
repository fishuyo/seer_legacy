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

import rx._
import rx.async._
import rx.async.Platform._
import scala.concurrent.duration._

case class Finger(id:Int, pos:Vec2, vel:Vec2, size:Float, angle:Float, state:String)

class TrackpadState {
  var fingers = ListBuffer[Finger]()
  var pos = Vec2()
  var vel = Vec2()
  var size = 0f

  def count = fingers.length
}

object Trackpad extends Trackpad {
  def apply() = new Trackpad
  
  // val countSlow = Var(0)
  // var timeout = Timer(30 millis)
  // val obs1 = Trackpad.count.trigger{     
  //     timeout.kill()
  //     timeout = Timer(30 millis)
  //     timeout.triggerLater { countSlow() = Trackpad.count.now; timeout.kill() }
  // }
 
}

class Trackpad extends Observer {
  
  val callbacksNew = new ListBuffer[(TrackpadState)=>Unit]()

  val state = new TrackpadState
  var connected = false

  //RX
  // val fingers = Var[List[Finger]](List[Finger]())
  val count = Var(0) //Rx{ fingers().length }
  val xys = Array.fill(11)(Var(Vec2()))
  val vels = Array.fill(11)(Var(Vec2()))
  val sizes = Array.fill(11)(Var(0f))
  val angles = Array.fill(11)(Var(0f))
  val status = Array.fill(11)(Var(""))
  val xy = Var(Vec2())
  val vel = Var(Vec2())
  val size = Var(0f)
  val angle = Var(0f)

  connect()
  
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
  def update(obj:Observable, arg:Object){
    
    val f = arg.asInstanceOf[TFinger]
    
    val x = f.getX()
    val y = f.getY()
    val dx = f.getXVelocity()
    val dy = f.getYVelocity()

    val id:Int = f.getID() 
    val fstate:FingerState = f.getState()
    val fsize = f.getSize()
    val angRad = f.getAngleInRadians()
    val angDeg:Int = f.getAngle() //in Degrees
    val majorAxis = f.getMajorAxis()
    val minorAxis = f.getMinorAxis()

    val frame:Int = f.getFrame()
    val timestamp:Double = f.getTimestamp()

    // val ts = (timestamp * 1000000).toLong
    val st = fstate match {
      // case FingerState.HOVER => "hover"
      // case FingerState.TAP => "tap"
      case FingerState.PRESSED => "down" //"pressed"
      // case FingerState.PRESSING => "down" //"pressing"        
      case FingerState.RELEASING => "up" //"releasing"          
      // case FingerState.RELEASED => "released"
      // case FingerState.UNKNOWN =>
      // case FingerState.UNKNOWN_1 => 
      case s => ""
    }

    val finger = Finger(id,Vec2(x,y),Vec2(dx,dy),fsize,angRad,st)

    var indx = state.fingers.indexWhere( _.id == id )
    // println(fstate + " " + indx)

    if(fstate == FingerState.PRESSED){
      if(indx == -1){
        state.fingers += finger
        indx = state.fingers.length - 1
      }else state.fingers(indx) = finger.copy(state="drag") 
    } else if(fstate == FingerState.RELEASED){
      state.fingers = state.fingers.filterNot( _.id == id )  
    }

    count() = state.fingers.length

    if(indx < 0) indx = 10 // fix because finger only added when pressed

    fstate match {
      case FingerState.HOVER => status(indx)() = "hover"
      case FingerState.TAP => status(indx)() = "tap"
      case FingerState.PRESSED => status(indx)() = "pressed"
      case FingerState.PRESSING => status(indx)() = "pressing"        
      case FingerState.RELEASING => status(indx)() = "releasing"          
      case FingerState.RELEASED => status(indx)() = "released"
      case FingerState.UNKNOWN =>
      case FingerState.UNKNOWN_1 => 
      case s => () //println(s + " " + indx)
    }

    xys(indx)() = finger.pos
    vels(indx)() = finger.vel
    sizes(indx)() = finger.size
    angles(indx)() = angDeg

    val sumpos = Vec2()
    val sumvel = Vec2()
    var sumsize = 0f
    var sumangle = 0f
    state.fingers.foreach { case f =>
      sumpos += f.pos
      sumvel += f.vel
      sumsize += f.size
      sumangle += angDeg
    }
    if(state.count > 0){
      state.pos = sumpos / state.count
      state.vel = sumvel / state.count
      state.size = sumsize / state.count
      xy() = state.pos
      vel() = state.vel
      size() = state.size
      angle() = sumangle / state.count
    }

    try{
      callbacksNew.foreach( _(state) )
    } catch { case e:Exception => println(e) }

  } 

  def non()() = {}

  def clear() = { 
    callbacksNew.clear()
    // callbacks.keys.foreach(callbacks(_) = List())
    // callbacksEach.clear()
  }

  def bind(f:(TrackpadState)=>Unit) = callbacksNew += f
  def listen(f:(TrackpadState)=>Unit) = callbacksNew += f
  // def bindP(f:PartialFunction[TrackpadState,Unit]) = callbacksNew += f

  // def bind( s:String, f:Callback ) = callbacks(s) = f :: callbacks.getOrElseUpdate(s,List())

  // def bind(f:Callback) = callbacks("multi") = f :: callbacks.getOrElseUpdate("multi",List())
  // def bindEach(f:Callback) = callbacksEach += f


}


