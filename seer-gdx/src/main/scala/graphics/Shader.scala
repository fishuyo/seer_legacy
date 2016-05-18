package com.fishuyo.seer
package graphics

import spatial._
import actor._
import io._

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.files.FileHandle;

// import monido._


object Shader {

  // var load = true
  // var indx = 0;
  // var shader:Option[Shader] = None
  // val loadedShaders = new HashMap[String,Shader]()

  // var defaultMaterial:BasicMaterial = new BasicMaterial

  // var bg = RGBA(0,0,0,1)
  // var color = RGBA(1,1,1,1)
  // var alpha = 1f
  // var fade = 0f
  // var visible = true
  // var wireframe = false
  // var linewidth = 1

  // var blend = false

  // var lightingMix = 1f
  // var lightPosition = Vec3(1,1,1)
  // var lightAmbient = RGBA(.2f,.2f,.2f,1)
  // var lightDiffuse = RGBA(.6f,.6f,.6f,1)
  // var lightSpecular = RGBA(.4f,.4f,.4f,1)
  // var shininess = 1f

  // var textureMix = 0f

  // var camera:NavCamera = Camera

  // def setCamera(cam:NavCamera){ camera = cam}

  // // def texture_=(v:Float){ texture = v }
  // // def lighting_=(v:Float){ lighting = v }

  // def alpha(f:Float) = {
  //   if(f == 1f) RenderGraph.root.depth = true
  //   else RenderGraph.root.depth = false
  //   Scene.alpha = f
  // }

  // def blend(mode:String){ mode.toUpperCase match {
  //   case "ONE" => Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
  //   case "ONEMINUSSRC" => Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
  // }}

  // def lineWidth(f:Float) = Gdx.gl.glLineWidth(f)


  // def setBlend(b:Boolean) = blend = b

  // def setBgColor(c:RGBA) = bg = c
  // def setColor(c:RGBA){
  //   if( shader.isEmpty ) return
  //   color = c
  //   shader.get.uniforms("u_color") = color
  //   // this().setUniformf("u_color", color.r, color.g, color.b, color.a)
  // }
  // def setColor(v:Vec3, a:Float){ setColor( RGBA(v,a) ) }

  // def setAlpha(f:Float) = {
  //   alpha = f
  //   this().setUniformf("u_alpha", alpha)
  // }

  // def setMaterial(material:Material){
    
  //   material match {
  //     case m:ShaderMaterial => setBasicMaterial(m);
  //     case m:SpecularMaterial => setBasicMaterial(m); //lightingMix=1f; shininess = m.shininess
  //     case m:DiffuseMaterial => setBasicMaterial(m); //lightingMix=1f; shininess = 0f
  //     case m:NoMaterial => setMaterial(defaultMaterial)
  //     case m:BasicMaterial => setBasicMaterial(m)
  //     case _ => () //setMaterial(defaultMaterial)
  //   }
  // }

  // def setBasicMaterial(material:BasicMaterial){
  //   setColor(material.color)
  //   visible = material.visible
  //   blend = material.transparent

  //   wireframe = material.wireframe
  //   linewidth = material.linewidth

  //   val s = shader.get
  //   material.texture.foreach( (t) => {t.bind(0); s.uniforms("u_texture0")=0 } )
  //   textureMix = material.textureMix

  //   // var normalMap = None:Option[Texture]
  //   // var specularMap = None:Option[Texture]

  //   lightingMix = material.lightingMix
  //   shininess = material.shininess
  // }

  // def setLightUniforms(){
  //   if( shader.isEmpty ) return
  //   val s = shader.get
  //   s.uniforms("u_lightingMix") = lightingMix
  //   s.uniforms("u_textureMix") = textureMix
  //   s.uniforms("u_lightPosition") = lightPosition
  //   s.uniforms("u_lightAmbient") = lightAmbient
  //   s.uniforms("u_lightDiffuse") = lightDiffuse
  //   s.uniforms("u_lightSpecular") = lightSpecular
  //   s.uniforms("u_shininess") = shininess
  // }

  // def setMatrices(){
  //   if( shader.isEmpty ) return
  //   val s = shader.get
  //   try{
  //     MatrixStack(camera)

