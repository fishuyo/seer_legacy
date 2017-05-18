

package com.fishuyo.seer
package interface

import hid._

object Test extends App {


  OSCServer

  val joy = new PS3Controller(0) with IO
  // joy.connect
  // joy.sources += joy.getSources

  while(true){}
}