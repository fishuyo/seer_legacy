
package com.fishuyo
package examples.gdxtest
import maths._
import graphics._
import trees._

import java.awt.event._

import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer
import com.badlogic.gdx.graphics.GL10

object Main extends App{
  

  //build scene by pushing objects to singleton GLScene (GLRenderWindow renders it by default)
  GLScene.push( new Suck)

  SimpleAppRun()
  //val window = new graphics.GLRenderWindow
  //window.addKeyMouseListener( Input )

}

class Suck extends GLAnimatable {

  var pos = Vec3(0,0,-2)
  var npos = Vec3(4,4,-2)
  var accum = 0.f

  override def draw() = {
    gli.begin( GL10.GL_LINES )
    gli.vertex(pos.x, pos.y, pos.z); gli.vertex( npos.x, npos.y, npos.z )
    gli.end

  }
  override def step(dt:Float) = {
    accum += dt
    npos = Vec3( 4.f*math.cos(accum), 4.f*math.sin(accum), -2.f)
  }

}

object Input extends io.KeyMouseListener {

  var g = true

  override def keyPressed( e: KeyEvent ){
    val k = e.getKeyCode
    k match {
      case KeyEvent.VK_G => 
        g = !g
        if(g) Fabric.g = -10.f else Fabric.g = 0.f
        println( "Gravity: " + Fabric.g )

      case _ => null
    }

  }


}

