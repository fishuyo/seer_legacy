package com.fishuyo.seer

import io._
import audio._
// import audio.PortAudio

import com.badlogic.gdx.utils.GdxNativesLoader
import com.badlogic.gdx.utils.SharedLibraryLoader
import com.badlogic.gdx.backends.lwjgl._
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Input.Keys

import org.lwjgl.opengl.Display
import org.lwjgl.opengl.AWTGLCanvas

import java.awt._
import java.awt.event._

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
      unsafeAddDir("../lib")
      unsafeAddDir("../../lib")
      unsafeAddDir("../../../lib")
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
    cfg.useGL30 = true
    cfg.width = Window.w0
    cfg.height = Window.h0

    usingCanvas = useCanvas

    if( usingCanvas ){
      frame = new Frame
      frame.setLayout(new BorderLayout())
      canvas = new Canvas
      // canvas = new AWTGLCanvas

      frame.add(canvas, BorderLayout.CENTER)
      frame.setSize(cfg.width, cfg.height)
      frame.setTitle(cfg.title)
      frame.addWindowListener(new WindowAdapter(){
        override def windowClosing(we:WindowEvent){
          Gdx.app.exit()
        }
      });
      canvas.setBackground(Color.black)
      frame.show()
      new LwjglApplication( app, cfg, canvas )

    }else{ 

      new LwjglApplication( app, cfg )
    }
    // PortAudio.initialize()
    // PortAudio.start()

  }

  def toggleFullscreen(){
    app.pause()
    if( usingCanvas ){

      if( fullscreen ){

        val w = bounds.getWidth().toInt
        val h = bounds.getHeight().toInt
        // Display.setParent(null)

        // frame.dispose
        frame.removeNotify()
        frame.setUndecorated(false)
        frame.addNotify()
        // frame.add(canvas)
        frame.setBounds( bounds )
        canvas.setBounds( bounds )
        // frame.pack; frame.setVisible(true)

        // Display.setParent(canvas)

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
        frame.removeNotify()
        frame.setUndecorated(true)
        frame.addNotify()
        // frame.add(canvas)
        frame.setBounds( r.getBounds() )
        canvas.setBounds( r.getBounds() )
        // frame.pack; frame.setVisible(true)
        // Display.setParent(canvas)

        //app.resize( r.getWidth().toInt, r.getHeight().toInt )
        // f.setSize(result.getWidth(), result.getHeight());
        // Gdx.graphics.setDisplayMode( Gdx.graphics.getDesktopDisplayMode() )

        Gdx.gl.glViewport(0, 0, w, h)
        app.resize(w,h)
      }

    }else{

      if( fullscreen ){
        Gdx.graphics.setDisplayMode( Window.w0, Window.h0, false)
      }else{
        Gdx.graphics.setDisplayMode( Gdx.graphics.getDesktopDisplayMode() )
      }
    }

    fullscreen = !fullscreen
    app.resume()
  }

  def setDecorated(b:Boolean){
    if( usingCanvas ){
      frame.removeNotify()
      frame.setUndecorated(!b)
      frame.addNotify()
    }
  }
  def setBounds(x:Int,y:Int,w:Int,h:Int){
    frame.setBounds(x,y,w,h)
    canvas.setBounds(x,y,w,h)
    Gdx.gl.glViewport(0, 0, w, h)
    app.resize(w,h)
  }
}


object FullscreenKey extends InputAdapter {

  override def keyDown(k:Int) = {
    
    k match {
      case Keys.ESCAPE => SimpleAppRun.toggleFullscreen
      case Keys.F1 => 
        if( PortAudio.sources.length > 0) PortAudio.toggleRecording()
        else Audio.toggleRecording()
      case _ => false
    }
    false
  }
}