
package seer
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


case class PointerEvent(event:String, x:Float, y:Float, dx:Float, dy:Float, px:Int, py:Int)

object Mouse extends Mouse {
  def apply() = new Mouse
}

class Mouse extends InputAdapter {
  val pfuncs = ListBuffer[(PointerEvent)=>Unit]()

  use()

	val xy = Var(Vec2())
  val x = Var(0f) //xy.map(_.x)
  val y = Var(0f) //xy.map(_.y)

  val vel = Var(Vec2())
  val dx = Var(0f) //vel.map(_.x)
  val dy = Var(0f) //vel.map(_.y)

	val id = Var(0)
	val button = Var(0)
	val scroll = Var(0f)
	val status = Var("up")

  def use() = {
    val ps = Inputs.getProcessors()
    if(!ps.contains(this, true)) Inputs.addProcessor(this)
  }
	def remove() = Inputs.removeProcessor(this)

	// def bind( s:String, f:Callback ) = callbacks(s) = f :: callbacks.getOrElseUpdate(s,List())	

  def listen(f:(PointerEvent)=>Unit) = pfuncs += f

	def update(sx:Int, sy:Int, stat:String){ 
    status() = stat
    val lx = x.now
    val ly = y.now
		xy() = Vec2(sx * 1f / Window.width, 1f - (sy * 1f / Window.height))
    x() = xy.now.x
    y() = xy.now.y
    vel() = Vec2(x.now - lx, y.now - ly)
    dx() = vel.now.x
    dy() = vel.now.y
    val e = PointerEvent(stat, x.now, y.now, dx.now, dy.now, sx, sy)
    pfuncs.foreach( _(e) )
  }
  
  override def touchUp(sx:Int, sy:Int, pointer:Int, but:Int) = {
		update(sx,sy,"up")
  	id() = pointer
  	button() = but
    false
  }
 	override def touchDown( sx:Int, sy:Int, pointer:Int, but:Int) = {
		update(sx,sy,"down")
  	id() = pointer
  	button() = but
    false
	}
  override def touchDragged( sx:Int, sy:Int, pointer:Int) = {
		update(sx,sy,"drag")  	
  	id() = pointer
    false
  }

  // mouse only
  override def mouseMoved( sx:Int, sy:Int ) = {
		update(sx,sy,"move")  	
    false
  }
  override def scrolled( amount:Int) = {
  	scroll() = amount.toFloat
    false
  }
}

