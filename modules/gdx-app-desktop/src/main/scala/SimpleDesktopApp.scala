package com.fishuyo.seer

import io._
import graphics._

import com.badlogic.gdx.utils.GdxNativesLoader
import com.badlogic.gdx.utils.SharedLibraryLoader
// import com.badlogic.gdx.backends.lwjgl._
import com.badlogic.gdx.backends.lwjgl3._
// import com.badlogic.gdx.backends.jglfw._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Input.Keys

// import org.lwjgl.opengl.Display
// import org.lwjgl.opengl.AWTGLCanvas
// import org.lwjgl.opengl.GL11;

import org.lwjgl.glfw.GLFW

object DesktopApp {

  // loadLibs()

  var displayMode:Option[Long] = _

  var fullscreen = false
  var nativesLoaded = false

  var app:ApplicationListener = _ //new SeerAppListener()

  Inputs.addProcessor(FullscreenKey)

  // for use to get fullscreen effect accross multiple monitors
  var usingCanvas = false
  var restoring = false

  // load gdx libs and other common natives
  def loadLibs(){
    if (nativesLoaded) return
    println("loading native libraries..")
    try {
      GdxNativesLoader.load()
      util.Hack.unsafeAddDir("lib")
      util.Hack.unsafeAddDir("../lib")
      util.Hack.unsafeAddDir("../../lib")
      util.Hack.unsafeAddDir("../../../lib")
      println("loaded.")
    } catch { case e:Exception => println(e) } 
    nativesLoaded = true
  }

  // create and run the lwjgl application
  def run(appl:ApplicationListener = null, useCanvas:Boolean = false) = {
    
    if(appl == null) app = new SeerAppListener
    else app = appl

    // loadLibs()

    val cfg = new Lwjgl3ApplicationConfiguration()
    // val cfg = new LwjglApplicationConfiguration()
    // val cfg = new JglfwApplicationConfiguration()

    cfg.setTitle("seer")
    cfg.setWindowedMode(Window.w0,Window.h0)
    cfg.useVsync(false); //for openvr?
    cfg.setBackBufferConfig(8,8,8,8, 16, 0, 4)
    // cfg.setHdpiMode(Lwjgl3ApplicationConfiguration.HdpiMode.Pixels)
    // cfg.useGL30 = true
    // cfg.width = Window.w0
    // cfg.height = Window.h0

    GLFW.glfwWindowHint(GLFW.GLFW_AUTO_ICONIFY, GLFW.GLFW_FALSE)
    println("GLFW Version: " + GLFW.glfwGetVersionString())

    new Lwjgl3Application( app, cfg )
    // new LwjglApplication( app, cfg )
    // new JglfwApplication( app, cfg )
  }

  def setFullscreen(){
    val mode = Gdx.graphics.getDisplayMode()
    // println(mode)
    // Gdx.graphics.setDisplayMode( mode )
    Gdx.graphics.setFullscreenMode(mode)
    fullscreen = true
  }

  def toggleFullscreen(){

      if( fullscreen ){
        Gdx.graphics.setWindowedMode( Window.w0, Window.h0)
        app.resize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight())

      }else{
        // val monitors = Gdx.graphics.getMonitors()
        // monitors.foreach{ case m => println(s"${m.name} ${m.virtualX} ${m.virtualY}")}

        // println(s"${mode.width} ${mode.height}")
        println("isFullscreen: " + Gdx.graphics.isFullscreen())
        // val g = Gdx.graphics.asInstanceOf[com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics]
        // val w = g.getWindow()
        // val method = w.getClass().getDeclaredMethod("getWindowHandle"); //g.getWindow().getWindowHandle()
        // method.setAccessible(true);
        // val h = method.invoke(w).asInstanceOf[Long];
        // GLFW.glfwSetWindowPos(h, 1440, 0);
        
        GLFW.glfwWindowHint(GLFW.GLFW_AUTO_ICONIFY, GLFW.GLFW_FALSE)

        val mode = Gdx.graphics.getDisplayMode()
        Gdx.graphics.setFullscreenMode(mode)

        app.resize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight())
        GLFW.glfwWindowHint(GLFW.GLFW_AUTO_ICONIFY, GLFW.GLFW_FALSE)

        // println(mode)
        // Gdx.graphics.setDisplayMode( mode )
      }

    fullscreen = !fullscreen
  }
}


object FullscreenKey extends InputAdapter {

  override def keyDown(k:Int) = {
    
    k match {
      case Keys.ESCAPE => DesktopApp.toggleFullscreen
      case Keys.F1 => 
        // Audio().toggleRecording()
      case _ => false
    }
    false
  }
}

