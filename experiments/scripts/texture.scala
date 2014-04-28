
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import spatial._
import io._
import util._
import audio._

import openni._

import com.badlogic.gdx.graphics.{Texture => GdxTexture}
import com.badlogic.gdx.graphics.Pixmap

object Script extends SeerScript {
	implicit def f2i(f:Float) = f.toInt

	val s = Plane()
	s.material = Material.basic

	val n = 1024
	var p = new Pixmap(n,n,Pixmap.Format.RGB888)
	p.setColor(1,0,0,1)
	p.drawCircle(128,128,20)
	var t:GdxTexture = null

	override def draw(){
		if(t == null){ 
			t = new GdxTexture(p,true)
			t.setFilter( GdxTexture.TextureFilter.Linear, GdxTexture.TextureFilter.Linear)
			s.material.texture = Some(t)
			s.material.textureMix=1.f
		}
		val v = Random.vec3() * (n-1)
		val r = Random.float() * 20
		val c = Random.int()
		p.setColor(c)
		p.drawCircle(v.x,v.y,r)
		t.draw(p,0,0)
		// t.getTextureData.consumePixmap
		s.draw
	}

	override def animate(dt:Float){
	
	}

	val onDepth = (i:Array[Int]) => {
		println(i(0))
	}
}


Kinect.disconnect()
Kinect.connect()
Kinect.trackUser()
// Kinect.startDepth(Script.onDepth)



Script
