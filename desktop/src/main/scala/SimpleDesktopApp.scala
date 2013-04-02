package com.fishuyo
//package gdx.desktop

import io._

import com.badlogic.gdx.utils.GdxNativesLoader
import com.badlogic.gdx.backends.lwjgl._
import com.badlogic.gdx._
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Input.Keys

object SimpleAppRun {
  var fullscreen = false
  var nativesLoaded = false
  loadLibs()
  val app = new SimpleAppListener()
  Inputs.addProcessor(FullscreenKey)

  def loadLibs(){
    if (nativesLoaded) return
    println("loading native libraries..")
    try GdxNativesLoader.load()
    catch { case e:Exception => println(e) } 
    nativesLoaded = true
  }
  def apply() = {
    loadLibs()
    val _app = new SimpleDesktopApp( app )
    _app.run
  }

  def toggleFullscreen(){
    if( fullscreen ){
      Gdx.graphics.setDisplayMode( SimpleAppSize.width, SimpleAppSize.height, false)
    }else{
      Gdx.graphics.setDisplayMode( Gdx.graphics.getDesktopDisplayMode() )
    }
    fullscreen = !fullscreen
  }
}

class SimpleDesktopApp( val app: ApplicationListener ){
  def run = {
    val cfg = new LwjglApplicationConfiguration()
    cfg.title = "seer"
    cfg.useGL20 = true
    cfg.width = SimpleAppSize.width
    cfg.height = SimpleAppSize.height
    new LwjglApplication( app, cfg )
  }
}

object FullscreenKey extends InputAdapter {
  override def keyDown(k:Int) = {
    
    k match {
      case Keys.ESCAPE => SimpleAppRun.toggleFullscreen
      case _ => false
    }
    false
  }
}