
package com.fishuyo
package io

import graphics._
import maths._
import spatial._
//import ray._

// import java.awt.event.KeyEvent
// import java.awt.event.KeyListener
// import java.awt.event.MouseEvent
// import java.awt.event.MouseListener
// import java.awt.event.MouseMotionListener

import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Input.Keys

class Input extends InputAdapter

object Inputs extends InputMultiplexer

object Keyboard extends InputAdapter {
	var charCallbacks = Map[Char,()=>Unit]()
	var charUpCallbacks = Map[Char,()=>Unit]()
	var callbacks = Map[String,()=>Unit]()
	def non()() = {}

	def clear() = { charCallbacks.clear(); charUpCallbacks.clear(); callbacks.clear(); Inputs.removeProcessor(this) }
	def use() = Inputs.addProcessor(this)
	def bind( s:String, f:()=>Unit) = {
		if( s.length == 1) charCallbacks += s.charAt(0) -> f
		else callbacks += s -> f
	}
	def bindUp( s:String, f:()=>Unit) = {
		if( s.length == 1) charUpCallbacks += s.charAt(0) -> f
		//else callbacks += s -> f
	}
	override def keyTyped(k:Char) = {
		try{
			charCallbacks.getOrElse(k, non()_)()
		} catch { case e:Exception => println(e) }
		false
	}
	override def keyUp(k:Int) = {
		val c = (k+68).toChar
		try{
			charUpCallbacks.getOrElse(c, non()_)()
		} catch { case e:Exception => println(e) }
		false
	}
}

object Mouse extends InputAdapter {
	type Callback = (Int,Int,Int,Int)=>Unit
	var callbacks = Map[String,List[Callback]]()
	callbacks += ("up" -> List())
	callbacks += ("down" -> List())
	callbacks += ("drag" -> List())
	callbacks += ("move" -> List())
	callbacks += ("scroll" -> List())

	def non()() = {}

	def clear() = { callbacks.keys.foreach(callbacks(_) = List()); Inputs.removeProcessor(this) }
	def use() = Inputs.addProcessor(this)

	def bind( s:String, f:Callback ) = callbacks(s) = f :: callbacks.getOrElseUpdate(s,List())	

  override def touchUp( screenX:Int, screenY:Int, pointer:Int, button:Int) = {
    try { callbacks("up").foreach( _(screenX,screenY,pointer,button) ) }
    catch { case e:Exception => println(e) }
    false
  }
 	override def touchDown( screenX:Int, screenY:Int, pointer:Int, button:Int) = {
    try{ callbacks("down").foreach( _(screenX,screenY,pointer,button) ) }
    catch { case e:Exception => println(e) }
    false
	}
  override def touchDragged( screenX:Int, screenY:Int, pointer:Int) = {
    try{ callbacks("drag").foreach( _(screenX,screenY,pointer,0) ) }
    catch { case e:Exception => println(e) }
    false
  }

  // mouse only
  override def mouseMoved( screenX:Int, screenY:Int ) = {
    try { callbacks("move").foreach( _(screenX,screenY,0,0) ) }
    catch { case e:Exception => println(e) }
    false
  }
  override def scrolled( amount:Int) = {
    try{ callbacks("scroll").foreach( _(amount,0,0,0) ) }
    catch { case e:Exception => println(e) }
    false
  }
}


class KeyboardNavInput( var nav:Nav ) extends InputAdapter {

	var alt = false
	var shift = false
	var ctrl = false

	override def keyDown(k:Int) = {
		var v = 1.f
		var w = 45.f.toRadians
		//if( shift ) v *= .1f
		if( alt ) v *= 10.f
		
		k match {
			case Keys.SHIFT_LEFT => nav.vel.y = -v
			case Keys.ALT_LEFT => alt = true
			case Keys.CONTROL_LEFT => ctrl = true
			case Keys.W => nav.vel.z = v
			case Keys.A => nav.vel.x = -v
			case Keys.S => nav.vel.z = -v
			case Keys.D => nav.vel.x = v
			case Keys.SPACE => nav.vel.y = v

			case Keys.UP => nav.angVel.x = w
			case Keys.LEFT => nav.angVel.y = w
			case Keys.RIGHT => nav.angVel.y = -w
			case Keys.DOWN => nav.angVel.x = -w
			case Keys.Q => nav.angVel.z = w
			case Keys.E => nav.angVel.z = -w

			//case Keys.NUM_1 => nav.moveToOrigin
			case _ => false
		}
		false
	}

	override def keyUp(k:Int) = {	
		k match {
			case Keys.ALT_LEFT => alt = false
			case Keys.CONTROL_LEFT => ctrl = false
			case Keys.W | Keys.S => nav.vel.z = 0
			case Keys.A | Keys.D => nav.vel.x = 0
			case Keys.SHIFT_LEFT | Keys.SPACE => nav.vel.y = 0
			case Keys.UP | Keys.DOWN => nav.angVel.x = 0
			case Keys.LEFT | Keys.RIGHT => nav.angVel.y = 0
			case Keys.Q | Keys.E => nav.angVel.z = 0
			case _ => false
		}
		false
	}
}


