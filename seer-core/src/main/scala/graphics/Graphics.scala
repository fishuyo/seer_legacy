
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
  def gl10 = Gdx.gl10
  def gl11 = Gdx.gl11
  def gl20 = Gdx.gl20
}

trait Drawable extends GLThis {
  def init(){}
  def draw(){}
}

trait Animatable extends Drawable {
  def animate( dt: Float){}
}



object Graphics {

	def draw(d:Drawable){
		d.draw()
	}


}








