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

import org.lwjgl.opengl.Display
import java.awt._

object SimpleAppRun {

  var displayMode:Option[Long] = _

  var fullscreen = false
  var nativesLoaded = false

  loadLibs() // load gdx before simple app listener

  val app = new SimpleAppListener()
  Inputs.addProcessor(FullscreenKey)

  // for use to get fullscreen effect accross multiple monitors
  var usingCanvas = false
  var bounds = new Rectangle
  var frame:Frame = null
  var canvas:Canvas = null

  // HACK: adds dir to load library path
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

  // load gdx libs and other common natives
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

  // create and run the lwjgl application
  def apply(useCanvas:Boolean = false) = {
    
    loadLibs()

    val cfg = new LwjglApplicationConfiguration()
    cfg.title = "seer"
    cfg.useGL20 = true
    cfg.width = SimpleAppSize.width
    cfg.height = SimpleAppSize.height

    usingCanvas = useCanvas

    if( usingCanvas ){
      frame = new Frame
      canvas = new Canvas

      frame.add(canvas)
      frame.setSize(cfg.width, cfg.height)
      frame.setTitle(cfg.title)
      canvas.setBackground(Color.black)
      frame.show()
      new LwjglApplication( app, cfg, canvas )

    }else{ 

      new LwjglApplication( app, cfg )
    }
  }

  def toggleFullscreen(){
    
    app.pause()
    if( usingCanvas ){

      if( fullscreen ){

        val w = bounds.getWidth().toInt
        val h = bounds.getHeight().toInt
        //Display.setParent(null)

        //frame.dispose
        //frame.setUndecorated(false)
        // frame.add(canvas)
        frame.setBounds( bounds )
        canvas.setBounds( bounds )
        //frame.pack; frame.setVisible(true)

        //Display.setParent(canvas)

        //app.resize( bounds.getWidth().toInt, bounds.getHeight().toInt )

        // Gdx.graphics.setDisplayMode( SimpleAppSize.width, SimpleAppSize.height, false)
        Gdx.gl.glViewport(0, 0, w, h)
        app.resize(w,h)

      }else{
        var r = new Rectangle
        GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().foreach( _.getConfigurations().foreach( (g) => r = r.union( g.getBounds() ) ))
        println( s"fullscreen: $r")

        bounds = frame.getBounds
        val w = r.getWidth.toInt
        val h = r.getHeight.toInt

        // Display.setParent(null)

        // frame.dispose
        // frame.setUndecorated(true)
        // frame.add(canvas)
        frame.setBounds( r.getBounds() )
        canvas.setBounds( r.getBounds() )
        //frame.pack; frame.setVisible(true)
        //Display.setParent(canvas)

        //app.resize( r.getWidth().toInt, r.getHeight().toInt )
        // f.setSize(result.getWidth(), result.getHeight());
        // Gdx.graphics.setDisplayMode( Gdx.graphics.getDesktopDisplayMode() )

        Gdx.gl.glViewport(0, 0, w, h)
        app.resize(w,h)
      }

    }else{

      if( fullscreen ){
        Gdx.graphics.setDisplayMode( SimpleAppSize.width, SimpleAppSize.height, false)
      }else{
        Gdx.graphics.setDisplayMode( Gdx.graphics.getDesktopDisplayMode() )
      }
    }

    fullscreen = !fullscreen
    app.resume()
  }
}

// class SimpleDesktopApp( val app: ApplicationListener ){
//   def run = {
//     val cfg = new LwjglApplicationConfiguration()
//     cfg.title = "seer"
//     cfg.useGL20 = true
//     cfg.width = SimpleAppSize.width
//     cfg.height = SimpleAppSize.height

//     val f = new Frame
//     val c = new Canvas

//     f.add(c)
//     f.setUndecorated(true)
//     f.show()
//     f.setSize(800,800)
//     c.setBackground(Color.red)

//     new LwjglApplication( app, cfg )
//     //new LwjglApplication( app, cfg, c )
//   }
// }

object FullscreenKey extends InputAdapter {

  override def keyDown(k:Int) = {
    
    k match {
      case Keys.ESCAPE => SimpleAppRun.toggleFullscreen
      case _ => false
    }
    false
  }
}