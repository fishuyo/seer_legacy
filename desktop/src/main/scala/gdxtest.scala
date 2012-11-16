
package com.fishuyo
package examples.gdxtest
import maths._
import graphics._
import trees._

import java.awt.event._

import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer
import com.badlogic.gdx.graphics.GL10
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.glutils.VertexBufferObject

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
  
  var vertices = new Array[Float](6)
  var vbo:VertexBufferObject = null

  override def draw() = {
    if( vbo == null ) vbo = new VertexBufferObject( false, 2, VertexAttribute.Position )
    
    vertices(0) = pos.x; vertices(1) = pos.y; vertices(2) = pos.z
    vertices(3) = npos.x; vertices(4) = npos.y; vertices(5) = npos.z

    gl11.glColor4f(1.f,1.f,1.f,1.f)
    gl.glLineWidth( 1.f )
    vbo.setVertices( vertices, 0, vertices.length )
    vbo.bind
    gl11.glDrawArrays( GL10.GL_LINES, 0, vertices.length)
    //gli.begin( GL10.GL_LINES )
    //gli.vertex(pos.x, pos.y, pos.z); gli.vertex( npos.x, npos.y, npos.z )
    //gli.end

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

