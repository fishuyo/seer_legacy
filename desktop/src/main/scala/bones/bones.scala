package com.fishuyo
package bones

import maths._
import graphics._
import spatial._
import io._
import dynamic._
import audio._
import drone._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.glutils._
//import com.badlogic.gdx.graphics.GL10

object Main extends App{

  SimpleAppRun.loadLibs()

  var bones = new Array[GLPrimitive](34)
  for( i <- (0 until 34)) bones(i) = GLPrimitive.cube(Pose(), Vec3(.01f))
  bones.foreach( GLScene.push( _ ))


  val live = new Ruby("src/main/scala/bones/bones.rb")

	TransTrack.bind("point", (i:Int, f:Array[Float])=>{

		if (i > 0 && i < 34){
			bones(i).p.pos.set(f(0),f(1),f(2))
			//bones(i).p.quat.set(f[6],f[3],f[4],f[5])
		}
	})

  TransTrack.start()

  SimpleAppRun()  

}



