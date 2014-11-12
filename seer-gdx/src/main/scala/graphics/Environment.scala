
package com.fishuyo.seer
package graphics

import collection.mutable.ListBuffer

class Environment {
  var backgroundColor = RGB.black

  var lights = ListBuffer[Light]()

  var fog = false
  var fogColor = RGB.white
}