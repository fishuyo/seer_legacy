
// package com.fishuyo.seer
// package dynamic

// package com.fishuyo.seer
// package dynamic

// import graphics.Animatable
// import graphics.Scene
// import audio.AudioSource
// import audio.Audio

// //import javax.script._
// import java.io._

// import scala.language.dynamics
// import monido._
 
	
// class LuaScriptLoader(val scriptPath:String) extends scala.Dynamic {

//   var loaded = false

//   val file = new File(scriptPath)

//   var script:SeerScript = null

//   load()
      
//   val monitor = FileMonido(scriptPath){
//     case ModifiedOrCreated(f) => reload;
//     case _ => None
//   }

//   private def load() = {
//     try{
//       script = Eval[SeerScript](file)
//       loaded = true
//       Scene.push(script)
//       Audio.push(script)
//       script.onLoad()
//     } catch { 
//       case e:Exception => loaded = false; println(e)
//     } 
//   }
  
//   def reload() = {
//     try{
//       if(script != null) script.preUnload()
//     	val newscript = Eval[SeerScript](file)
//       unload
//       script = newscript
//       loaded = true
//       Scene.push(script)
//       Audio.push(script)
//     } catch { 
//       case e:Exception => loaded = false; println(e)
//     }
//   }

//   def unload(){
//     if(script != null){
//       Scene.remove(script)
//       Audio.sources -= script
//       script.onUnload()
//       script = null
//       loaded = false
//     }
//   }
         
//   def applyDynamic(name: String)(args: Any*){
//     if( !loaded ) return
//     println(name)
//   }
                   
// }

// class SeerScript extends scala.Dynamic with Animatable with AudioSource {

// 	def selectDynamic(name:String){println(s"$name select")}
// 	def applyDynamic(name:String)(args:Any*){println(s"$name called")}

//   def onLoad(){}
//   def onUnload(){}
//   def preUnload(){}
// }

