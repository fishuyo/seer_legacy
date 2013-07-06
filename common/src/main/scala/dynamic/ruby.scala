package com.fishuyo.dynamic

//import javax.script._
import java.io._

import org.jruby.embed._
import monido._
 
 class Ruby(val scriptPath:String, var imports:List[String]=List("")) extends scala.Dynamic {

  var loaded = false

  val file = new File(scriptPath)

  //var engine:ScriptEngine with Invocable = null
  var engine:ScriptingContainer = null

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

        module M
          include_package "scala"
          include_package "com.fishuyo"
          include_package "com.fishuyo.io"
          include_package "com.fishuyo.maths"
          include_package "com.fishuyo.spatial"
          include_package "com.fishuyo.graphics"
          include_package "com.fishuyo.audio"
          include_package "com.fishuyo.util"
          include_package "com.fishuyo.examples"
        """ + imports.foldLeft(""){ case (o,s) => o + "\n" + "include_package \"" + s + "\""} + """
        end

        class Object
          class << self
            alias :const_missing_old :const_missing
            def const_missing c
              M.const_get c
            end
          end
        end
      """
      // engine.eval(auto_import) 
      // engine.eval(new FileReader(file))
      engine.runScriptlet(auto_import) 
      engine.runScriptlet(new FileReader(file), "scriptPath" )
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
    try { engine.callMethod(None, name, args.map(_.asInstanceOf[AnyRef]) : _*) }
    catch { case e:Exception => println(e) }
  }
                   
  def typed[T] = error("nope")

 }

