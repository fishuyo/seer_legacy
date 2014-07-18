
package com.fishuyo.seer
package graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3

object GLImmediate {
  val renderer = new ImmediateModeRenderer20(true,true,2)
}

trait GLThis {
  def gli = GLImmediate.renderer
  def gl = Gdx.gl
  // def GL20 = Gdx.GL20
  // def gl11 = Gdx.gl11
  def gl20 = Gdx.gl20
  def gl30 = Gdx.gl30
}

// trait Drawable extends GLThis {
//   def init(){}
//   def draw(){}
// }

// trait Animatable extends Drawable {
//   def animate( dt: Float){}
// }



object Graphics {

	def draw(d:Drawable){
		d.draw()
	}

  def run(f:()=>Any){

  }

}

object Run{
  def apply[T](f:()=>T) = new RunOnce(f)
}
class RunOnce[T](f:()=>T) extends Drawable{
  var ret:T = null.asInstanceOf[T]
  var ran = false
  Scene.push(this)
  override def draw(){
    try{
    if(!ran){ ret=f(); ran = true}
    } catch{ case e:Exception => println(e)} finally{ Scene.drawable -= this }
  }
}








