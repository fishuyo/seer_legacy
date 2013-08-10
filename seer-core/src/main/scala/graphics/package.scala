
package com.fishuyo
package object graphics {
	implicit def RGBA2Float(c:RGBA) = c.toGray
}