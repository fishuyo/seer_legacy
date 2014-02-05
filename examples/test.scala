package com.fishuyo.seer
package test

import graphics._
import io._
import maths._
import particle._
import dynamic._
import audio._

import scala.collection.mutable.ListBuffer

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils._

object Main extends App with Animatable{

  SimpleAppRun.loadLibs()

  Scene.push(this)


  val cube = Cube() //new SpringMesh(Cube.generateMesh(), 1.f)
  val cubes = ListBuffer[Model]()
  val n = 15
  for( i<-(-n until n); j<-(-n until n)){
    val x = i * .2f
    val z = j * .2f
    val c = Cube().scale(.1f).translate(x,0,z)
    cubes += c
  }

  val node = new RenderNode
  val s = new SpringMesh( Sphere.generateMesh(prim=Triangles), 1.f) //Sphere()
  // s.color.set(1,0,0,1)
  // node.scene.push(s)
  // SceneGraph.addNode(node)
  Scene.push(s)

  val mesh = Cylinder.mesh.getOrElse(new Mesh()) //Sphere.generateMesh()

  // var modelBuilder = Some(parsers.EisenScriptParser(""))
  // var model = Some(Model())

  // Scene.push(cube)

  val live = new Ruby("test.rb")

  SimpleAppRun()  

  override def init(){
  }
  override def draw(){

    live.draw()
    // cubes.foreach( _.draw() )

  }

  override def animate(dt:Float){

    live.animate(dt)
  }

}



