
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import io._
import util._

import de.sciss.osc._

object Script extends SeerScript {

	OSC.clear()
	OSC.disconnect()
	OSC.listen(7110)
	// OSC.connect("127.0.0.1", 8008)
	// OSC.connect("192.168.0.255", 8008)
	// OSC.connect("192.168.3.255", 8008)
	OSC.connect("192.168.1.255", 8008)

	// OSC.bind("/new_user", (f) => { OSC.send("/calibrating", f(0)) })
	// OSC.bind("/new_skel", (f) => { OSC.send("/tracking", f(0)) })
	// OSC.bind("/lost_user", (f) => { OSC.send("/lost",f(0) ) })

	OSC.bindp {
		case Message("/joint", name:String, id:Int, x:Float, y:Float, z:Float) =>
			val pos = Vec3(2*x-1,1-y,z) 
			if( name == "head"){
				OSC.endBundle()
				OSC.startBundle()
			}
			OSC.send("/" + 1 + "/joint/" + name, pos.x, pos.y, pos.z)
		case Message("/lost_user", id:Int) => OSC.send("/lost",1)
		case _ => ()
	}

}
Script
