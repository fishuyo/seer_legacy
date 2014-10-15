
package com.fishuyo.seer
package io

import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.input._

import rx._


object Keyboard extends InputAdapter {
  // var charCallbacks = Map[Char,()=>Unit]()
  // var charUpCallbacks = Map[Char,()=>Unit]()
  // var callbacks = Map[String,()=>Unit]()

  val key = Var('\0')
  val up = Var('\0')
  val down = Var('\0')
  var observing = List[Obs]()

  // def non()() = {}

  def clear() = { observing.foreach( _.kill() ); observing = List(); /*charCallbacks.clear(); charUpCallbacks.clear(); callbacks.clear();*/ Inputs.removeProcessor(this) }
  def use() = Inputs.addProcessor(this)

  def bind( s:String, f:()=>Unit){
    val k = s.charAt(0)
    observing = Obs(key,skipInitial=true){ 
      if( key() == k ) try{ 
        f()
      }catch{ case e:Exception => println(e) }
    } :: observing
    // if( s.length == 1) charCallbacks += s.charAt(0) -> f
    // else callbacks += s -> f
  }
  def bindDown( s:String, f:()=>Unit) = {
    val k = s.charAt(0)
    observing = Obs(down,skipInitial=true){ if( down() == k ) f() } :: observing
  }
  def bindUp( s:String, f:()=>Unit) = {
    val k = s.charAt(0)
    observing = Obs(up,skipInitial=true){ if( up() == k ) f() } :: observing
  }
  override def keyTyped(k:Char) = {
    key() = k
    // try{
      // charCallbacks.getOrElse(k, non()_)()
    // } catch { case e:Exception => println(e) }
    false
  }
  override def keyDown(k:Int) = {
    val c = (k+68).toChar
    down() = c
    // try{
      // charUpCallbacks.getOrElse(c, non()_)()
    // } catch { case e:Exception => println(e) }
    false
  }
  override def keyUp(k:Int) = {
    val c = (k+68).toChar
    up() = c
    // try{
      // charUpCallbacks.getOrElse(c, non()_)()
    // } catch { case e:Exception => println(e) }
    false
  }
}

