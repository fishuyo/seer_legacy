package com.fishuyo.dynamic

import javax.script._
import java.io._

import monido._
 
 class Ruby(script:String) extends scala.Dynamic {
  var loaded = false
  val file = new File(script)
  var engine:ScriptEngine with Invocable = null
  reload()
  //if(loaded) this.once()
      
  def language() = "Ruby"

  val monitor = FileMonido(script){
    case ModifiedOrCreated(f) => reload;
    case _ => None
  }

  def reload() = {
    try{
  	 engine = new ScriptEngineManager().getEngineByName("jruby").asInstanceOf[ScriptEngine with Invocable]
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

