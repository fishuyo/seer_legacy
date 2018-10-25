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

  val loadedShaders = new HashMap[String,Shader]()

  def apply(name:String) : Shader = {
    loadedShaders.get(name) match {
      case Some(s) => return s
      case None => 
        if(!loadedShaders.contains("basic")) Shader.load("basic",DefaultShaders.basic)
        return loadedShaders("basic")
    }
  }


  def load(name:String, path:String) = {
    val s = new Shader
    s.vertFile = Some(File(path+".vert"))
    s.fragFile = Some(File(path+".frag"))
    loadedShaders(name) = s
    s
  }
  def loadFiles(name:String, pathV:String, pathF:String) = {
    val s = new Shader
    s.vertFile = Some(File(pathV))
    s.fragFile = Some(File(pathF))
    loadedShaders(name) = s
    s
  }
  def load(name:String, v:String, f:String) = {
    val s = new Shader
    s.vertCode = v
    s.fragCode = f
    loadedShaders(name) = s
    s
  }
  def load(name:String, code:(String,String)) = {
    val s = new Shader
    s.name = name
    s.vertCode = code._1
    s.fragCode = code._2
    loadedShaders(name) = s
    s
  }

}

class Shader {

  var name = ""
  var loaded = false
  var reloadFiles = false
  var monitoring = false

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
        if(s.hasUniform(u._1) ){  // TODO use immutable && currentUniforms.getOrElse(u._1,null) != u._2 ){
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
            case v:Array[Vec2] => 
              val vs = v.flatMap((u:Vec2) => Array(u.x,u.y))              
              s.setUniform2fv(u._1, vs, 0, vs.length)

            case _ => println("TODO: implement uniform type: " + u._1 + " " + u._2)
          }
          currentUniforms += u
        }
        u._2 match {
          case v:Array[Vec2] => 
            val vs = v.flatMap((u:Vec2) => Array(u.x,u.y))
            s.setUniform2fv(u._1, vs, 0, vs.length)
            currentUniforms += u
          case _ => ()  
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
    if(monitoring) return this
    if( vertFile.isEmpty || fragFile.isEmpty ) return this
    val that = this;
    try{
      Monitor( vertFile.get.path() ){ (p) => {that.reload; println(s"reloading file ${that.vertFile.get.path()}") }}
      Monitor( fragFile.get.path() ){ (p) => {that.reload; println(s"reloading file ${that.fragFile.get.path()}") }}
    } catch { case e:Exception => println(e) }
    monitoring = true
    this
  }

  def stopMonitor(){
    if(!monitoring) return
    Monitor.stop(vertFile.get.path())
    Monitor.stop(fragFile.get.path())
    monitoring = false
  } 
}

