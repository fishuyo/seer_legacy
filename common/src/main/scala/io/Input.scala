
package com.fishuyo
package io

import graphics._
import maths._
import spatial._
//import ray._

import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Input.Keys

object Inputs extends InputMultiplexer

//object NavInput extends NavInput(Camera)

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

			case Keys.NUM_1 => nav.moveToOrigin
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


