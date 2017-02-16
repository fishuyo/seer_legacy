
package com.fishuyo.seer
package examples.graphics

import graphics._
import spatial._
import util.Random

import com.badlogic.gdx.graphics.profiling.GLProfiler

object ProfileExample extends SeerApp {

  var t = 0f 

  // make a new mesh and set render primitive to use GL_TRIANGLE
  val mesh = Mesh()
  mesh.primitive = Triangles

  // make a model, which is like a renderable instance of the mesh
  val model = Model(mesh)
  model.material = Material.specular

  // reset the mesh, generate new random vertices, and push to gpu
  def generateMesh(m:Mesh){
    m.clear
    for( i <- 0 until 102) m.vertices += Random.vec3() // 34 random triangles
    m.recalculateNormals()
    m.update()  // update mesh on gpu, must be called from render thread (init, draw, animate functions)
  }

  // init function called once from the render thread on startup
  // we put the call to generateMesh in here since it needs to be run in the 
  // render thread where the underlying GL context is valid
  override def init(){
    GLProfiler.enable()
    generateMesh(mesh)
  }

  // draw model
  override def draw(){
    model.draw
    if(Random.float() < 0.5){
      MatrixStack.push()
      MatrixStack.translate(1,0,0)
      model.draw
      MatrixStack.pop()
    }
    println("calls: " + GLProfiler.calls )
    println("draw calls: " + GLProfiler.drawCalls )
    println("shader switches: " + GLProfiler.shaderSwitches )
    GLProfiler.reset()
  }

  // animate the mesh
  override def animate(dt:Float){
    t += dt

    // random walk the vertices in the mesh and update on the gpu
    mesh.vertices.foreach( _ += Random.vec3()*0.01f )
    mesh.recalculateNormals()
    mesh.update()

    // generate new random mesh ever 2.5 seconds
    if( t > 2.5f){
      generateMesh(mesh)
      t = 0f
    }
  }

}