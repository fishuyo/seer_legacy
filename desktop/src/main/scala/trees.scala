package com.fishuyo
package examples.trees
import maths._
import graphics._
import trees._
import io._
import dynamic._

import com.badlogic.gdx._
import com.badlogic.gdx.graphics.GL10

object MyInput extends InputAdapter {

  override def touchDown( screenX:Int, screenY:Int, pointer:Int, button:Int) = {
    false
  }

  override def touchDragged( screenX:Int, screenY:Int, pointer:Int) = {
    //touchDown(screenX,screenY,pointer,0);
    false
  }

  override def keyTyped( c: Char) = {
    c match {
      case 'r' => Main.live.reload
      case _ => false
    }
    true
  }
}

object Main extends App{

  //SimpleAppRun.loadLibs()
  
  var trees = new TreeRoot(TreeNode( Vec3(-1,.3,0), .3f )) :: List() //:: TreeNode( Vec3(3.f,0,0), .3f ) :: TreeNode( Vec3( 6.f,0,0), .3f) :: TreeNode( Vec3(9.f,0,0), .1f) :: List()
  var fabrics = Fabric( Vec3(0), 1.f,1.f,.05f,"xz") ::List()//:: Fabric( Vec3(3.f,0,0),1.f,1.f,.05f,"xz") :: Fabric( Vec3(6.f,0,0),1.f,1.f,.05f,"xz") :: Fabric( Vec3(9.f,0,0),1.f,1.f,.05f,"xz") :: List()

  //fabrics(0).stiff = 100.0f

  trees(0).node.branch( 5, 45.f, .8f, 0)
  var tree = trees(0).node
  //trees(1).branch( 5, 10.f, .9f, 0)
  //trees(2).branch( 10, 20.f, .5f, 0)
  //trees(3).branch( 8, 20.f, .5f, 0)

  //build scene by pushing objects to singleton GLScene (GLRenderWindow renders it by default)
  trees.foreach( t => GLScene.push( t ) )
  //fabrics.foreach( f => GLScene.push( f ) )

  Inputs.addProcessor(MyInput) 
  val live = new Ruby("src/main/scala/live/live.rb")
  SimpleAppRun()  

}



