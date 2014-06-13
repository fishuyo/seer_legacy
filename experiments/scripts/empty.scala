
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import io._
import util._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{Texture => GdxTexture}
import com.badlogic.gdx.graphics.Pixmap

object Script extends SeerScript {

	val s = Cube()
	s.material = Material.specular
	
	override def draw(){
		s.draw
	}

	override def animate(dt:Float){
		s.rotate(0.0,0,0)
	}

}
Script
