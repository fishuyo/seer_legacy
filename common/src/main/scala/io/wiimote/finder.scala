
package com.fishuyo
package io.wiimote

import motej._


class WiimoteFinder extends MoteFinderListener {

  var mote: Mote = _
  var finder: MoteFinder = _
  val lock = new scala.concurrent.Lock
  lock.acquire

  def moteFound( m: Mote ) = {
    mote = m
    lock.release
    println("found wiimote!")
  }

  def findMote() : Mote = {
    if( finder == null){
      finder = MoteFinder.getMoteFinder
      finder.addMoteFinderListener(this)
    }
    finder.startDiscovery()
    lock.acquire
    mote
  }

}
