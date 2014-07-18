
package com.fishuyo.seer
package io

import graphics._
import spatial._
import spatial._

import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.input._

import rx._

class Input extends InputAdapter
object Inputs extends InputMultiplexer

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

