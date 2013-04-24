
package com.fishuyo
package object audio {
	implicit def int2gen(i:Int) = new Gen{ var v = i.toFloat; gen = ()=>{v}}
	implicit def float2gen(f:Float) = new Gen{ var v=f; gen = ()=>{v}}
}