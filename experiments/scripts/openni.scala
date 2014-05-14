
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import io._
import util._
import openni._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{Texture => GdxTexture}
import com.badlogic.gdx.graphics.Pixmap

object Script extends SeerScript {

	// Kinect.disconnect 
	Kinect.connect(0)
	Kinect.trackUser

	override def draw(){
		Sphere().draw
	}

	override def animate(dt:Float){
	}

	override def onUnload(){
		Kinect.disconnect
	}

}
Script
