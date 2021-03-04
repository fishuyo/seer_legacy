
package seer
package io

import graphics._
import spatial.Nav

import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.input._
import com.badlogic.gdx.Gdx

import rx._

// import actor._
// import akka.actor._
// import akka.stream._
// import akka.stream.scaladsl._
import scala.concurrent._

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

// case class KeyboardEvent(key:String, modifiers:String)

class Keyboard extends InputAdapter {

  val key = Var('\u0000')
  val up = Var('\u0000')
  val down = Var('\u0000')

  val downCallbacks = ListBuffer[PartialFunction[Char,Unit]]()
  val eventCallbacks = ListBuffer[PartialFunction[String,Unit]]()
  val nullfunc:PartialFunction[Char,Unit] = { case _ => () }
  val nullfuncs:PartialFunction[String,Unit] = { case _ => () }

  use()

  def clear() = { downCallbacks.clear; eventCallbacks.clear}
  def use() = Inputs.add(this)
  def remove() = Inputs.remove(this)

  def listen(f:PartialFunction[Char,Unit]) = downCallbacks += f
  def event(f:PartialFunction[String,Unit]) = eventCallbacks += f

  override def keyTyped(k:Char) = {
    // key.Internal.value = '\u0000' // make rx propogate even if same key
    key() = k
    downCallbacks.foreach(_.orElse(nullfunc).apply(k))

    // var event = "" //collection.mutable.ListBuffer[String]()
    // if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)){
    //   println(s"hi: $k")
    //   event += s"shift-$k"
    // } else
    //   event += k.toString
    // eventCallbacks.foreach(_.orElse(nullfuncs).apply(event))
    false
  }
  
  override def keyDown(k:Int) = {
    var c = '\u0000'
    if(k >= Keys.A && k <= Keys.Z) c = (k+68).toChar
    else if(k >= Keys.NUM_0 && k <= Keys.NUM_9) c = (k+41).toChar
    down() = c
    false
  }

  override def keyUp(k:Int) = {
    val c = (k+68).toChar
    up() = c
    down() = '\u0000'
    false
  }
}

