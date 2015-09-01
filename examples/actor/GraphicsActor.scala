
package com.fishuyo.seer
package examples.actor

import graphics._
import actor._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20


object GraphicsActorTest extends SeerApp {


  val model = Cube()
  model.material = Material.specular
  model.material.color = RGB.red

  Renderer().clear = false

  override def draw(){
    GraphicsActor.actor ! (() => { 
      // println("hi")
      Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
      Renderer().shader.begin() 

      // MatrixStack.clear()
      // Renderer().setMatrixUniforms()
      // Renderer().setEnvironmentUniforms()
      // Renderer().environment.setGLState()
      // Renderer().setMaterialUniforms(material)
      
      // Renderer().shader.setUniforms() // set buffered uniforms in shader program

      model.draw()
      model.rotate(0,0.01f,0)
      
      Renderer().shader.end()

    })
  }


}