package com.fishuyo
package io
package leap

import maths._

import collection.mutable.ListBuffer

import com.leapmotion.leap._

object Leap extends Listener {
  
  var controller:Controller  = _

  type Callback = (Frame) => Any
  val callbacks = new ListBuffer[Callback]()
  val callbacksEach = new ListBuffer[Callback]()


  var down = ListBuffer[(Int,Vec3)]()
  val pos = new Array[Vec3](20)

  var connected = false

	def connect(){
    if( connected ) return
	  controller = new Controller()
	  controller.addListener(this)
    connected = true
	}
  def disconnect(){
    if(!connected) return
    controller.removeListener(this)
    connected = false
  }


  override def onInit(c:Controller){ println("Leap Init..") }
  override def onConnect(c:Controller){ println("Leap Connected.") }
  override def onDisconnect(c:Controller){ println("Leap Disconnecting..") }
  override def onExit(c:Controller){ println("Leap Done.") }

  override def onFrame( c:Controller ) {
        
    val frame = c.frame();
    try{
      callbacks.foreach( _(frame) ) 
    } catch { case e:Exception => println(e)} 
  } 

  def clear() = { callbacks.clear();}
  def bind(f:Callback) = callbacks += f
  def bindEach(f:Callback) = callbacksEach += f
}