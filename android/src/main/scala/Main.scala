package com.fishuyo
package trees

import maths._
import graphics._
import audio._
import io._

import android.os.Bundle

import com.badlogic.gdx.backends.android._
import com.badlogic.gdx.InputAdapter

class Main extends AndroidApplication {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val config = new AndroidApplicationConfiguration()
    config.useAccelerometer = true
    config.useCompass = false
    config.useWakelock = true
    config.depth = 0
    config.useGL20 = false
    TreeScene.init
    initialize(new SimpleAppListener, config)
  }
}


object TreeScene {
  var trees:List[TreeRoot] = null
  var fabrics:List[Fabric] = null
  var sounds:List[TriangleWave] = null

  def init(){
    trees = new TreeRoot(TreeNode( Vec3(0), .1f )) :: List() //:: TreeNode( Vec3(3.f,0,0), .3f ) :: TreeNode( Vec3( 6.f,0,0), .3f) :: TreeNode( Vec3(9.f,0,0), .1f) :: List()
    fabrics = Fabric( Vec3(0), .9f,.9f,.1f,"xz") ::List()//:: Fabric( Vec3(3.f,0,0),1.f,1.f,.05f,"xz") :: Fabric( Vec3(6.f,0,0),1.f,1.f,.05f,"xz") :: Fabric( Vec3(9.f,0,0),1.f,1.f,.05f,"xz") :: List()
    sounds = new TriangleWave(440.f) :: new TriangleWave(40.f) :: List()


    trees(0).node.branch( 4, 45.f, .8f, 0)
    //trees(1).branch( 5, 10.f, .9f, 0)
    //trees(2).branch( 10, 20.f, .5f, 0)
    //trees(3).branch( 8, 20.f, .5f, 0)

    //build scene by pushing objects to singleton GLScene (GLRenderWindow renders it by default)
    trees.foreach( t => GLScene.push( t ) )
    fabrics.foreach( f => GLScene.push( f ) )

    sounds.foreach( s => Audio.push( s ))

    //val f = Gdx.files.internal("res/wind.mp3")
    //val wind = Gdx.audio.newMusic(f)
    //wind.setVolume(.3f)
    //wind.setLooping(true)
    //wind.play

    Inputs.addProcessor(MyInput) 
    //val live = new Ruby("src/main/scala/live/live.rb")
    //SimpleAppRun()
  }  

}

object MyInput extends InputAdapter {

  override def touchDown( screenX:Int, screenY:Int, pointer:Int, button:Int) = {
    TreeScene.sounds(0).f(screenX*1.f + 40.f) //Scale.note(screenX/10))//*1.0f + 40.f)
    TreeScene.sounds(1).f(screenY*1.f + 40.f) //Scale.note(screenY/10))//*1.0f + 40.f)
    TreeScene.trees(0).node.branch( 5, screenX / 10.f, screenY * .5f / 800 + .5f, 0)
    true
  }

  override def touchDragged( screenX:Int, screenY:Int, pointer:Int) = {
    touchDown(screenX,screenY,pointer,0);
  }

  override def keyTyped( c: Char) = {
    c match {
      //case 'r' => Main.live.reload
      case _ => false
    }
    true
  }
}
