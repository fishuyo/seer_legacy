package seer

import java.net.InetAddress

object Hostname{
	def apply() = {
		val local = InetAddress.getLocalHost()
		val name = local.getHostName()
		// println(s"Hostname: $name")
		name
	}
}