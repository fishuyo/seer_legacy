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

  var blend = false
  var multiPass = false

  var lighting = 1.f
  var lightPosition = Vec3(5,5,5)
  var lightAmbient = RGBA(.2f,.2f,.2f,1)
  var lightDiffuse = RGBA(.6f,.6f,.6f,1)
  var lightSpecular = RGBA(.4f,.4f,.4f,1)

  var texture = 0.f

  // def texture_=(v:Float){ texture = v }
  // def lighting_=(v:Float){ lighting = v }

  def setBlend(b:Boolean) = blend = b

  def setBgColor(c:RGBA) = bg = c
  def setColor(c:RGBA){
    color = c
    this().setUniformf("u_color", color.r, color.g, color.b, color.a)
  }
  def setColor(v:Vec3, a:Float){ setColor( RGBA(v,a) ) }

  def setAlpha(f:Float) = {
    alpha = f
    this().setUniformf("u_alpha", alpha)
  }

  def setLightUniforms() = {
    this().setUniformf("u_lighting", lighting)
    this().setUniformf("u_texture", texture)
    this().setUniformf("u_lightPosition", lightPosition.x, lightPosition.y, lightPosition.z)
    this().setUniformf("u_lightAmbient", lightAmbient.r, lightAmbient.g, lightAmbient.b, lightAmbient.a)
    this().setUniformf("u_lightDiffuse", lightDiffuse.r, lightDiffuse.g, lightDiffuse.b, lightDiffuse.a)
    this().setUniformf("u_lightSpecular", lightSpecular.r, lightSpecular.g, lightSpecular.b, lightSpecular.a)
  }

  def setMatrices() = {
    try{
      MatrixStack()
    	this().setUniformMatrix("u_projectionViewMatrix", MatrixStack.projectionModelViewMatrix() )
      this().setUniformMatrix("u_modelViewMatrix", MatrixStack.modelViewMatrix() )
    	this().setUniformMatrix("u_viewMatrix", MatrixStack.viewMatrix() )
    	this().setUniformMatrix("u_normalMatrix", MatrixStack.normalMatrix() )
      this().setUniformf("u_color", color.r, color.g, color.b, color.a)
      setLightUniforms();
    } catch { case e:Exception => ()}//println(e)}

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
      uniform mat4 u_viewMatrix;
      uniform mat4 u_normalMatrix;
      uniform vec3 u_lightPosition;

      varying vec4 v_color;
      varying vec3 v_normal, v_lightDir, v_eyeVec;
      varying vec2 v_texCoord;

      void main(){
        v_color = u_color;
        vec4 vertex = u_modelViewMatrix * a_position;
        v_normal = vec3(u_normalMatrix * a_normal);
        vec3 V = vertex.xyz;
        v_eyeVec = normalize(-V);
        vec3 light_pos = vec3(u_viewMatrix * vec4(u_lightPosition,1));
        v_lightDir = normalize(vec3(light_pos - V));
        v_texCoord = a_texCoord0;
        gl_Position = u_projectionViewMatrix * a_position; 
      }

    """,
    // Fragment Shader
    """
      #ifdef GL_ES
       precision mediump float;
      #endif

      uniform sampler2D u_texture0;

      uniform float u_texture;
      uniform float u_lighting;
      uniform vec4 u_lightAmbient;
      uniform vec4 u_lightDiffuse;
      uniform vec4 u_lightSpecular;

      varying vec2 v_texCoord;
      varying vec3 v_normal;
      varying vec3 v_eyeVec;
      varying vec3 v_lightDir;
      varying vec4 v_color;

      void main() {
        
        vec4 colorMixed;
        if( u_texture > 0.0){
          vec4 textureColor = texture2D(u_texture0, v_texCoord);
          colorMixed = mix(v_color, textureColor, u_texture);
        }else{
          colorMixed = v_color;
        }

        vec4 final_color = colorMixed * u_lightAmbient;
        vec3 N = normalize(v_normal);
        vec3 L = v_lightDir;
        float lambertTerm = max(dot(N, L), 0.0);
        final_color += u_lightDiffuse * colorMixed * lambertTerm;
        vec3 E = v_eyeVec;
        vec3 R = reflect(-L, N);
        float spec = pow(max(dot(R, E), 0.0), 0.9 + 1e-20);
        final_color += u_lightSpecular * spec;
        gl_FragColor = mix(colorMixed, final_color, u_lighting);
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