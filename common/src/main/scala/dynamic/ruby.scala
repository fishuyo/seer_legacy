package com.fishuyo.dynamic

import javax.script._
import java.io._

import monido._
 
 class Ruby(script:String, var imports:List[String]=List("")) extends scala.Dynamic {

  var loaded = false

  val file = new File(script)

  var engine:ScriptEngine with Invocable = null

  reload()
      
  def language() = "Ruby"

  val monitor = FileMonido(script){
    case ModifiedOrCreated(f) => reload;
    case _ => None
  }

  def reload() = {
    try{
  	  engine = new ScriptEngineManager().getEngineByName("jruby").asInstanceOf[ScriptEngine with Invocable]
      val auto_import = """
        require 'java'

        module M
          include_package "scala"
          include_package "com.fishuyo"
          include_package "com.fishuyo.io"
          include_package "com.fishuyo.maths"
          include_package "com.fishuyo.spatial"
          include_package "com.fishuyo.graphics"
          include_package "com.fishuyo.util"
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
      engine.eval(auto_import) 
  	  engine.eval(new FileReader(file))
      loaded = true
      //this.onLoad()
    } catch { 
      case e:Exception => loaded = false; println(e)
    }
  }
         
  def applyDynamic(name: String)(args: Any*){
    if( !loaded ) return
    try { engine.invokeFunction(name, args.map(_.asInstanceOf[AnyRef]) : _*) }
    catch { case e:Exception => println(e) }
  }
                   
  def typed[T] = error("nope")

 }

