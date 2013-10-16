
package com.fishuyo
package graphics
import maths._

import scala.collection.mutable.ListBuffer
//import javax.media.opengl._

/**
* Singleton scene object to contain list of scene drawables
*/
object GLScene extends GLScene

class GLScene {
  
  var active = true
  var alpha = 1.f

  val drawable = new ListBuffer[GLDrawable]
  val animatable = new ListBuffer[GLAnimatable]
  //val pickable = new ListBuffer[GLPickable]
  //val lights = new ListBuffer[GLLight]

  def push( o: GLDrawable) = drawable += o
  def push( o: GLAnimatable) = { animatable += o;  drawable += o }
  def remove( o: GLAnimatable) = { animatable -= o;  drawable -= o }
  //def pushPickable( o: GLPickable) = pickable += o
  //def push( s: SoundSource) = { sounds += s; objects += s }

  def init() = drawable.foreach( _.init() )
  def step( dt: Float ) = animatable.foreach( _.step(dt) )
  //def onDraw( gl: GL2 ) = drawable.foreach( _.onDraw(gl) )
  def draw() = drawable.foreach( _.draw() )
  def draw2() = drawable.foreach( _.draw2() )
  //def pick( r: Ray ) = pickable.foreach( _.pick(r) )
  
}

object SceneManager {
  val scenes = new ListBuffer[GLScene]
  val active = GLScene :: List()

  def apply(i:Int) = scenes(i)



  // def init() = scenes.foreach( _.init() )
  def step( dt: Float ) = scenes.filter( _.active == true).foreach( _.step(dt) )
  def draw() = scenes.filter( _.active == true).foreach( _.draw() )

}


class RenderNode {
  val scene = new GLScene
  val camera = new Camera

  // val inputs = new ListBuffer[]


}