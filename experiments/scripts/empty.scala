
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

	val s = Plane()
	s.material = Material.basic
	// Run(()=>{s.material.loadTexture("../seer-allosphere/calibration/test.jpg")})
	
	val path = "../seer-allosphere/calibration/alpha9.png"
	Run(()=>{s.material.texture = Some(new GdxTexture(Gdx.files.internal(path), Pixmap.Format.RGB888, true))})
	override def draw(){
		s.material.textureMix = 1.f

		s.draw
	}

	override def animate(dt:Float){
	}

}
Script
