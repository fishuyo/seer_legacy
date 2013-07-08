package com.fishuyo
//package gdx.desktop

import io._

import com.badlogic.gdx.utils.GdxNativesLoader
import com.badlogic.gdx.utils.SharedLibraryLoader
import com.badlogic.gdx.backends.lwjgl._
import com.badlogic.gdx._
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Input.Keys

object SimpleAppRun {

  var displayMode:Option[Long] = _
  var fullscreen = false
  var nativesLoaded = false
  loadLibs()
  val app = new SimpleAppListener()
  Inputs.addProcessor(FullscreenKey)

  def unsafeAddDir(dir: String) = try {
    val field = classOf[ClassLoader].getDeclaredField("usr_paths")
    field.setAccessible(true)
    val paths = field.get(null).asInstanceOf[Array[String]]
    if(!(paths contains dir)) {
      field.set(null, paths :+ dir)
      System.setProperty("java.library.path",
       System.getProperty("java.library.path") +
       java.io.File.pathSeparator +
       dir)
    }
  } catch {
    case _: IllegalAccessException =>
      error("Insufficient permissions; can't modify private variables.")
    case _: NoSuchFieldException =>
      error("JVM implementation incompatible with path hack")
  }

  def loadLibs(){
    if (nativesLoaded) return
    println("loading native libraries..")
    try {
      GdxNativesLoader.load()
      unsafeAddDir("lib")
      //System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)
      //new SharedLibraryLoader("lib/GlulogicMT.jar").load("GlulogicMT")
    } catch { case e:Exception => println(e) } 
    nativesLoaded = true
  }
  def apply() = {
    loadLibs()
    val _app = new SimpleDesktopApp( app )
    _app.run
  }

  def toggleFullscreen(){
    var i = -1
    displayMode match {
      case Some(v) => i = v.toInt
      case _ => i = -1
    }
    val modes = Gdx.graphics.getDisplayModes
    //modes.foreach( (x) => println(x.width + " " + x.height ))
    val deskmode = Gdx.graphics.getDesktopDisplayMode()
    //println( "desktop: " + deskmode.width + " " + deskmode.height)

    app.pause()
    if( fullscreen ){
      Gdx.graphics.setDisplayMode( SimpleAppSize.width, SimpleAppSize.height, false)
    }else{
      if( i >= 0) Gdx.graphics.setDisplayMode( modes(i))
      else Gdx.graphics.setDisplayMode( Gdx.graphics.getDesktopDisplayMode() )
    }
    fullscreen = !fullscreen
    app.resume()

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