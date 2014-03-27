

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