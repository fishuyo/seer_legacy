package com.fishuyo.seer
package examples.silo

import maths._
import spatial._
import graphics._
import trees._
import util._

import io._
import dynamic._
import audio._

import com.badlogic.gdx.Gdx

import collection.mutable.ListBuffer


object Main extends App with Animatable with AudioSource {

  SimpleAppRun.loadLibs()
  Scene.push(this)
  Audio.push(this)
  
  var mesh = Sphere.generateMesh(1.f,60)
  var ground = Model(mesh)
  var tree = new Tree() 
	tree.branch()

	var rms = 0.f
  var move = Vec3(0.f)
  var rot = Vec3(0.f)
  var nmove = Vec3(0.f)
  var nrot = Vec3(0.f)

  val live = new Ruby("silo.rb")

  SimpleAppRun() 

  override def init(){
    tree.init()
    mesh.vertices.foreach( (v) => v += Random.vec3()*.01f )
    mesh.update()
  }
  override def draw(){
    ground.draw()
    tree.draw()
  }
  
  override def animate(dt:Float){

    Shader.bg.set(0,0,0,0)
    Shader.lightingMix = 0.0
    live.animate(dt)
    // Kinect.animate(dt)
    tree.animate(dt)
  }

  override def audioIO( in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){
    var sum = 0.f
    for( i<-(0 until numSamples)){ //Audio.bufferSize)){
      sum += in(i)*in(i) //Audio.in(i)*Audio.in(i)
      rms = rms * .9999f + .0001f* math.sqrt( sum / numSamples ).toFloat
    }
    //println( rms )
  }

}



