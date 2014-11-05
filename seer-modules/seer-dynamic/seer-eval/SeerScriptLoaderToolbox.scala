package com.fishuyo.seer
package dynamic

import actor._

import graphics.Animatable
import graphics.Scene
import audio.AudioSource
import audio.Audio

//import javax.script._
import java.io._
import scala.io.Source

import scala.language.dynamics
// import monido._
// import com.twitter.util.Eval

import reflect.runtime.universe._
import reflect.runtime.currentMirror
import tools.reflect.ToolBox

class SeerScriptLoader(val scriptPath:String) {

  val toolbox = currentMirror.mkToolBox() 

  var loaded = false

  val file = new File(scriptPath)

  var script:SeerScript = null

  load()
      
  // val monitor = FileMonido(scriptPath){
  //   case ModifiedOrCreated(f) => reload;
  //   case _ => None
  // }
  Monitor(scriptPath){ (p) =>
    reload
  }

  private def load() = {
    try{
      val source = Source.fromFile(file)
      val tree = toolbox.parse(source.mkString)
      script = toolbox.eval(tree).asInstanceOf[SeerScript]
      loaded = true
      Scene.push(script)
      Audio().push(script)
      script.onLoad()
    } catch { 
      case e:Exception => loaded = false; println(e)
    } 
  }
  
  def reload() = {
    try{
      if(script != null) script.preUnload()
      val source = Source.fromFile(file)
      val tree = toolbox.parse(source.mkString)
      val newscript = toolbox.eval(tree).asInstanceOf[SeerScript]
      // newscript.copy(script)
      unload
      script = newscript
      loaded = true
      Scene.push(script)
      Audio().push(script)
    } catch { 
      case e:Exception => loaded = false; println(e)
    }
  }

  def unload(){
    if(script != null){
      Scene.remove(script)
      Audio().sources -= script
      script.onUnload()
      script = null
      loaded = false
    }
  }
                       
}

// class SeerScriptTextLoader {

//   var loaded = false
//   var script:SeerScript = null

//   def load(code:String) = {
//     try{
//       script = Eval[SeerScript](code)
//       loaded = true
//       // Scene.push(script)
//       // Audio().push(script)
//       script.onLoad()
//     } catch { 
//       case e:Exception => loaded = false; println(e)
//     } 
//   }
  
//   def reload(code:String) = {
//     try{
//       if(script != null) script.preUnload()
//       val newscript = Eval[SeerScript](code)
//       unload
//       script = newscript
//       loaded = true
//       // Scene.push(script)
//       // Audio().push(script)
//     } catch { 
//       case e:Exception => loaded = false; println(e)
//     }
//   }

//   def unload(){
//     if(script != null){
//       // Scene.remove(script)
//       // Audio().sources -= script
//       script.onUnload()
//       script = null
//       loaded = false
//     }
//   }
                       
// }

// TODO serialization to maintain state across compilations?

class SeerScript extends Animatable with AudioSource {
// class SeerScript extends scala.Dynamic with Animatable with AudioSource {

  // def selectDynamic(name:String){println(s"$name select")}
  // def applyDynamic(name:String)(args:Any*){println(s"$name called")}
  // def copy[T](from:T){}
  def onLoad(){}
  def onUnload(){}
  def preUnload(){}
}

