
package com.fishuyo
package examples.trees
import maths._
import graphics._
import trees._

import java.awt.event._

object Main extends App{
  

  var trees = TreeNode( Vec3(0), .1f ) :: TreeNode( Vec3(3.f,0,0), .3f ) :: TreeNode( Vec3( 6.f,0,0), .3f) :: TreeNode( Vec3(9.f,0,0), .1f) :: List()
  var fabrics = Fabric( Vec3(0), 1.f,1.f,.05f,"xz") :: Fabric( Vec3(3.f,0,0),1.f,1.f,.05f,"xz") :: Fabric( Vec3(6.f,0,0),1.f,1.f,.05f,"xz") :: Fabric( Vec3(9.f,0,0),1.f,1.f,.05f,"xz") :: List()

  trees(0).branch( 6, 45.f, .8f, 0)
  trees(1).branch( 5, 10.f, .9f, 0)
  trees(2).branch( 10, 20.f, .5f, 0)
  trees(3).branch( 8, 20.f, .5f, 0)

  //build scene by pushing objects to singleton GLScene (GLRenderWindow renders it by default)
  trees.foreach( t => GLScene.push( t ) )
  fabrics.foreach( f => GLScene.push( f ) )

  //SimpleAppRun()
  //val window = new graphics.GLRenderWindow
  //window.addKeyMouseListener( Input )

}

object Input extends io.KeyMouseListener {

  var g = true

  override def keyPressed( e: KeyEvent ){
    val k = e.getKeyCode
    k match {
      case KeyEvent.VK_G => 
        g = !g
        if(g) Fabric.g = -10.f else Fabric.g = 0.f
        println( "Gravity: " + Fabric.g )

      case _ => null
    }

  }


}

