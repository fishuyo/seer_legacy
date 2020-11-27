
package seer
import scala.language.implicitConversions
package object audio {
	// implicit def int2gen(i:Int) = new Gen{ var v = i.toFloat; gen = ()=>{v}}
  // implicit def float2gen(f:Float) = new Gen{ var v=f; gen = ()=>{v}}
	implicit def float2gen(f:Float) = new Var(f)
  implicit def func2gen(f: () => Float) = new Gen{ def apply() = f()}
}
