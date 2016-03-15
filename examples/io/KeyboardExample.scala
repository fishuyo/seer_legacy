

package com.fishuyo.seer
package examples.io

import graphics._
import io._

import rx._

object KeyboardExample extends SeerApp { 

  val keyboard = Keyboard()

  // val afgb = Rx{ keyboard.a() && keyboard.f() && keyboard.g() && keyboard.b()}

  override def animate(dt:Float){
    // println(afgb())
  }

}