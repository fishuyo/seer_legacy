
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import io._
import util._

object Script extends SeerScript {

	OSC.clear()
	OSC.disconnect()
	OSC.listen(7110)
	// OSC.connect("127.0.0.1", 8008)
	OSC.connect("192.168.0.255", 8008)
	// OSC.connect("192.168.1.255", 8008)

	OSC.bind("/new_user", (f) => { println("new user ${f(0)}") })
	// OSC.bind("/user/1", (f) => { })
	// OSC.bind("/user/2", (f) => { })
	// OSC.bind("/user/3", (f) => { })
	// OSC.bind("/user/4", (f) => { })
	OSC.bind("/new_skel", (f) => { println( "calibrated ${f(0)}") })
	OSC.bind("/lost_user", (f) => { println( "lost user ${f(0)}" ) })
	
	OSC.bind("/joint", (f) => {
		var id = f(1).asInstanceOf[Int]
		val name = f(0).asInstanceOf[String]
		val x = 2*f(2).asInstanceOf[Float] - 1.f
		val y = 1.f-f(3).asInstanceOf[Float]
		val z = f(4).asInstanceOf[Float]

		id = 1
		// val pos = Vec3(x,y,z) 

		if( name == "head"){
			OSC.endBundle()
			OSC.startBundle()
		}
		OSC.send("/" + id + "/joint/" + name, x, y, z)
	})

}
Script
