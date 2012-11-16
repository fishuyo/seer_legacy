package com.fishuyo
//package gdx.desktop

import com.badlogic.gdx.utils.GdxNativesLoader
import com.badlogic.gdx.backends.lwjgl._
import com.badlogic.gdx._
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.InputMultiplexer

object SimpleAppRun {
  var nativesLoaded = false

  def loadLibs() = {
    println("loading native libraries..")
    try GdxNativesLoader.load()
    catch { case e:Exception => println(e) } 
    nativesLoaded = true
  }
  def apply() = {
    if( !nativesLoaded ) loadLibs()
    val app = new SimpleDesktopApp( new SimpleAppListener )
    app.run
  }
}

class SimpleDesktopApp( val app: ApplicationListener ){
  def run = {
    val cfg = new LwjglApplicationConfiguration()
    cfg.title = "seer"
    cfg.useGL20 = false
    cfg.width = SimpleAppSize.width
    cfg.height = SimpleAppSize.height
    new LwjglApplication( app, cfg )
  }
}