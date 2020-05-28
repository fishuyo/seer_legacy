
package com.fishuyo.seer
package graphics
import spatial._

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap


/**
  * Singleton scene object to contain list of scene drawables
  */ 
object Scene extends Scene {

  val loadedScenes = new HashMap[String,Scene]()

  def apply() = new Scene
  def apply(name:String) = loadedScenes.getOrElseUpdate(name, new Scene) 

  def become(name:String) = {

  }
}

/**
  * A Scene contains animatable / drawable models to be rendered by a Renderer
  */
class Scene {
  
  var drawable = new ListBuffer[Drawable]
  var animatable = new ListBuffer[Animatable]
  //val pickable = new ListBuffer[GLPickable]
  //val lights = new ListBuffer[GLLight]

  def +=(o:Any): Unit ={ push(o) }
  def push(o:Any) = o match {
    case d:Animatable => animatable += d; drawable += d
    case d:Drawable => drawable += d
  } 
  def -=(o:Any): Unit ={ remove(o) }
  def remove(o:Any) = o match {
    case d:Animatable => animatable -= d; drawable -= d
    case d:Drawable => drawable -= d
  } 

  def clear() = { drawable.clear; animatable.clear }
  
  def init() = drawable.foreach( d => { d.init(); d.initialized = true })
  def animate( dt: Float ) = animatable.foreach( (a) => {
    if(!a.initialized){
      a.init()
      a.initialized = true
    }
    a.animate(dt) 
  })
  def draw() = drawable.foreach( _.draw() )

  //def pick( r: Ray ) = pickable.foreach( _.pick(r) )
  
}

