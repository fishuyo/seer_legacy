package com.fishuyo.seer
package graphics

import maths._

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.files.FileHandle;

import monido._

abstract class Uniform 
case class Matrix(m:Matrix4) extends Uniform

object Shader {

  var load = true
  var indx = 0;
  var shader:Option[Shader] = None
  val loadedShaders = new HashMap[String,Shader]()

  var bg = RGBA(0,0,0,1)
  var color = RGBA(1,1,1,1)
  var alpha = 1.f
  var fade = 0.f

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
    if( shader.isEmpty ) return
    color = c
    shader.get.uniforms("u_color") = color
    // this().setUniformf("u_color", color.r, color.g, color.b, color.a)
  }
  def setColor(v:Vec3, a:Float){ setColor( RGBA(v,a) ) }

  def setAlpha(f:Float) = {
    alpha = f
    this().setUniformf("u_alpha", alpha)
  }

  def setMaterial(m:Material){
    
  }

  def setLightUniforms(){
    if( shader.isEmpty ) return
    val s = shader.get
    s.uniforms("u_lighting") = lighting
    s.uniforms("u_texture") = texture
    s.uniforms("u_lightPosition") = lightPosition
    s.uniforms("u_lightAmbient") = lightAmbient
    s.uniforms("u_lightDiffuse") = lightDiffuse
    s.uniforms("u_lightSpecular") = lightSpecular
  }

  def setMatrices(camera:NavCamera = Camera){
    if( shader.isEmpty ) return
    val s = shader.get
    try{
      MatrixStack(camera)

      s.uniforms("u_projectionViewMatrix") = MatrixStack.projectionModelViewMatrix() 
      s.uniforms("u_modelViewMatrix") = MatrixStack.modelViewMatrix() 
      s.uniforms("u_viewMatrix") = MatrixStack.viewMatrix() 
      s.uniforms("u_normalMatrix") = MatrixStack.normalMatrix() 
      // s.uniforms("u_color") = color
      // s.uniforms("u_alpha") = alpha
      // s.uniforms("u_fade") = fade
      setLightUniforms()
    } catch { case e:Exception => println(e)}
    s.setUniforms() 
  }

  def load(s:Shader) = {
    if( s.loaded ){
      loadedShaders(s.name) = s
    }
    s
  }

  //load new shader program from files
  def load(name:String, v:FileHandle, f:FileHandle) = {
    val s = new Shader
    s.load(name,v, f)

    if( s.loaded ){
      loadedShaders(name) = s
    }
    s
  }

  //load new shader program from strings
  def load(name:String, v:String, f:String) = {
    val s = new Shader
    s.load(name,v, f)

    if( s.loaded ){
      loadedShaders(name) = s
    }
    s
  }

  // return selected shader
  def apply() = { if(shader.isEmpty) shader = Some(loadedShaders.values.head); shader.get.program.get }

  // select shader
  def apply(n:String) = { shader = Some(loadedShaders(n)); shader.get.program.get }
  
  // called in between frames to reload shader programs
  def update() = {
    loadedShaders.foreach{ case(n,s) => s.update() } 
  }

}

class Shader {

  var name = ""
  var index = 0
  var loaded = false
  var reloadFiles = false
  var program:Option[ShaderProgram] = None
  var vertFile:Option[FileHandle] = None
  var fragFile:Option[FileHandle] = None

  val uniforms = new HashMap[String,Any]()

  //load new shader program from file
  def load(n:String, v:FileHandle, f:FileHandle) = {

    val s = new ShaderProgram(v, f)
    if( s.isCompiled() ){
      name = n
      program = Some(s)
      vertFile = Some(v)
      fragFile = Some(f)
      loaded = true
    }else{
      println( s.getLog() )
    }
  }

  //load new shader program directly
  def load(n:String, v:String, f:String) = {

    val s = new ShaderProgram(v, f)
    if( s.isCompiled() ){
      name = n
      program = Some(s)
      loaded = true
    }else{
      println( s.getLog() )
    }
  }

  def reload() = reloadFiles = true

  def apply() = program.get
  def begin() = program.get.begin()
  def end() = program.get.end()

  def setUniforms(){
    if( program.isEmpty) return
    val s = program.get

    uniforms.foreach( (u) => {
      // try{
        // s.setUniformMatrix(u._1, u._2)
        if( s.hasUniform(u._1)){
          u._2 match {
            // case Matrix(m) => s.setUniformMatrix(u._1,m)
            case m:Matrix4 => s.setUniformMatrix(u._1, m)
            case f:Float => s.setUniformf(u._1, f)
            case v:Vec2 => s.setUniformf(u._1, v.x, v.y)
            case v:Vec3 => s.setUniformf(u._1, v.x, v.y, v.z)
            case v:RGBA => s.setUniformf(u._1, v.r, v.g, v.b, v.a)
            case _ => println("TODO: implement uniform type: " + u._1 + " " + u._2)
          }
        }
      // } catch { case e:Exception => ()}
    })
    uniforms.clear()
  }

  def update() = {
    if( vertFile.isDefined && fragFile.isDefined && reloadFiles ){
      load(name,vertFile.get,fragFile.get) 
      reloadFiles = false
    }
  }

  // reload shader when files modified
  def monitor(){
    if( vertFile.isEmpty || fragFile.isEmpty ) return
    val that = this;
    try{
      FileMonido( vertFile.get.path() ){
        case ModifiedOrCreated(f) => that.reload
        case _ => None
      }
      FileMonido( fragFile.get.path() ){
        case ModifiedOrCreated(f) => that.reload
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

      uniform float u_alpha;
      uniform float u_fade;
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
        gl_FragColor *= (1.0 - u_fade);
        gl_FragColor.a *= u_alpha;
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