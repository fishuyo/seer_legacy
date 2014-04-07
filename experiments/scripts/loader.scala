
import com.fishuyo.seer._
import dynamic._

object Loader extends SeerScript {
	val loader = new SeerScriptLoader("experiments/scripts/gesture.scala")

	override def onUnload(){
		loader.unload
	}
}
Loader