
import com.fishuyo.seer._

import dynamic.SeerScript
import graphics._
import io._

object Script extends SeerScript {

	val n = 30
	val s = 1.f / n

	var boost = 1.f 

	val cubes = for(i <- 0 until n) yield { 
		val c = Cube().scale(1,s,1).translate(0,i*s - .5f,0)
		c.material = Material.specular
		c.material.color = HSV(i*s,0.7f,0.7f)
		c
	}

	override def draw(){
		FPS.print
		cubes.foreach( _.draw )
	}
	override def animate(dt:Float){
		cubes.zipWithIndex.foreach {
			case(c,i)	=> c.rotate(0,(i+1)*s/100*boost,0)
		}
	}

	Trackpad.clear
	Trackpad.connect
	Trackpad.bind((touch) => {
		boost = 5*touch.size + 1.f
	})
}

Script