package com.fishuyo.seer
package test

import graphics._
import io._
import maths._
import particle._
import dynamic._
import audio._
import util._

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
  val s = new SpringMesh( Plane.generateMesh(2,2,10,10), 1.f) //Sphere()
  s.particles.takeRight(10).foreach( (p) => s.pins += AbsoluteConstraint(p, p.position))
  val model = Model(s)
  // s.color.set(1,0,0,1)
  // node.scene.push(s)
  // SceneGraph.addNode(node)
  // Scene.push(model)

  val mesh = Cylinder.mesh.getOrElse(new Mesh()) //Sphere.generateMesh()

  // var modelBuilder = Some(parsers.EisenScriptParser(""))
  // var model = Some(Model())

  // Scene.push(cube)

  val live = new Ruby("test.rb")

  val words = List("soup","salad","is","super","\n","\n","\n","azure","why","eyes","dark","\n", "\n","\n","\n","sexy","time","space","can be", "will", "to be about to", "has", "futile", "dog", "witch")
  var text = ".."
  var x = 10.f
  var y = 100.f
  var t = 0.f 
  var next = 1.f
  def movet(a:Float,b:Float){ x=a; y=b}

  SimpleAppRun()  

  override def init(){
    Text.loadFont()

  }
  override def draw(){

    Text.render(text,x,y)

    live.draw()
    // cubes.foreach( _.draw() )

  }

  override def animate(dt:Float){

    if( t > next){
      text += " " + Random.oneOf(words: _*)()
      t=0.f 
      next = Random.float()*5.f
    }
    t += dt

    s.animate(dt)
    live.animate(dt)
  }

}



