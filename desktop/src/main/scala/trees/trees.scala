package com.fishuyo
package examples.trees
import maths._
import spatial._
import graphics._
import trees._
import io._
import dynamic._
import audio._

import com.badlogic.gdx.Gdx


object Main extends App with GLAnimatable with AudioSource {

  SimpleAppRun.loadLibs()
  GLScene.push(this)
  //GLScene.push(Kinect)
  
  var ground:GLPrimitive = _
  var tree = new Tree() 
 tree.branch()

  var wind:com.badlogic.gdx.audio.Music = _
  Audio.push(this) 

  val niceView = new Pose(Vec3(0.7285795f,0.f, 1.4537674f), Quat(0.9765499f, 0.113466285f, 0.18053834f, -0.029700035f))

  var rms = 0.f

  val live = new Ruby("src/main/scala/trees/trees.rb")

  SimpleAppRun() 

  override def init(){
    ground = GLPrimitive.fromObj("src/main/scala/drone/landscapealien.obj") //new ObjLoader().loadObj(Gdx.files.internal("src/main/scala/drone/landscapealien.obj"))
    ground.pose.quat.set(0.42112392f,-0.09659095f, 0.18010217f, -0.8836787f)
    ground.pose.pos.set(0.f,-1.3f,-.0f)
    ground.scale.set(10.f,10.f,10.f)

    val f = Gdx.files.internal("res/wind.mp3")
    wind = Gdx.audio.newMusic(f)
    wind.setVolume(0.f)
    wind.setLooping(true)
    wind.play

    Kinect.init()
    tree.init()
  }
  override def draw(){
    
    ground.draw()
    Kinect.draw()
    tree.draw()
  }
  override def step(dt:Float){
    live.step(dt)
    Kinect.step(dt)
    tree.step(dt)
  }

  override def audioIO( in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){
    var sum = 0.f
    for( i<-(0 until numSamples)){ //Audio.bufferSize)){
      sum += in(i)*in(i) //Audio.in(i)*Audio.in(i)
      rms = math.sqrt( sum / numSamples ).toFloat
    }
    //println( rms )
  }
}



