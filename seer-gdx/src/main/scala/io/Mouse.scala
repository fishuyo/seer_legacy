
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
import com.badlogic.gdx.math.Vector2

import rx._

object Mouse extends InputAdapter {
	type Callback = (Array[Int])=>Unit
	var callbacks = Map[String,List[Callback]]()
	callbacks += ("up" -> List())
	callbacks += ("down" -> List())
	callbacks += ("drag" -> List())
	callbacks += ("move" -> List())
	callbacks += ("scroll" -> List())

	val x = Var(0f)
	val y = Var(0f)
	val xy = Var(Vec2())

	val id = Var(0)
	val button = Var(0)
	val scroll = Var(0f)
	val status = Var("up")

	def non()() = {}

	def clear() = { callbacks.keys.foreach(callbacks(_) = List()); Inputs.removeProcessor(this) }
	def use() = Inputs.addProcessor(this)

	def bind( s:String, f:Callback ) = callbacks(s) = f :: callbacks.getOrElseUpdate(s,List())	

	def update(sx:Int, sy:Int, stat:String){
		x() = sx * 1f / Window.width
		y() = 1f - (sy * 1f / Window.height)
		xy() = Vec2(x(),y())
		status() = stat
	}
  override def touchUp( sx:Int, sy:Int, pointer:Int, but:Int) = {
		update(sx,sy,"up")
  	id() = pointer
  	button() = but
    try { callbacks("up").foreach( _(Array(sx,sy,pointer,but)) ) }
    catch { case e:Exception => println(e) }
    false
  }
 	override def touchDown( sx:Int, sy:Int, pointer:Int, but:Int) = {
		update(sx,sy,"down")
  	id() = pointer
  	button() = but
    try{ callbacks("down").foreach( _(Array(sx,sy,pointer,but)) ) }
    catch { case e:Exception => println(e) }
    false
	}
  override def touchDragged( sx:Int, sy:Int, pointer:Int) = {
		update(sx,sy,"drag")  	
  	id() = pointer
    try{ callbacks("drag").foreach( _(Array(sx,sy,pointer)) ) }
    catch { case e:Exception => println(e) }
    false
  }

  // mouse only
  override def mouseMoved( sx:Int, sy:Int ) = {
		update(sx,sy,"move")  	
    try { callbacks("move").foreach( _(Array(sx,sy)) ) }
    catch { case e:Exception => println(e) }
    false
  }
  override def scrolled( amount:Int) = {
  	scroll() = amount.toFloat
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
		// 	try { callbacks("up").foreach( _(sx,sy,pointer,button) ) }
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

  override def touchUp( sx:Int, sy:Int, pointer:Int, button:Int) = {
  	down(pointer) = false
    false
  }
 	override def touchDown( sx:Int, sy:Int, pointer:Int, button:Int) = {
  	down(pointer) = true
  	pos(pointer) = Vec3(sx,sy,0f)

    val indices = down.zipWithIndex.collect{ case (true,i) => i }
    val p = indices.map( pos(_) )
    //val centroid = p.sum / p.length

    val coords = p.flatMap( (v:Vec3) => List(v.x,v.y) )

    try { callbacks("multi").foreach( _(p.length, coords.toArray) ) }
		catch { case e:Exception => println(e) }
    false
	}
  override def touchDragged( sx:Int, sy:Int, pointer:Int) = {
  	touchDown(sx,sy,pointer,0);
    false
  }
}


