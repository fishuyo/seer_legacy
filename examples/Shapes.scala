

package com.fishuyo.seer
package examples.graphics.shapes

import graphics._
import maths._
import dynamic._

object Main extends SeerApp { 

	// make a list of a few primitive shapes
	val shapes = Tetrahedron() :: Cube() :: Octahedron() :: Dodecahedron() :: Icosahedron() :: List()

	// modify material and color and arrange shapes in a circle
	shapes.zipWithIndex.foreach { case (model,idx) =>
		model.material = new SpecularMaterial
		model.material.color = HSV(idx.toFloat/shapes.length, 1.f, 1.f)

		val r = 3.f
		val ang = 2*Pi/shapes.length * idx
		model.translate( r*math.cos(ang), r*math.sin(ang), -4)
	}

	override def draw(){
		// call each shapes' draw method
		shapes.foreach(_.draw())
	}

	override def animate(dt:Float){
		// rotate each shape
		shapes.foreach( _.rotate(Pi/3.f*dt, 1/3.f*dt, 0))
	}

}

// import scala.tools.nsc.interpreter._
// import scala.tools.nsc.Settings


// object Repl {

//   def repl = new ILoop {
//     override def loop(): Unit = {
//       // intp.bind("e", "Double", 2.71828)
//       super.loop()
//     }
//   }

//   def start() = {
//   	val settings = new Settings
//   	settings.Yreplsync.value = true

//   	//use when launching normally outside SBT
//   	// settings.usejavacp.value = true      

//   	//an alternative to 'usejavacp' setting, when launching from within SBT
//   	settings.embeddedDefaults[Repl.type]

//   	repl.process(settings)
// 	}
// }