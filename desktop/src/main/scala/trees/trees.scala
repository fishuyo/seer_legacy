package com.fishuyo
package examples.trees
import maths._
import graphics._
import trees._
import io._
import dynamic._
import audio._

import com.badlogic.gdx.Gdx

object Main extends App with GLAnimatable {

  SimpleAppRun.loadLibs()
  GLScene.push(this)
  
  var trees = new Tree() :: List() //:: TreeNode( Vec3(3.f,0,0), .3f ) :: TreeNode( Vec3( 6.f,0,0), .3f) :: TreeNode( Vec3(9.f,0,0), .1f) :: List()
  //var fabrics = Fabric( Vec3(0,-.5f,0), 1.f,1.f,.05f,"xz") ::List()//:: Fabric( Vec3(3.f,0,0),1.f,1.f,.05f,"xz") :: Fabric( Vec3(6.f,0,0),1.f,1.f,.05f,"xz") :: Fabric( Vec3(9.f,0,0),1.f,1.f,.05f,"xz") :: List()

  // var osc = new Tri(41.f)
  // var lfo = new Saw(1.f)
  // var sounds = osc * ((1.f-lfo)*.5f) :: List()

  trees(0).branch()
  var tree = trees(0)

  trees.foreach( t => GLScene.push( t ) )
  //fabrics.foreach( f => GLScene.push( f ) )

  val cube = GLPrimitive.cube()
  cube.pose.pos.set(1,0,0)
  GLScene.push( cube )

  //sounds.foreach( s => Audio.push( s ))

  //val f = Gdx.files.internal("res/wind.mp3")
  //val wind = Gdx.audio.newMusic(f)
  //wind.setVolume(.3f)
  //wind.setLooping(true)
  //wind.play

  // Inputs.addProcessor(MyInput) 

  val live = new Ruby("src/main/scala/trees/trees.rb")

  SimpleAppRun() 

  override def step(dt:Float){
    live.step(dt)
  } 
}