  //     s.uniforms("u_projectionViewMatrix") = MatrixStack.projectionModelViewMatrix() 
  //     s.uniforms("u_modelViewMatrix") = MatrixStack.modelViewMatrix() 
  //     s.uniforms("u_viewMatrix") = MatrixStack.viewMatrix() 
  //     s.uniforms("u_modelMatrix") = MatrixStack.modelMatrix() 
  //     s.uniforms("u_normalMatrix") = MatrixStack.normalMatrix()
  //     s.uniforms("u_cameraPosition") = camera.nav.pos
  //     // s.uniforms("u_color") = color
  //     s.uniforms("u_alpha") = alpha
  //     s.uniforms("u_fade") = fade
  //     setLightUniforms()
  //   } catch { case e:Exception => ()} //println(e)}
  //   s.setUniforms() 
  // }

  // def load(s:Shader) = {
  //   if( s.loaded ){
  //     loadedShaders(s.name) = s
  //   }
  //   s
  // }

  // //load new shader program from files
  // def load(name:String, v:FileHandle, f:FileHandle) = {
  //   val s = new Shader
  //   s.load(name,v, f)

  //   if( s.loaded ){
  //     loadedShaders(name) = s
  //   }
  //   s
  // }

  // //load new shader program from strings
  // def load(name:String, v:String, f:String) = {
  //   val s = new Shader
  //   s.load(name,v, f)

  //   if( s.loaded ){
  //     loadedShaders(name) = s
  //   }
  //   s
  // }

  // // return selected shader
  // def apply() = { if(shader.isEmpty) shader = Some(loadedShaders.values.head); shader.get.program.get }

  // // select shader
  // def apply(n:String) = { shader = Some(loadedShaders(n)); shader.get.program.get }
  
  // // called in between frames to reload shader programs
  // def update() = {
  //   loadedShaders.foreach{ case(n,s) => s.update() } 
  // }

  def load(path:String) = {
    val s = new Shader
    s.vertFile = Some(File(path+".vert"))
    s.fragFile = Some(File(path+".frag"))
    s
  }
  def loadFiles(pathV:String,pathF:String) = {
    val s = new Shader
    s.vertFile = Some(File(pathV))
    s.fragFile = Some(File(pathF))
    s
  }
  def load(v:String, f:String) = {
    val s = new Shader
    s.vertCode = v
    s.fragCode = f
    s
  }
  def load(code:(String,String)) = {
    val s = new Shader
    s.vertCode = code._1
    s.fragCode = code._2
    s
  }

}

class Shader {

  var name = ""
  var loaded = false
  var reloadFiles = false

  var program:Option[ShaderProgram] = None

  var vertCode:String = null
  var fragCode:String = null

  var vertFile:Option[FileHandle] = None
  var fragFile:Option[FileHandle] = None

  val uniforms = new HashMap[String,Any]()
  var currentUniforms = new HashMap[String,Any]()

  def load(){
    var s:ShaderProgram=null

    if(vertFile.isDefined && fragFile.isDefined)
      s = new ShaderProgram(vertFile.get, fragFile.get)
    else
      s = new ShaderProgram(vertCode, fragCode)

    currentUniforms = new HashMap[String,Any]()

    if( s.isCompiled() ){
      program = Some(s)
      loaded = true
    }else{
      println( s.getLog() )
    }
  }
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

    vertCode = v
    fragCode = f

    if( s.isCompiled() ){
      name = n
      program = Some(s)
      loaded = true
    }else{
      println( s.getLog() )
    }
  }

  def reload() = reloadFiles = true

  def apply() = {
    if(program.isEmpty || reloadFiles) load()
    program.get
  }

  def begin() = this().begin()
  def end() = this().end()

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
            case v:RGBA => s.setUniformf(u._1, v.r, v.g, v.b, v.a)
            case v:RGB => s.setUniformf(u._1, v.r, v.g, v.b)
            case v:Vec2 => s.setUniformf(u._1, v.x, v.y)
            case v:Vec3 => s.setUniformf(u._1, v.x, v.y, v.z)
            case q:Quat => s.setUniformf(u._1, q.w, q.x, q.y, q.z)

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
  def monitor():Shader = {
    if( vertFile.isEmpty || fragFile.isEmpty ) return this
    val that = this;
    try{
      Monitor( vertFile.get.path() ){ (p) => {that.reload; println(s"reloading file ${that.vertFile.get.path()}") }}
      Monitor( fragFile.get.path() ){ (p) => {that.reload; println(s"reloading file ${that.fragFile.get.path()}") }}
    } catch { case e:Exception => println(e) }
    this
  } 
}

