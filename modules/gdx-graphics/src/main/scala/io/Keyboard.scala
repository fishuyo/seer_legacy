
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

import actor._
import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
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

class Keyboard extends InputAdapter {

  implicit val system = System()
  implicit val materializer = ActorMaterializer()

  var _promise = Promise[Option[(Char,Char)]]()
  def promise = _promise

  val keypress = Source.unfoldAsync('\u0000'){
    case s => 
      promise.future
  }
  // keypress.map { c => println(c); c }.runWith( Sink.ignore )

  val key = Var('\u0000')
  val up = Var('\u0000')
  val down = Var('\u0000')
  var observing = List[Obs]()

  var callbacks = Map[Char,List[()=>Unit]]()
  var typedCallbacks = List[(Char)=>Unit]()
  val pfuncs = ListBuffer[PartialFunction[Char,Unit]]()
  val nullfunc:PartialFunction[Char,Unit] = { case _ => () }

  use()

  val keyStream = Source
    .actorRef[Char](bufferSize = 0, OverflowStrategy.fail)
    .mapMaterializedValue(bindKeyEvent)

  keyStream.map { c => println(c); c }.runWith( Sink.ignore )

  def bindKeyEvent(a:ActorRef){
    this.listen {
      case c => a ! c
    }
  }

  def clear() = { observing.foreach( _.kill() ); observing = List(); typedCallbacks = List[(Char)=>Unit]()}
  def use() = Inputs.add(this)
  def remove() = Inputs.remove(this)

  def listen(f:PartialFunction[Char,Unit]) = pfuncs += f

  def bind(s:String, f: ()=>Unit)(implicit ctx: Ctx.Owner){
    val k = s.charAt(0)
    observing = key.trigger {
      // println(key.now)
      if( key.now == k ) try{ 
        f()
      }catch{ case e:Exception => println(e) }
    } :: observing
  }

  def bindDown( s:String, f: ()=>Unit)(implicit ctx: Ctx.Owner) = {
    val k = s.charAt(0)
    observing = down.trigger { if( down.now == k ) f() } :: observing
  }
  def bindUp( s:String, f: ()=>Unit)(implicit ctx: Ctx.Owner) = {
    val k = s.charAt(0)
    observing = up.trigger { if( up.now == k ) f() } :: observing
  }
  def bindTyped(f:(Char=>Unit)){
    typedCallbacks = f :: typedCallbacks
  }

  override def keyTyped(k:Char) = {
    key.Internal.value = '\u0000' // make rx propogate even if same key
    key() = k
    typedCallbacks.foreach((f) => f(k))
    pfuncs.foreach( (f) => f.orElse(nullfunc).apply(k) )
    val p = promise
    _promise = Promise[Option[(Char,Char)]]()
    p.success(Some(k,k))
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

