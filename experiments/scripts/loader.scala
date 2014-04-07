
import com.fishuyo.seer._
import dynamic._

object Loader extends SeerScript {
	// val loader = new SeerScriptLoader("experiments/scripts/gesture.scala")
	val loader = new SeerScriptLoader("experiments/scripts/empty.scala")

	override def onUnload(){
		loader.unload
	}
}
Loader