package com.fishuyo
package trees

import maths._
import graphics._
import audio._
import io._
import dynamic._

import android.os.Bundle

import com.badlogic.gdx.Gdx
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
    config.useGL20 = true
    TreeScene.init
    initialize(new SimpleAppListener, config)
  }
}


object TreeScene {
  var trees:List[Tree] = null
  var fabrics:List[Fabric] = null
  var sounds:List[TriangleWave] = null
  var tree:Tree = null

  def init(){
    trees = new Tree() :: List() //:: TreeNode( Vec3(3.f,0,0), .3f ) :: TreeNode( Vec3( 6.f,0,0), .3f) :: TreeNode( Vec3(9.f,0,0), .1f) :: List()
    fabrics = Fabric( Vec3(0), .9f,.9f,.1f,"xz") ::List()//:: Fabric( Vec3(3.f,0,0),1.f,1.f,.05f,"xz") :: Fabric( Vec3(6.f,0,0),1.f,1.f,.05f,"xz") :: Fabric( Vec3(9.f,0,0),1.f,1.f,.05f,"xz") :: List()
    sounds = new TriangleWave(440.f) :: new TriangleWave(40.f) :: List()


    trees(0).branch()
    tree = trees(0)
    tree.bDepth = 6
    tree.branchNum.setChoices(Array(0,1,2))
    tree.branchNum.setProb(Array(0.f,0.f,1.f))
    tree.sLength.setMinMax(0.5f,0.5f,false)

    tree.bAngle.z.setMinMax(0.1f,0.1f,false)
    tree.bAngle.y.setMinMax(-1.0f,1.0f,false)
    tree.bAngle.x.setMinMax(0.0f,0.0f,false)

    tree.sAngle.z.setMinMax(-2.0f,2.0f,false)
    tree.sAngle.y.setMinMax(-0.2f,0.2f,false)
    tree.sAngle.x.setMinMax(-0.2f,0.2f,false)

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
    //val live = new Ruby(Gdx.files.internal("res/live.rb").path())
    //OSC.listen()
    //SimpleAppRun()
  }  

}

object MyInput extends InputAdapter {

  override def touchDown( screenX:Int, screenY:Int, pointer:Int, button:Int) = {
    TreeScene.sounds(0).f(screenX*.1f + 40.f) //Scale.note(screenX/10))//*1.0f + 40.f)
    TreeScene.sounds(1).f(screenY*.1f + 40.f) //Scale.note(screenY/10))//*1.0f + 40.f)
    TreeScene.tree.bAngle.y.setMinMax( .1f, (screenX/10.f).toRadians  )
    TreeScene.tree.sRatio.set( screenY * .5f / 800 + .5f )
    TreeScene.tree.bRatio.set( screenY * .5f / 800 + .3f )
    TreeScene.tree.branch() //, screenX / 10.f, screenY * .5f / 800 + .5f, 0)
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
