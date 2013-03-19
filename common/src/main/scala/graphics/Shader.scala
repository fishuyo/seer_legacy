package com.fishuyo
package graphics

import scala.collection.mutable.ListBuffer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4

import monido._

object Shader {

  var load = true
  var indx = 0;
  var shader:ShaderProgram = null
  var shaders = new ListBuffer[(String,String,ShaderProgram)]()

  var projModelViewMatrix = new Matrix4()
  var modelViewMatrix = new Matrix4()
  var modelMatrix = new Matrix4()

  def matrixTransform( m:Matrix4 ) = Matrix4.mul(modelMatrix.`val`, m.`val`)
  def matrixClear() = modelMatrix.idt()

  def setMatrices() = {
  	projModelViewMatrix.set(Camera.combined)
  	modelViewMatrix.set(Camera.view)
  	Matrix4.mul( projModelViewMatrix.`val`, modelMatrix.`val`)
  	Matrix4.mul( modelViewMatrix.`val`, modelMatrix.`val`)
  	this().setUniformMatrix("u_projectionViewMatrix", projModelViewMatrix)
  	this().setUniformMatrix("u_modelViewMatrix", modelViewMatrix)
  	//this().setUniformMatrix("u_normalMatrix", modelViewMatrix.toNormalMatrix())
  }


  //load new shader program
  def apply(v:String, f:String, i:Int = -1) = {

    val s = new ShaderProgram( Gdx.files.internal(v), Gdx.files.internal(f))
    if( s.isCompiled() ){
      val shader = (v,f,s)
      if( i >= 0) shaders(i) = shader
      else shaders += shader
    }else{
      println( s.getLog() )
    }
  }

  // return selected shader
  def apply() = { if(shaders.size > indx) shader = shaders(indx)._3; shader }

  // select shader at index i
  def apply(i:Int) = {indx = i; shader = shaders(i)._3; shader}
  
  // reload shader programs
  def reload() = load = true

  // called in between frames to reload shader programs
  def update() = {
    if( load ){
      shaders.zipWithIndex.foreach{ case((v,f,s),i) => apply(v,f,i) } 
      load = false
    }
  }

  def monitor(i:Int) = {
	  FileMonido(shaders(i)._1){
	    case ModifiedOrCreated(f) => reload;
	    case _ => None
	  }
	  FileMonido(shaders(i)._2){
	    case ModifiedOrCreated(f) => reload;
	    case _ => None
	  }
	}

}