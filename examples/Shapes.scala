

package com.fishuyo.seer
package examples.graphics

import com.fishuyo.seer.graphics._
import com.fishuyo.seer.spatial._

object Shapes extends SeerApp { 

	// make a list of a few generated models
	val shapes = Tetrahedron() :: Cube() :: Octahedron() :: Dodecahedron() :: Icosahedron() :: Sphere() :: List()

	// modify material and color and arrange shapes in a circle
	shapes.zipWithIndex.foreach { case (model,idx) =>

		model.material = Material.specular
		model.material.color = HSV(idx.toFloat/shapes.length, 1.f, 1.f)

		val r = 3.f
		val ang = 2*Pi / shapes.length * idx

		model.translate( r * math.cos(ang), r * math.sin(ang), -4.f)
	}

	override def draw(){
		// call each shapes' draw method
		shapes.foreach( _.draw() )
	}

	override def animate(dt:Float){
		// rotate each shape
		shapes.foreach( _.rotate(1/3.f*dt, 1/3.f*dt, 0))
	}

}