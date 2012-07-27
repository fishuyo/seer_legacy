
package com.fishuyo
package io

import graphics._
import maths._
import ray._

import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener

object NavInput extends NavInput(Camera)

class NavInput( var cam:Camera=Camera ) extends KeyMouseListener {

  var moving = 0
  var looking = 0

	override def keyPressed(e: KeyEvent) = {
		val keyCode = e.getKeyCode();

		if (keyCode == KeyEvent.VK_ESCAPE) {
			//System.exit(0);
		}
		if (keyCode == KeyEvent.VK_W) {
			cam.forward
      moving += 1
		}
		if (keyCode == KeyEvent.VK_S) {
			cam.backward
      moving += 1
		}
		if (keyCode == KeyEvent.VK_A) {
			cam.left
      moving += 1
		}
		if (keyCode == KeyEvent.VK_D) {
		  cam.right
      moving += 1
    }
		if (keyCode == KeyEvent.VK_SPACE) {
			cam.up
      moving += 1
		}
		if (keyCode == KeyEvent.VK_SHIFT) {
      cam.down
      moving += 1
    }
		if (keyCode == KeyEvent.VK_Q) {
			System.exit(0)//this.rotation = !rotation;
		}
    if (keyCode == KeyEvent.VK_BACK_QUOTE) cam.initialPosition()

		if (keyCode == KeyEvent.VK_UP) { cam.lookUp; looking+=1 }
		if (keyCode == KeyEvent.VK_DOWN) { cam.lookDown; looking+=1 }
		if (keyCode == KeyEvent.VK_LEFT) { cam.lookLeft; looking+=1 }
		if (keyCode == KeyEvent.VK_RIGHT) { cam.lookRight; looking+=1 }

	}

	override def keyReleased(e: KeyEvent) = {
		val k = e.getKeyCode();

		if (k == KeyEvent.VK_W || k == KeyEvent.VK_S || k == KeyEvent.VK_A || k == KeyEvent.VK_D || k == KeyEvent.VK_SPACE || k == KeyEvent.VK_SHIFT) {
      cam.stop
		}
		if (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_UP || k == KeyEvent.VK_DOWN) {
      cam.stopLook
		}
	}

/*
	def mouseReleased(e: MouseEvent) = {
		int x = e.getX();
		int y = e.getY();
		this.mouseClickX = x;
		this.mouseClickY = y;

		this.dragging = false;
	}

	def mousePressed(e: MouseEvent) = {
		
    val x = e.getX();
		val y = e.getY();
    println( x + " " + y )

    val e = cam.position
    val v = cam.projectPoint( x, y )
    println( v )
   
    val r = new Ray( e, v-e )
    val xx = r(200.f).x + 1.f

		//lastx = x;
		//lasty = y;
	}

	def mouseClicked(e: MouseEvent) = {
	}

	def mouseEntered(e: MouseEvent) = {
	}

  def mouseExited(e: MouseEvent) = {
	}

	def mouseMoved(e: MouseEvent) = {
		int x = e.getX();
		int y = e.getY();
		int dx = Math.abs(x - this.mouseClickX);
		int dy = Math.abs(y - this.mouseClickY);

		if (dragging == false) {
		}

		// set to true, so that the camera movement doesn't trigger window events
		this.dragging = true;
		// Calculate mouse movements
		if (x < this.mouseClickX) {
			this.camera.turnLeft(dx);
		} else if (x > this.mouseClickX) {
			this.camera.turnRight(dx);
		}
		if (y < this.mouseClickY) {
			this.camera.turnUp(dy);
		} else if (y > this.mouseClickY) {
			this.camera.turnDown(dy);
		}
		this.mouseClickX = x;

		this.mouseClickY = y;
	
  }

	def mouseDragged(e: MouseEvent) = {}*/
}

trait KeyMouseListener extends KeyListener with MouseListener with MouseMotionListener {

  def mouseReleased( e: MouseEvent) = {}
  def mousePressed( e: MouseEvent) = {}
  def mouseClicked( e: MouseEvent) = {}
  def mouseEntered( e: MouseEvent) = {}
  def mouseExited( e: MouseEvent) = {}
  def mouseMoved( e: MouseEvent) = {}
  def mouseDragged( e: MouseEvent) = {}

  def keyPressed( e: KeyEvent ) = {}
  def keyReleased( e: KeyEvent ) = {}
  def keyTyped( e: KeyEvent ) = {}

}
