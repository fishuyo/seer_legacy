
import com.fishuyo.seer._
import dynamic._

object Loader extends SeerScript {
	// val loader = new SeerScriptLoader("scripts/agentgrid.scala")
	// val loader = new SeerScriptLoader("scripts/gesture.scala")
	// val loader = new SeerScriptLoader("scripts/empty.scala")
	// val loader = new SeerScriptLoader("scripts/texture.scala")
	// val loader = new SeerScriptLoader("scripts/shader.scala")
	// val loader = new SeerScriptLoader("scripts/omni.scala")
	// val loader = new SeerScriptLoader("scripts/puddle.scala")
	// val loader = new SeerScriptLoader("scripts/field.scala")
	val loader = new SeerScriptLoader("scripts/andreou.scala")
	// val loader = new SeerScriptLoader("scripts/trees.scala")
	// val loader = new SeerScriptLoader("scripts/openni.scala")
	// val loader = new SeerScriptLoader("scripts/cloth.scala")
	// val loader = new SeerScriptLoader("scripts/wall.scala")
	// val loader = new SeerScriptLoader("scripts/eisenscript.scala")

// 
	override def onUnload(){
		loader.unload
		// loader2.unload
	}
}
Loader