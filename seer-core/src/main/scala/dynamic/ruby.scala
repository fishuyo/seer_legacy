package com.fishuyo.seer.dynamic

//import javax.script._
import java.io._

import scala.language.dynamics
import org.jruby.embed._
import monido._
 
 class Ruby(val scriptPath:String) extends scala.Dynamic {

  var loaded = false

  val file = new File(scriptPath)

  //var engine:ScriptEngine with Invocable = null
  var engine:ScriptingContainer = null
  var receiver:java.lang.Object = null

  reload()
      
  def language() = "Ruby"

  val monitor = FileMonido(scriptPath){
    case ModifiedOrCreated(f) => reload;
    case _ => None
  }

  def reload() = {
    try{
  	  //engine = new ScriptEngineManager().getEngineByName("jruby").asInstanceOf[ScriptEngine with Invocable]
      engine = new ScriptingContainer()
      val auto_import = """
        require 'java'

        module Seer
          include_package "scala"
          include_package "com.fishuyo.seer"
          include_package "com.fishuyo.seer.io"
          include_package "com.fishuyo.seer.maths"
          include_package "com.fishuyo.seer.spatial"
          include_package "com.fishuyo.seer.graphics"
          include_package "com.fishuyo.seer.audio"
          include_package "com.fishuyo.seer.util"
          include_package "com.fishuyo.seer.examples"
        end

        class Object
          class << self
            alias :const_missing_old :const_missing
            def const_missing c
              Seer.const_get c
            end
          end
        end
      """
      // engine.eval(auto_import) 
      // engine.eval(new FileReader(file))
      engine.runScriptlet(auto_import) 
      // receiver = engine.runScriptlet(PathType.CLASSPATH, scriptPath )
      receiver = engine.runScriptlet(new FileReader(file), "scriptPath" )
  	  // engine.runScriptlet(PathType.CLASSPATH, scriptPath )
      loaded = true
      //this.onLoad()
    } catch { 
      case e:Exception => loaded = false; println(e)
    }
  }
         
  def applyDynamic(name: String)(args: Any*){
    if( !loaded ) return
    //try { engine.invokeFunction(name, args.map(_.asInstanceOf[AnyRef]) : _*) }
    try { engine.callMethod(receiver, name, args.map(_.asInstanceOf[AnyRef]) : _*) }
    catch { case e:Exception => println(e) }
  }
                   
  def typed[T] = error("nope")

 }

