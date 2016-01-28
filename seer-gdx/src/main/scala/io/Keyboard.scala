
package com.fishuyo.seer
package io

import graphics._
import spatial.Nav

import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.input._

import rx._

object Keyboard extends Keyboard {

  def apply() = new Keyboard 
  
  val cameraNavInput = new KeyboardNavInput(Camera.nav)
  def bindCamera() = Inputs += cameraNavInput
  def unbindCamera() = Inputs -= cameraNavInput

  def bindCamera(c:NavCamera){
    unbindCamera()
    cameraNavInput.nav = c.nav
    bindCamera()
  }
  def bindNav(nav:Nav) = Inputs += new KeyboardNavInput(nav)
}

class Keyboard extends InputAdapter {
  val key = Var('\0')
  val up = Var('\0')
  val down = Var('\0')
  var observing = List[Obs]()

  var typedCallbacks = List[(Char)=>Unit]()

  use()

  def clear() = { observing.foreach( _.kill() ); observing = List(); }
  def use() = Inputs.add(this)
  def remove() = Inputs.remove(this)

  def bind(s:String, f:()=>Unit){
    val k = s.charAt(0)
    observing = Obs(key,skipInitial=true){ 
      if( key() == k ) try{ 
        f()
      }catch{ case e:Exception => println(e) }
    } :: observing
  }

  def bindDown( s:String, f:()=>Unit) = {
    val k = s.charAt(0)
    observing = Obs(down,skipInitial=true){ if( down() == k ) f() } :: observing
  }
  def bindUp( s:String, f:()=>Unit) = {
    val k = s.charAt(0)
    observing = Obs(up,skipInitial=true){ if( up() == k ) f() } :: observing
  }
  def bindTyped(f:(Char=>Unit)){
    typedCallbacks = f :: typedCallbacks
  }

  override def keyTyped(k:Char) = {
    key() = k
    typedCallbacks.foreach((f) => f(k))
    false
  }
  override def keyDown(k:Int) = {
    var c = '\0'
    if(k >= Keys.A && k <= Keys.Z) c = (k+68).toChar
    else if(k >= Keys.NUM_0 && k <= Keys.NUM_9) c = (k+41).toChar
    down() = c
    false
  }
  override def keyUp(k:Int) = {
    val c = (k+68).toChar
    up() = c
    down() = '\0'
    false
  }
}

