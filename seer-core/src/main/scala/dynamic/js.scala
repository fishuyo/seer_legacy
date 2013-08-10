package com.fishuyo.dynamic

import scala.language.dynamics
import javax.script._
import java.io._

import monido._
 
 class JS(script:String, var imports:List[String]=List("")) extends scala.Dynamic {

  var loaded = false

  val file = new File(script)

  var engine:ScriptEngine with Invocable = null

  reload()
      
  def language() = "js"

  val monitor = FileMonido(script){
    case ModifiedOrCreated(f) => reload;
    case _ => None
  }

  def reload() = {
    try{
  	  engine = new ScriptEngineManager().getEngineByName("js").asInstanceOf[ScriptEngine with Invocable]
      
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

