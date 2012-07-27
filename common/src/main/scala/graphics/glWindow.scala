
package com.fishuyo
package graphics
import maths._
//import ray._
import io._
import media._

import java.nio.FloatBuffer
import javax.swing._
import java.awt._
import java.awt.event._
import java.awt.image.BufferedImage

import javax.media.opengl._
import javax.media.opengl.awt._
import javax.media.opengl.glu._
import com.jogamp.opengl.util._
import com.jogamp.opengl.util.awt.Screenshot
import javax.media.opengl.fixedfunc.{GLLightingFunc => L}

class GLRenderWindow( var scene:GLScene=GLScene, var camera:Camera=Camera ) extends GLEventListener{
  
  //var scene = GLScene
  //var camera = Camera
  var input = new NavInput(camera)
  var name = "scalaAV"
  var w=600
  var h=600
  var fps=60
  var fullscreen=false
  var displayChanged=false
  var dm_old:DisplayMode = null

  val profile = GLProfile.get(GLProfile.GL2);
  val capabilities = new GLCapabilities(profile);
  
  var capture: MediaWriter = _
  var cap_num = 0

  // The canvas is the widget that's drawn in the JFrame
  val glcanvas = new GLCanvas(capabilities);
  glcanvas.addGLEventListener(this);
  glcanvas.setSize( w, h );
                                 
  var frame = new JFrame( name );
  frame.getContentPane().add( glcanvas);
  frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE )
  
  frame.setSize( frame.getContentPane().getPreferredSize() );
  frame.setVisible( true );
  frame.requestFocus

  val animator = new FPSAnimator( glcanvas, fps )
  animator.start
  
  val glu = new GLU();


  def init(drawable: GLAutoDrawable) = { 

    println( "init called." )
    val gl = drawable.getGL().getGL2();
    
    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    //gl.glLightModeli(GL2ES1.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_TRUE )
    //gl.glShadeModel(L.GL_SMOOTH);

    //gl.glMaterialfv(GL.GL_FRONT, L.GL_SPECULAR, Array(1.f,1.f,1.f,1.f), 0  );
    //gl.glMaterialfv(GL.GL_FRONT, L.GL_SHININESS, Array(50.f), 0 );
    //gl.glLightfv(L.GL_LIGHT0, L.GL_POSITION, Array(0.f,0.f,0.f,0.f), 0 );

    //gl.glEnable(L.GL_LIGHTING)
    //gl.glEnable(L.GL_LIGHT0)
    gl.glEnable(GL.GL_DEPTH_TEST)
    //gl.glEnable(GL.GL_CULL_FACE )

    gl.setSwapInterval(1)

    addKeyMouseListener(input)
    glcanvas.addKeyListener( new KeyMouseListener { override def keyPressed(e:KeyEvent) = { if(e.getKeyCode == KeyEvent.VK_ESCAPE) toggleFullscreen }} )

  }

  def reshape(drawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int) = {
  
    //println("reshape() called: x = "+x+", y = "+y+", width = "+width+", height = "+height);
    val gl = drawable.getGL().getGL2();
    
    var hh = height
    if (hh <= 0) hh = 1;
    
    val h = width.toFloat / hh.toFloat;
    
    gl.glViewport(0, 0, width, hh);
    
    camera.aspect = h
    camera.loadGLProjection(gl)

  }
  def display(drawable: GLAutoDrawable) = { 
 
    val gl = drawable.getGL().getGL2();
    camera.step( 1.f/fps.toFloat);
    val p = camera.position
    val az = camera.azimuth
    val el = camera.elevation

    scene.step( 1.f/fps.toFloat);

    //gl.glMaterialfv(GL.GL_FRONT_AND_BACK, L.GL_AMBIENT_AND_DIFFUSE, Array(.8f, 0.f, 0.f, 0.f), 0 );
    
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    
    camera.loadGLModelview(gl)

    //gl.glScalef( .1f, .1f, .1f )
    scene.onDraw( gl )

    gl.glFlush()

    if( capture != null ) capture.addFrame( this.getImage() )
  }
  
  def displayChanged(drawable: GLAutoDrawable, modeChanged: Boolean, deviceChanged: Boolean) = { println( "displayChanged called." ) }
  def dispose( drawable: GLAutoDrawable ) = { println( "dispose called." ) }

  def getImage() : BufferedImage = {
    Screenshot.readToBufferedImage(0,0,w,h,false)
  }

  def addKeyMouseListener( l: KeyMouseListener ) = { 
    glcanvas.addKeyListener(l)
    glcanvas.addMouseListener(l)
    glcanvas.addMouseMotionListener(l)
  }

  def toggleCapture() = {
    if( capture == null ) capture = new MediaWriter
    else{
      capture.close
      capture = null
    }
  }

  def toggleFullscreen() = {
    val ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    val gd = ge.getDefaultScreenDevice();
    
    if(!fullscreen){
      dm_old = gd.getDisplayMode();
      var dm = dm_old;
      
      frame = new JFrame( name );
      frame.getContentPane().add( glcanvas);
      frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE )
      frame.setVisible( false )
      frame.setUndecorated( true );
      frame.setVisible( true );
      frame.requestFocus
      
      if( gd.isFullScreenSupported() ){
        println("Fullscreen...");//ddd
        try {
          gd.setFullScreenWindow( frame );
          fullscreen = true; 
        } catch {
          case e:Exception =>
          gd.setFullScreenWindow( null );
          fullscreen = false; 
        }
        // Once an application is in full-screen exclusive mode, 
        // it may be able to take advantage of actively setting the display mode.
        if( fullscreen && gd.isDisplayChangeSupported() ) {
          // Change displaymode here [..]
          try {
            gd.setDisplayMode( dm );
            displayChanged = true;
          } catch {
            case e:Exception =>
            gd.setDisplayMode( dm_old );
            displayChanged = false;
          }
        }
      }
    } else {
      if( gd.isFullScreenSupported() ){
        gd.setFullScreenWindow(null);
        println("Exit fullscreen done.");//ddd
        //if( displayChanged ) gd.setDisplayMode( dm_old ); 
        fullscreen = false; 
      } 
    
    }
  }
}


