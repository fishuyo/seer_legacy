package com.fishuyo
package trees

import maths._
import graphics._

import android.os.Bundle

import com.badlogic.gdx.backends.android._

class Main extends AndroidApplication {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val config = new AndroidApplicationConfiguration()
    config.useAccelerometer = true
    config.useCompass = false
    config.useWakelock = true
    config.depth = 0
    config.useGL20 = false
    val t = new TreeSceneInit
    initialize(new SimpleAppListener, config)
  }
}

class TreeSceneInit {

  var trees = TreeNode( Vec3(0), .1f ) :: List() //:: TreeNode( Vec3(3.f,0,0), .3f ) :: TreeNode( Vec3( 6.f,0,0), .3f) :: TreeNode( Vec3(9.f,0,0), .1f) :: List()
  var fabrics = Fabric( Vec3(0), 1.f,1.f,.08f,"xz") ::List()//:: Fabric( Vec3(3.f,0,0),1.f,1.f,.05f,"xz") :: Fabric( Vec3(6.f,0,0),1.f,1.f,.05f,"xz") :: Fabric( Vec3(9.f,0,0),1.f,1.f,.05f,"xz") :: List()

  trees(0).branch( 6, 45.f, .8f, 0)
  //trees(1).branch( 5, 10.f, .9f, 0)
  //trees(2).branch( 10, 20.f, .5f, 0)
  //trees(3).branch( 8, 20.f, .5f, 0)

  //build scene by pushing objects to singleton GLScene (GLRenderWindow renders it by default)
  trees.foreach( t => GLScene.push( t ) )
  fabrics.foreach( f => GLScene.push( f ) )

}
