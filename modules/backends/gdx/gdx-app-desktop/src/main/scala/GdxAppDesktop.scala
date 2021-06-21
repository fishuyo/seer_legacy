package seer

import io._
import graphics._

import com.badlogic.gdx.utils.GdxNativesLoader
import com.badlogic.gdx.utils.SharedLibraryLoader
import com.badlogic.gdx.backends.lwjgl3._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Input.Keys

import org.lwjgl.glfw.GLFW

object GdxAppDesktop {

  var fullscreen = false
  var nativesLoaded = false

  var app:ApplicationListener = _ 

  Inputs.addProcessor(FullscreenKey)

  // load gdx libs and other common natives
  def loadLibs(){
    if (nativesLoaded) return
    println("loading native libraries..")
    try {
      GdxNativesLoader.load()
      unsafeAddDir("lib")
      unsafeAddDir("../lib")
      unsafeAddDir("../../lib")
      unsafeAddDir("../../../lib")
      println("loaded.")
    } catch { case e:Exception => println(e) } 
    nativesLoaded = true
  }

  // create and run the lwjgl application
  def run() = {
    app = new SeerAppListener
    
    val cfg = new Lwjgl3ApplicationConfiguration()
    cfg.setTitle("seer")
    cfg.setWindowedMode(Window.w0,Window.h0)
    cfg.setBackBufferConfig(8,8,8,8, 16, 0, 0)
    // cfg.setHdpiMode(Lwjgl3ApplicationConfiguration.HdpiMode.Pixels)
    // cfg.useOpenGL3(true,3,2)

    cfg.setAutoIconify(false)
    // GLFW.glfwWindowHint(GLFW.GLFW_AUTO_ICONIFY, GLFW.GLFW_FALSE)
    // GLFW.glfwWindowHint(GLFW.GLFW_FOCUSED, GLFW.GLFW_FALSE)
    // GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE)
    // println("GLFW Version: " + GLFW.glfwGetVersionString())

    new Lwjgl3Application( app, cfg )
  }

  def setFullscreen(){
    val mode = Gdx.graphics.getDisplayMode()
    Gdx.graphics.setFullscreenMode(mode)
    fullscreen = true
  }

  def toggleFullscreen(){
      if( fullscreen ){
        Gdx.graphics.setWindowedMode( Window.w0, Window.h0)
        app.resize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight())
      }else{
        GLFW.glfwWindowHint(GLFW.GLFW_AUTO_ICONIFY, GLFW.GLFW_FALSE)
        val mode = Gdx.graphics.getDisplayMode()
        Gdx.graphics.setFullscreenMode(mode)
        app.resize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight())
        GLFW.glfwWindowHint(GLFW.GLFW_AUTO_ICONIFY, GLFW.GLFW_FALSE)
      }
    fullscreen = !fullscreen
  }

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
      sys.error("Insufficient permissions; can't modify private variables.")
    case _: NoSuchFieldException =>
      sys.error("JVM implementation incompatible with path hack")
  }

}

object FullscreenKey extends InputAdapter {
  override def keyDown(k:Int) = {
    k match {
      case Keys.ESCAPE => GdxAppDesktop.toggleFullscreen
      // case Keys.F1 => Audio().toggleRecording()
      case _ => false
    }
    false
  }
}

