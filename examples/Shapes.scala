

package com.fishuyo.seer
package examples.graphics.shapes

import graphics._
import spatial._
import dynamic._

object Main extends SeerApp { 

	// make a list of a few primitive shapes
	val shapes = Tetrahedron() :: Cube() :: Octahedron() :: Dodecahedron() :: Icosahedron() :: List()

  println(shapes.length)
	// modify material and color and arrange shapes in a circle
	shapes.zipWithIndex.foreach { case (model,idx) =>
		model.material = new SpecularMaterial
		model.material.color = HSV(idx.toFloat/shapes.length, 1.f, 1.f)

		val r = 3.f
		val ang = 2*Pi/shapes.length * idx
		model.translate( r*math.cos(ang), r*math.sin(ang), -4)
	}
//  var cube:Model = null

	override def draw(){
		// call each shapes' draw method
		shapes.foreach(_.draw())
//    if(cube != null) cube.draw()
	}

	override def animate(dt:Float){
		// rotate each shape
//    cube = Cube()
//    cube.rotate(1.,1.,1.)
		shapes.foreach( _.rotate(Pi/3.f*dt, 1/3.f*dt, 0))
	}

}