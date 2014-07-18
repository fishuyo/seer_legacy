

// package com.fishuyo.seer
// package examples.graphics.eisenscript

// import graphics._
// import io._
// import dynamic._
// import parsers._

// import com.badlogic.gdx.graphics.profiling.GLProfiler

// /**
// 	* This example sets up a generative recursive model specified via eisenscript
// 	* 
// 	* Saving the ruby script causes it to be evaluated in turn reloading the model
// 	*/
// object Main extends SeerApp {

// 	var live:Ruby = _

// 	var modelGenerator = EisenScriptParser("""
//     set maxdepth 50
//     r0
//     var scale = 0.99
//     var rotx = 1.57
//     var rotz = 6.0

//     rule r0 {
//       3 * { rz 120  } R1
//       3 * { rz 120 } R2
//     }

//     rule R1 {
//       { x 1.3 rx rotx rz 6 ry 3 s scale } R1
//       { s 1 } sphere
//     }

//     rule R2 {
//       { x -1.3 rz rotz ry 3 s scale } R2
//       { s 1 }  sphere
//     }
// 	""")

// 	var model = Model()
// 	model.material = new SpecularMaterial
// 	model.material.color.set(1,0,0,1)
// 	modelGenerator.buildModel(model)

// 	def setModel(m:Model) = model = m

// 	override def init(){
// 		live = new Ruby("EisenScript.rb")
// 		GLProfiler.enable()
// 	}

// 	override def draw(){
// 		model.draw()
// 		live.draw()

// 		println("draw calls: " + GLProfiler.drawCalls )
// 		println("shader switches: " + GLProfiler.shaderSwitches )

// 		GLProfiler.reset()
// 	}

// 	// animate called once per frame
// 	override def animate(dt:Float){
// 		// call our ruby scripts animate method
// 		live.animate(dt)
// 	}
// }
