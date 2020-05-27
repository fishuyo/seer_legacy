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


object Shader {

  val loadedShaders = new HashMap[String,Shader]()

  def apply(name:String) : Shader = {
    loadedShaders.get(name) match {
      case Some(s) => return s
      case None => 
        if(!loadedShaders.contains("basic")) Shader.loadCode("basic",DefaultShaders.basic)
        return loadedShaders("basic")
    }
  }


  def load(name:String, path:String):Shader = {
    loadFiles(name, path+".vert", path+".frag")
  }

  def loadFiles(name:String, pathV:String, pathF:String):Shader = {
    loadedShaders.get(name) match {
      case Some(s) => s.setFiles(pathV, pathF); s
      case None =>
        val s = new Shader
        s.setFiles(pathV, pathF)
        loadedShaders(name) = s
        s  
    }
  }

  def loadCode(name:String, v:String, f:String):Shader = {
    loadedShaders.get(name) match {
      case Some(s) => s.setCode(v, f); s
      case None =>
        val s = new Shader
        s.setCode(v, f)
        loadedShaders(name) = s
        s  
    }
  }

  def loadCode(name:String, code:(String,String)):Shader = {
    loadCode(name, code._1, code._2)
  }

}

class Shader {

  // var name = ""
  var loaded = false
  var isDirty = false
  var monitoring = false

  var program:Option[ShaderProgram] = None

  var vertCode:Option[String] = None
  var fragCode:Option[String] = None

  var vertFile:Option[FileHandle] = None
  var fragFile:Option[FileHandle] = None

  val uniforms = new HashMap[String,Any]()
  var currentUniforms = new HashMap[String,Any]()

  def load(): Unit ={
    var s:ShaderProgram = null
    isDirty = false

    if(vertFile.isDefined && fragFile.isDefined)
      s = new ShaderProgram(vertFile.get, fragFile.get)
    else if(vertCode.isDefined && fragCode.isDefined)
      s = new ShaderProgram(vertCode.get, fragCode.get)
    else return

    currentUniforms = new HashMap[String,Any]()

    if(s.isCompiled()){
      program = Some(s)
      loaded = true
    }else{
      println(s.getLog())
    }
  }

  def setFiles(vertPath:String, fragPath:String, create:Boolean = true) = {
    vertFile = Some(File(vertPath))
    fragFile = Some(File(fragPath))
    if(create){
      if(!vertFile.get.exists){
        vertFile.get.file.createNewFile
        new java.io.PrintWriter(vertFile.get.file) { write(DefaultShaders.empty._1); close() }
      }
      if(!fragFile.get.exists){
        fragFile.get.file.createNewFile
        new java.io.PrintWriter(fragFile.get.file) { write(DefaultShaders.empty._2); close() }
      }
    }
    isDirty = true
  }

  def setCode(v:String, f:String): Unit ={
    vertCode = Some(v)
    fragCode = Some(f)
    isDirty = true
  }

  def dirty() = isDirty = true

  def apply() = {
    if(program.isEmpty || isDirty) load()
    program.get
  }

  def begin() = this().begin()
  def end() = this().end()

  def setUniforms(): Unit ={
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

  // def update() = {
  //   if( vertFile.isDefined && fragFile.isDefined && isDirty ){
  //     load(name,vertFile.get,fragFile.get) 
  //     isDirty = false
  //   }
  // }

  // dirty shader when files modified
  // def monitor():Shader = {
  //   if(monitoring) return this
  //   if( vertFile.isEmpty || fragFile.isEmpty ) return this
  //   val that = this;
  //   try{
  //     Monitor( vertFile.get.path() ){ (p) => {that.dirty; println(s"reloading file ${that.vertFile.get.path()}") }}
  //     Monitor( fragFile.get.path() ){ (p) => {that.dirty; println(s"reloading file ${that.fragFile.get.path()}") }}
  //   } catch { case e:Exception => println(e) }
  //   monitoring = true
  //   this
  // }

  // def stopMonitor(): Unit ={
  //   if(!monitoring) return
  //   Monitor.stop(vertFile.get.path())
  //   Monitor.stop(fragFile.get.path())
  //   monitoring = false
  // } 
}

