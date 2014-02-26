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

  var defaultMaterial:Material = new DiffuseMaterial

  var bg = RGBA(0,0,0,1)
  var color = RGBA(1,1,1,1)
  var alpha = 1.f
  var fade = 0.f
  var visible = true
  var wireframe = false
  var linewidth = 1

  var blend = false

  var lightingMix = 1.f
  var lightPosition = Vec3(1,1,-2)
  var lightAmbient = RGBA(.2f,.2f,.2f,1)
  var lightDiffuse = RGBA(.6f,.6f,.6f,1)
  var lightSpecular = RGBA(.4f,.4f,.4f,1)
  var shininess = 1.f

  var textureMix = 0.f

  var camera:NavCamera = Camera

  def setCamera(cam:NavCamera){ camera = cam}

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

  def setMaterial(material:Material){
    
    material match {
      case m:ShaderMaterial => setBasicMaterial(m);
      case m:SpecularMaterial => setBasicMaterial(m); lightingMix=1.f; shininess = m.shininess
      case m:DiffuseMaterial => setBasicMaterial(m); lightingMix=1.f; shininess = 0.f
      case m:NoMaterial => ()
      case m:BasicMaterial => setBasicMaterial(m)
      case _ => () //setMaterial(defaultMaterial)
    }
  }

  def setBasicMaterial(material:BasicMaterial){
    setColor(material.color)
    visible = material.visible
    blend = material.transparent

    wireframe = material.wireframe
    linewidth = material.linewidth

    val s = shader.get
    material.texture.foreach( (t) => {t.bind(0); s.uniforms("u_texture0")=0 } )
    textureMix = material.textureMix

    // var normalMap = None:Option[Texture]
    // var specularMap = None:Option[Texture]

    lightingMix = 0.f
    shininess = 0.f
  }

  def setLightUniforms(){
    if( shader.isEmpty ) return
    val s = shader.get
    s.uniforms("u_lightingMix") = lightingMix
    s.uniforms("u_textureMix") = textureMix
    s.uniforms("u_lightPosition") = lightPosition
    s.uniforms("u_lightAmbient") = lightAmbient
    s.uniforms("u_lightDiffuse") = lightDiffuse
    s.uniforms("u_lightSpecular") = lightSpecular
    s.uniforms("u_shininess") = shininess
  }

  def setMatrices(){
    if( shader.isEmpty ) return
    val s = shader.get
    try{
      MatrixStack(camera)

      s.uniforms("u_projectionViewMatrix") = MatrixStack.projectionModelViewMatrix() 
      s.uniforms("u_modelViewMatrix") = MatrixStack.modelViewMatrix() 
      s.uniforms("u_viewMatrix") = MatrixStack.viewMatrix() 
      s.uniforms("u_modelMatrix") = MatrixStack.modelMatrix() 
      s.uniforms("u_normalMatrix") = MatrixStack.normalMatrix()
      s.uniforms("u_cameraPosition") = camera.nav.pos
      // s.uniforms("u_color") = color
      // s.uniforms("u_alpha") = alpha
      // s.uniforms("u_fade") = fade
      setLightUniforms()
    } catch { case e:Exception => ()} //println(e)}
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
  var currentUniforms = new HashMap[String,Any]()

  //load new shader program from file
  def load(n:String, v:FileHandle, f:FileHandle) = {

    val s = new ShaderProgram(v, f)
    currentUniforms = new HashMap[String,Any]()

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
    currentUniforms = new HashMap[String,Any]()

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
        if( s.hasUniform(u._1) ){  // TODO use immutable && currentUniforms.getOrElse(u._1,null) != u._2 ){
          u._2 match {
            // case Matrix(m) => s.setUniformMatrix(u._1,m)
            case m:Matrix4 => s.setUniformMatrix(u._1, m)
            case f:Float => s.setUniformf(u._1, f)
            case f:Double => s.setUniformf(u._1, f.toFloat)
            case i:Int => s.setUniformi(u._1, i)
            case i:Long => s.setUniformi(u._1, i.toInt)
            case v:Vec2 => s.setUniformf(u._1, v.x, v.y)
            case v:Vec3 => s.setUniformf(u._1, v.x, v.y, v.z)
            case v:RGBA => s.setUniformf(u._1, v.r, v.g, v.b, v.a)
            case _ => println("TODO: implement uniform type: " + u._1 + " " + u._2)
          }
          currentUniforms += u
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



object VertexSegments {
  val attributes = """
      attribute vec4 a_position;
      attribute vec4 a_normal;
      attribute vec4 a_color;
      attribute vec2 a_texCoord0;
  """

  val matrixUniforms = """
      uniform mat4 u_projectionViewMatrix;
      // uniform mat4 u_modelViewMatrix;
      // uniform mat4 u_viewMatrix;
      uniform mat4 u_modelMatrix;
      uniform mat4 u_normalMatrix;
      uniform vec4 u_cameraPosition;
  """

  val lightUniforms = """
      uniform vec4 u_color;
      uniform vec3 u_lightPosition;
  """

  val basicVarying = """
      varying vec4 v_color;
      varying vec3 v_normal, v_lightDir, v_eyeVec;
      varying vec2 v_texCoord;
  """
}

object DefaultShaders {

  val basic = (
    // Vertex Shader
    """
    attribute vec3 a_position;
      attribute vec3 a_normal;
      attribute vec4 a_color;
      attribute vec2 a_texCoord0;

      uniform vec4 u_color;
      uniform mat4 u_projectionViewMatrix;
      uniform mat4 u_modelViewMatrix;
      uniform mat4 u_viewMatrix;
      uniform mat4 u_modelMatrix;
      uniform mat4 u_normalMatrix;
      uniform vec4 u_cameraPosition;

      uniform vec3 u_lightPosition;

      varying vec4 v_color;
      varying vec3 v_normal, v_pos, v_lightDir, v_eyeVec;
      varying vec2 v_texCoord;
      varying float v_fog;

      void main(){
        v_color = u_color;

        vec4 pos = u_modelViewMatrix * vec4(a_position,1);
        v_pos = vec3(pos) / pos.w;

        v_normal = vec3(u_normalMatrix * vec4(a_normal,0));
        
        v_eyeVec = normalize(-pos.xyz);

        v_lightDir = vec3(u_viewMatrix * vec4(u_lightPosition,0));

        v_texCoord = a_texCoord0;
        gl_Position = u_projectionViewMatrix * vec4(a_position,1); 
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
      uniform float u_textureMix;
      uniform float u_lightingMix;
      uniform vec4 u_lightAmbient;
      uniform vec4 u_lightDiffuse;
      uniform vec4 u_lightSpecular;
      uniform float u_shininess;

      varying vec2 v_texCoord;
      varying vec3 v_normal;
      varying vec3 v_eyeVec;
      varying vec3 v_lightDir;
      varying vec4 v_color;
      varying vec3 v_pos;

      void main() {
        
        vec4 colorMixed;
        if( u_textureMix > 0.0){
          vec4 textureColor = texture2D(u_texture0, v_texCoord);
          colorMixed = mix(v_color, textureColor, u_textureMix);
        }else{
          colorMixed = v_color;
        }

        vec4 final_color = colorMixed * u_lightAmbient;

        vec3 N = normalize(v_normal);
        vec3 L = normalize(v_lightDir);

        float lambertTerm = dot(N,L);
        final_color += u_lightDiffuse * colorMixed * max(lambertTerm,0.0);

        float specularTerm = 0.0;

        //phong
        vec3 R = reflect(-L, N);
        vec3 E = normalize(v_eyeVec); //normalize(-v_pos);
        //float specAngle = max(dot(R,E), 0.0);
        //specularTerm = pow(specAngle, 8.0);

        //blinn
        float halfDotView = max(0.0, dot(N, normalize(L + E)));
        specularTerm = pow(halfDotView, 20.0);
        specularTerm = specularTerm * smoothstep(0.0,0.2,lambertTerm);

        final_color += u_lightSpecular * specularTerm;
        gl_FragColor = mix(colorMixed, final_color, u_lightingMix);
        gl_FragColor *= (1.0 - u_fade);
        gl_FragColor.a *= u_alpha;
      }
    """
  )

  val texture = (
    """
      attribute vec4 a_position;
      attribute vec2 a_texCoord0;

      uniform mat4 u_projectionViewMatrix;

      varying vec2 v_texCoords;

      void main(){

        // pass through the texture coordinate
        v_texCoords = a_texCoord0;
        
        // pass through the quad position
        gl_Position = u_projectionViewMatrix * a_position;
      }
    """,
    """
      #ifdef GL_ES
       precision mediump float;
      #endif

      uniform sampler2D u_texture0;
      varying vec2 v_texCoords;

      void main(){

        gl_FragColor = texture2D(u_texture0, v_texCoords);

      }
    """
  )

  val composite = (
    """
      attribute vec4 a_position;
      attribute vec2 a_texCoord0;

      uniform mat4 u_projectionViewMatrix;

      varying vec2 v_texCoords;

      void main(){

        // pass through the texture coordinate
        v_texCoords = a_texCoord0;
        
        // pass through the quad position
        gl_Position = u_projectionViewMatrix * a_position;
      }
    """,
    """
      #ifdef GL_ES
       precision mediump float;
      #endif

      uniform sampler2D u_texture0;
      uniform sampler2D u_texture1;

      uniform float u_blend0;
      uniform float u_blend1;

      uniform int mode;

      varying vec2 v_texCoords;

      void main(){

        // pull everything we want from the textures
        vec4 color0 = texture2D(u_texture0, v_texCoords) * u_blend0;
        vec4 color1 = texture2D(u_texture1, v_texCoords) * u_blend1;

        if( mode == 0){
          gl_FragColor = color0 + color1;
        }else {
        gl_FragColor = color0 * color1;
        }
      }
    """
  )

  val text = (
    """
      attribute vec4 a_position;
      attribute vec2 a_texCoord0;
      attribute vec4 a_color;

      uniform mat4 u_projTrans;

      varying vec4 v_color;
      varying vec2 v_texCoord;

      void main() {
          gl_Position = u_projTrans * a_position;
          v_texCoord = a_texCoord0;
          v_color = a_color;
      }
    """,
    """
      uniform sampler2D u_texture;

      varying vec4 v_color;
      varying vec2 v_texCoord;

      uniform float smoothing; // = 1.0/16.0;

      void main() {
          float distance = texture2D(u_texture, v_texCoord).a;
          float alpha = smoothstep(0.5 - smoothing, 0.5 + smoothing, distance);
          gl_FragColor = vec4(v_color.rgb, alpha);
      }
    """
  )
}