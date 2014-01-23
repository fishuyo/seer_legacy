package com.fishuyo.seer
package test

import graphics._
import io._
import maths._
import dynamic._
import audio._

import scala.collection.mutable.ListBuffer

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils._

object Main extends App with Animatable{

  SimpleAppRun.loadLibs()

  Scene.push(this)

  val live = new Ruby("test.rb")

  val cube = Cube()
  val wire = Cube(Lines)

  cube.addChild(wire)

  val node = new RenderNode
  val s = Sphere()
  s.color.set(1,0,0,1)
  node.scene.push(s)
  SceneGraph.addNode(node)

  val mesh = Cylinder.mesh.getOrElse(new Mesh()) //Sphere.generateMesh()

  var modelBuilder = Some(parsers.EisenScriptParser(""))
  var model = Some(Model())

  //cube.scale.set(1.f, (2*480.f)/640.f, 1.f)
  Scene.push(cube)
  // Scene.push(wire)


  SimpleAppRun()  

  override def init(){
    cube.translate(-1,0,0)
    wire.translate(2,0,0)
  }
  override def draw(){

    live.draw()

  }

  override def animate(dt:Float){
    cube.rotate(0,0.1f,0)
    wire.rotate(0,-0.1f,0)

    live.animate(dt)

  }

}



