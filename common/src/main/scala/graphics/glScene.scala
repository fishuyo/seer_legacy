
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

  val drawable = new ListBuffer[GLDrawable]
  val animatable = new ListBuffer[GLAnimatable]
  //val pickable = new ListBuffer[GLPickable]
  //val lights = new ListBuffer[GLLight]

  def push( o: GLDrawable) = drawable += o
  def push( o: GLAnimatable) = { animatable += o;  drawable += o }
  //def pushPickable( o: GLPickable) = pickable += o
  //def push( s: SoundSource) = { sounds += s; objects += s }

  def init() = drawable.foreach( _.init() )
  def step( dt: Float ) = animatable.foreach( _.step(dt) )
  //def onDraw( gl: GL2 ) = drawable.foreach( _.onDraw(gl) )
  def draw() = drawable.foreach( _.draw() )
  def draw2() = drawable.foreach( _.draw2() )
  //def pick( r: Ray ) = pickable.foreach( _.pick(r) )
  
}

