package com.fishuyo
package graphics

import maths.Vec3

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4

import monido._

object Shader {

  var load = true
  var indx = 0;
  var shader:ShaderProgram = null
  var shaders = new HashMap[String,(String,String,ShaderProgram)]()

  // var projModelViewMatrix = new Matrix4()
  // var modelViewMatrix = new Matrix4()
  // var modelMatrix = new Matrix4()
  var bg = (0.f,0.f,0.f,1.f)
  var color = (1.f,1.f,1.f,1.f)
  var alpha = 1.f

  def setBgColor(c:Vec3, a:Float) = bg = (c.x,c.y,c.z,a)
  def setColor(c:Vec3, a:Float) = {
    color = (c.x,c.y,c.z,a)
    this().setUniformf("u_color", color._1, color._2, color._3, color._4)
  }
  def setAlpha(f:Float) = {
    alpha = f
    this().setUniformf("u_alpha", alpha)
  }

  // def matrixTransform( m:Matrix4 ) = Matrix4.mul(modelMatrix.`val`, m.`val`)
  // def matrixClear() = modelMatrix.idt()

  def setMatrices() = {
    try{
    	// projModelViewMatrix.set(Camera.combined)
    	// modelViewMatrix.set(Camera.view)
    	// Matrix4.mul( projModelViewMatrix.`val`, modelMatrix.`val`)
    	// Matrix4.mul( modelViewMatrix.`val`, modelMatrix.`val`)
    	this().setUniformMatrix("u_projectionViewMatrix", MatrixStack() )
    	// this().setUniformMatrix("u_modelViewMatrix", modelViewMatrix)
    	//this().setUniformMatrix("u_normalMatrix", modelViewMatrix.toNormalMatrix())
      // this().setUniformf("u_color", color._1, color._2, color._3, color._4)
    } catch { case e:Exception => e}

  }

  //load new shader program
  def apply(name:String, v:String, f:String) = {

    val s = new ShaderProgram( Gdx.files.internal(v), Gdx.files.internal(f))
    if( s.isCompiled() ){
      val shader = (v,f,s)
      shaders(name) = shader
    }else{
      println( s.getLog() )
    }
  }

  // return selected shader
  def apply() = { if(shader == null) shader = shaders.values.head._3; shader }

  // select shader
  def apply(n:String) = { shader = shaders(n)._3; shader}
  
  // reload shader programs
  def reload() = load = true

  // called in between frames to reload shader programs
  def update() = {
    if( load ){
      shaders.foreach{ case(n,(v,f,s)) => apply(n,v,f) } 
      load = false
    }
  }

  def monitor(name:String) = {
	  FileMonido(shaders(name)._1){
	    case ModifiedOrCreated(f) => reload;
	    case _ => None
	  }
	  FileMonido(shaders(name)._2){
	    case ModifiedOrCreated(f) => reload;
	    case _ => None
	  }
	}

}