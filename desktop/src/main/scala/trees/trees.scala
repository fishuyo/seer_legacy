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
  
  var ground:GLPrimitive = _
  val t = new Tree()
  t.root.pose.pos.set(1.f,0.f,.5f)
  t.setDepth(5)
  t.branch()
  var trees = new Tree() :: List() //:: TreeNode( Vec3(3.f,0,0), .3f ) :: TreeNode( Vec3( 6.f,0,0), .3f) :: TreeNode( Vec3(9.f,0,0), .1f) :: List()
  //var fabrics = Fabric( Vec3(0,-.5f,0), 1.f,1.f,.05f,"xz") ::List()//:: Fabric( Vec3(3.f,0,0),1.f,1.f,.05f,"xz") :: Fabric( Vec3(6.f,0,0),1.f,1.f,.05f,"xz") :: Fabric( Vec3(9.f,0,0),1.f,1.f,.05f,"xz") :: List()

  // var osc = new Tri(41.f)
  // var lfo = new Saw(1.f)
  // var sounds = osc * ((1.f-lfo)*.5f) :: List()

  trees(0).branch()
  var tree = trees(0)

  trees.foreach( t => GLScene.push( t ) )
  //fabrics.foreach( f => GLScene.push( f ) )

  //val cube = GLPrimitive.cube()
  //cube.pose.pos.set(1,0,0)
  //GLScene.push( cube )

  //sounds.foreach( s => Audio.push( s ))

  //val f = Gdx.files.internal("res/wind.mp3")
  //val wind = Gdx.audio.newMusic(f)
  //wind.setVolume(.3f)
  //wind.setLooping(true)
  //wind.play

  // Inputs.addProcessor(MyInput)

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
  }
  override def draw(){
    ground.draw() //render(Shader())
  }
  override def step(dt:Float){
    live.step(dt)
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



