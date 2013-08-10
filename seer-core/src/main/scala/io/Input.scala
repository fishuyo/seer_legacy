
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
import com.badlogic.gdx.input._
import com.badlogic.gdx.math.Vector2

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
	type Callback = (Array[Int])=>Unit
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
    try { callbacks("up").foreach( _(Array(screenX,screenY,pointer,button)) ) }
    catch { case e:Exception => println(e) }
    false
  }
 	override def touchDown( screenX:Int, screenY:Int, pointer:Int, button:Int) = {
    try{ callbacks("down").foreach( _(Array(screenX,screenY,pointer,button)) ) }
    catch { case e:Exception => println(e) }
    false
	}
  override def touchDragged( screenX:Int, screenY:Int, pointer:Int) = {
    try{ callbacks("drag").foreach( _(Array(screenX,screenY,pointer)) ) }
    catch { case e:Exception => println(e) }
    false
  }

  // mouse only
  override def mouseMoved( screenX:Int, screenY:Int ) = {
    try { callbacks("move").foreach( _(Array(screenX,screenY)) ) }
    catch { case e:Exception => println(e) }
    false
  }
  override def scrolled( amount:Int) = {
    try{ callbacks("scroll").foreach( _(Array(amount)) ) }
    catch { case e:Exception => println(e) }
    false
  }
}

object Touch extends InputAdapter {
	type Callback = (Int,Array[Float])=>Unit
	var callbacks = Map[String,List[Callback]]()

	val down = ListBuffer.fill(20)(false)
  val pos = new Array[Vec3](20)

	callbacks += ("multi" -> List()) // num [pos]
	callbacks += ("tap" -> List())
	callbacks += ("long" -> List())
	callbacks += ("fling" -> List())
	callbacks += ("pan" -> List())
	callbacks += ("zoom" -> List())
	callbacks += ("pinch" -> List())

	class Gesture extends GestureDetector.GestureAdapter {
		// override def touchDown (x:Float, y:Float, pointer:Int, button:Int) = {
		// 	try { callbacks("up").foreach( _(screenX,screenY,pointer,button) ) }
		// 	catch { case e:Exception => println(e) }
	 //    false;
	 //  }
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
	val gesture = new GestureDetector(new Gesture())

	def non()() = {}

	def clear() = { callbacks.keys.foreach(callbacks(_) = List()); Inputs.removeProcessor(this); Inputs.removeProcessor(gesture) }
	def use() = { Inputs.addProcessor(this); Inputs.addProcessor(gesture) }

	def bind( s:String, f:Callback ) = callbacks(s) = f :: callbacks.getOrElseUpdate(s,List())	

  override def touchUp( screenX:Int, screenY:Int, pointer:Int, button:Int) = {
  	down(pointer) = false
    false
  }
 	override def touchDown( screenX:Int, screenY:Int, pointer:Int, button:Int) = {
  	down(pointer) = true
  	pos(pointer) = Vec3(screenX,screenY,0.f)

    val indices = down.zipWithIndex.collect{ case (true,i) => i }
    val p = indices.map( pos(_) )
    //val centroid = p.sum / p.length

    val coords = p.flatMap( (v:Vec3) => List(v.x,v.y) )

    try { callbacks("multi").foreach( _(p.length, coords.toArray) ) }
		catch { case e:Exception => println(e) }
    false
	}
  override def touchDragged( screenX:Int, screenY:Int, pointer:Int) = {
  	touchDown(screenX,screenY,pointer,0);
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


