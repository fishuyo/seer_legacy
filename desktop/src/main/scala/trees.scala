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

object MyInput extends InputAdapter {

  override def touchDown( screenX:Int, screenY:Int, pointer:Int, button:Int) = {
    Main.sounds(0).f(screenX*.1f + 40.f) //Scale.note(screenX/10))//*1.0f + 40.f)
    Main.sounds(1).f(screenY*.1f + 40.f) //Scale.note(screenY/10))//*1.0f + 40.f)
    Main.tree.branch( 5, screenX / 10.f, screenY * .5f / 800 + .5f, 0)
    true
  }

  override def touchDragged( screenX:Int, screenY:Int, pointer:Int) = {
    touchDown(screenX,screenY,pointer,0);
  }

  override def keyTyped( c: Char) = {
    c match {
      case 'r' => Main.live.reload; Shader.reload;
      case _ => false
    }
    true
  }
}

object Main extends App{

  SimpleAppRun.loadLibs()
  
  var trees = new TreeRoot(TreeNode( Vec3(-1,.3,0), .3f )) :: List() //:: TreeNode( Vec3(3.f,0,0), .3f ) :: TreeNode( Vec3( 6.f,0,0), .3f) :: TreeNode( Vec3(9.f,0,0), .1f) :: List()
  var fabrics = Fabric( Vec3(0), 1.f,1.f,.05f,"xz") ::List()//:: Fabric( Vec3(3.f,0,0),1.f,1.f,.05f,"xz") :: Fabric( Vec3(6.f,0,0),1.f,1.f,.05f,"xz") :: Fabric( Vec3(9.f,0,0),1.f,1.f,.05f,"xz") :: List()

  var sounds = new TriangleWave(440.f) :: new TriangleWave(40.f) :: List()


  trees(0).node.branch( 5, 45.f, .8f, 0)
  var tree = trees(0).node
  //trees(1).branch( 5, 10.f, .9f, 0)
  //trees(2).branch( 10, 20.f, .5f, 0)
  //trees(3).branch( 8, 20.f, .5f, 0)



  //GLScene.push( GLPrimitive.cube(Vec3(0), Vec3(.4f)) )
  trees.foreach( t => GLScene.push( t ) )
  //fabrics.foreach( f => GLScene.push( f ) )

  sounds.foreach( s => Audio.push( s ))

  //val f = Gdx.files.internal("res/wind.mp3")
  //val wind = Gdx.audio.newMusic(f)
  //wind.setVolume(.3f)
  //wind.setLooping(true)
  //wind.play

  Inputs.addProcessor(MyInput) 
  val live = new Ruby("src/main/scala/live/live.rb")
  SimpleAppRun()  

}



