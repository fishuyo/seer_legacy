package com.fishuyo.dynamic

import javax.script._
import java.io._
 
 class Ruby(script:String) extends scala.Dynamic {
 	val file = new File(script)
  val engine = new ScriptEngineManager().getEngineByName("jruby").asInstanceOf[ScriptEngine with Invocable]
   engine.eval(new FileReader(file));
      
  def language() = "Ruby"
         
  def applyDynamic(name: String)(args: Any*) =
    engine.invokeFunction(name, args.map(_.asInstanceOf[AnyRef]) : _*)
                   
  def typed[T] = error("nope")
 }

 object Magic {

  def bean() = println("fucking beans man!")

 }
