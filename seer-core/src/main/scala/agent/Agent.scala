

package com.fishuyo.seer
package agent

import spatial._

trait Agent {

  val nav = Nav()

  def sense[T](value:T){}
  def think(){}
  def move(){}


}