package com.fishuyo
package graphics

import maths.Vec3

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.files.FileHandle;

import monido._

object Shader {

  var load = true
  var indx = 0;
  var shader:ShaderProgram = null
  val loadedShaderFiles = new HashMap[String,(FileHandle,FileHandle)]()
  val loadedShaders = new HashMap[String,ShaderProgram]()

  var bg = RGBA(0,0,0,1)
  var color = RGBA(1,1,1,1)
  var alpha = 1.f

  var blend = true
  def setBlend(b:Boolean) = blend = b
  var multiPass = false

  def setBgColor(c:RGBA) = bg = c
  def setColor(c:RGBA) = {
    color = c
    this().setUniformf("u_color", color.r, color.g, color.b, color.a)
  }
  def setColor(v:Vec3, a:Float) = {
    color = RGBA(v,a)
    this().setUniformf("u_color", color.r, color.g, color.b, color.a)
  }
  def setAlpha(f:Float) = {
    alpha = f
    this().setUniformf("u_alpha", alpha)
  }


  def setMatrices() = {
    try{
    	this().setUniformMatrix("u_projectionViewMatrix", MatrixStack() )
    	// this().setUniformMatrix("u_modelViewMatrix", modelViewMatrix)
    	//this().setUniformMatrix("u_normalMatrix", modelViewMatrix.toNormalMatrix())
      this().setUniformf("u_color", color.r, color.g, color.b, color.a)
    } catch { case e:Exception => e}

  }

  //load new shader program from file
  def load(name:String, v:FileHandle, f:FileHandle) = {

    val s = new ShaderProgram(v, f)
    if( s.isCompiled() ){
      loadedShaderFiles(name) = (v,f)
      loadedShaders(name) = s
    }else{
      println( s.getLog() )
    }
  }

  //load new shader program directly
  def load(name:String, v:String, f:String) = {

    val s = new ShaderProgram(v, f)
    if( s.isCompiled() ){
      loadedShaders(name) = s
    }else{
      println( s.getLog() )
    }
  }

  // return selected shader
  def apply() = { if(shader == null) shader = loadedShaders.values.head; shader }

  // select shader
  def apply(n:String) = { shader = loadedShaders(n); shader}
  
  // reload shader programs
  def reload() = load = true

  // called in between frames to reload shader programs
  def update() = {
    if( load ){
      loadedShaderFiles.foreach{ case(n,(v,f)) => load(n,v,f) } 
      load = false
    }
  }

  def monitor(name:String) = {
    val that = this;
    try{
  	  FileMonido( loadedShaderFiles(name)._1.path() ){
  	    case ModifiedOrCreated(f) => that.reload;
  	    case _ => None
  	  }
  	  FileMonido( loadedShaderFiles(name)._2.path() ){
  	    case ModifiedOrCreated(f) => that.reload;
  	    case _ => None
  	  }
    } catch { case e:Exception => println(e) }
	}

}


object DefaultShaders {

  val basic = (
    // Vertex Shader
    """
      attribute vec4 a_position;
      attribute vec4 a_normal;
      attribute vec4 a_color;
      attribute vec2 a_texCoord0;

      uniform vec4 u_color;
      uniform mat4 u_projectionViewMatrix;
      uniform mat4 u_modelViewMatrix;
      uniform mat4 u_normalMatrix;

      varying vec3 v_normal;
      varying vec3 v_eye;
      varying vec3 v_lightPosition;
      varying vec4 v_color;

      varying vec2 v_texCoords;



      void main(){

        v_texCoords = a_texCoord0;

        v_eye = -(u_modelViewMatrix * a_position).xyz; //-transformedVertex.xyz;

        // transform normals to the current view
        v_normal = a_normal.xyz; //normalize(u_normalMatrix * a_normal).xyz;

        // pass the light position through
        v_lightPosition = vec3(10.0,10.0,10.0);

        //if( a_color != vec4(0.0,0.0,0.0,1.0)){
        //  v_color = a_color;
        //}else{
          v_color = u_color;  
        //}

        gl_Position = u_projectionViewMatrix * a_position;
      }
    """,
    // Fragment Shader
    """
      #ifdef GL_ES
       precision mediump float;
      #endif

      uniform sampler2D u_texture0;
      uniform sampler2D u_texture1;

      uniform float u_near;
      uniform float u_far;
      uniform float u_useTexture;

      varying vec2 v_texCoords;
      varying float v_depth;
      varying vec3 v_normal;
      varying vec3 v_eye;
      varying vec3 v_lightPosition;
      varying vec4 v_color;

      void main()
      {
        
        vec4 textureColor = texture2D(u_texture0, v_texCoords);
        vec4 textureColor1 = texture2D(u_texture1, vec2(1.0*v_texCoords.y,2.0*v_texCoords.x));

        //gl_FragData[0] = v_color;

        //gl_FragData[0] = vec4( textureColor, depthShift);

        if( u_useTexture == 1.0){
          gl_FragData[0] = textureColor;
        }else{
          gl_FragData[0] = v_color;
        }

        gl_FragData[0] = v_color;
        //gl_FragData[0] = vec4(textureColor.xyz, 1.0);
        //gl_FragData[0] = textureColor;

      }

    """
  )

  val firstPass = (
    """
    """,
    """
    """
  )
  val secondPass = (
    """
    """,
    """
    """
  )  
}