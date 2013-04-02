package com.fishuyo
package examples.trees
import maths._
import graphics._
import trees._
import io._
import dynamic._
import audio._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.glutils._
//import com.badlogic.gdx.graphics.GL10

import monido._

object MyInput extends InputAdapter {

  override def touchDown( screenX:Int, screenY:Int, pointer:Int, button:Int) = {
    Main.osc.f(screenX*1.f + 80.f) //Scale.note(screenX/10))//*1.0f + 40.f)
    Main.lfo.f(screenY*.01f + 0.f) //Scale.note(screenY/10))//*1.0f + 40.f)
    Main.tree.bAngle.y.setMinMax( .1f, (screenX/10.f).toRadians  )
    Main.tree.sRatio.set( screenY * .5f / 800 + .5f )
    Main.tree.bRatio.set( screenY * .5f / 800 + .3f )
    Main.tree.branch() //, screenX / 10.f, screenY * .5f / 800 + .5f, 0)
    true
  }

  override def touchDragged( screenX:Int, screenY:Int, pointer:Int) = {
    touchDown(screenX,screenY,pointer,0);
  }

  // override def keyTyped( c: Char) = {
  //   c match {
  //     case 'r' => 
  //       Main.live.reload;
  //       Shader.reload;
  //     case _ => false
  //   }
  //   true
  // }
}

object Main extends App{

  SimpleAppRun.loadLibs()
  
  var trees = new Tree() :: List() //:: TreeNode( Vec3(3.f,0,0), .3f ) :: TreeNode( Vec3( 6.f,0,0), .3f) :: TreeNode( Vec3(9.f,0,0), .1f) :: List()
  var fabrics = Fabric( Vec3(0,-.5f,0), 1.f,1.f,.05f,"xz") ::List()//:: Fabric( Vec3(3.f,0,0),1.f,1.f,.05f,"xz") :: Fabric( Vec3(6.f,0,0),1.f,1.f,.05f,"xz") :: Fabric( Vec3(9.f,0,0),1.f,1.f,.05f,"xz") :: List()

  var osc = new Tri(41.f)
  var lfo = new Saw(1.f)
  var sounds = osc * ((1.f-lfo)*.5f) :: List()


  trees(0).branch()
  var tree = trees(0)
  //trees(1).branch( 5, 10.f, .9f, 0)
  //trees(2).branch( 10, 20.f, .5f, 0)
  //trees(3).branch( 8, 20.f, .5f, 0)



  //GLScene.push( GLPrimitive.cube(Vec3(0), Vec3(.4f)) )
  trees.foreach( t => GLScene.push( t ) )
  fabrics.foreach( f => GLScene.push( f ) )

  sounds.foreach( s => Audio.push( s ))

  //val f = Gdx.files.internal("res/wind.mp3")
  //val wind = Gdx.audio.newMusic(f)
  //wind.setVolume(.3f)
  //wind.setLooping(true)
  //wind.play

  Inputs.addProcessor(MyInput) 
  val live = new Ruby("src/main/scala/trees/trees.rb")
  OSC.listen()

  val monitor = FileMonido("src/main/scala/trees/trees.rb"){
    case ModifiedOrCreated(f) => Main.live.reload;
    case _ => None
  }
  FileMonido("res/shaders/firstPass.vert"){
    case ModifiedOrCreated(f) => Shader.reload;
    case _ => None
  }
  FileMonido("res/shaders/firstPass.frag"){
    case ModifiedOrCreated(f) => Shader.reload;
    case _ => None
  }
  SimpleAppRun()  

}



