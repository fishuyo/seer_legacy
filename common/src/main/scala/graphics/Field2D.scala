
package com.fishuyo
package graphics

import maths._

import javax.media.opengl._
import com.jogamp.common.nio.Buffers
import java.nio.FloatBuffer

object Field2D {
  def apply(width:Int, height:Int ) = new Field2D { w=width; h=height; }
}

class Field2D extends GLAnimatable {

  var go = false
  var data:FloatBuffer = _
  var w:Int = _
  var h:Int = _
  if( w > 0 && h > 0 ) data = Buffers.newDirectFloatBuffer(w*h)

  def apply( i:Int ) = data.get(i)
  def apply( x:Int, y:Int ) = data.get( w*y + x)
  def getToroidal( i:Int, j:Int ) = {
    var x = i; var y = j;
    while( x < 0 ) x += w; while( x >= w ) x -= w;
    while( y < 0 ) y += h; while( y >= h ) y -= h;
    data.get( w*y + x );
  }

  def allocate( x:Int, y:Int ) = { w=x; h=y; data = Buffers.newDirectFloatBuffer( w*h ); }
  def set( i:Int, v:Float ) = data.put( i, v )
  def set( x:Int, y:Int, v:Float) = data.put( w*y + x, v)
  def set( v:Float ) = for( i <- ( 0 until w*h )) data.put( i, v )
  def set( a: Array[Float] ) = {
    //should check array size matches field size
    data.put( a )
    data.rewind
  }

  def normalize() = {

    var max = 0.0f
    var min = Float.MaxValue
    for ( x <- ( 0 until data.capacity )){
      val v = data.get(x)
      if( v > max ) max = v
      if( v < min ) min = v
    }

    if( max.isInfinite ) max = Float.MaxValue
    val range = 1.0f / (max - min)
    println( "min: "+min+"  max: "+max+"  range: " + range )
    for ( x <- (0 until data.capacity) ){
      data.put(x, (data.get(x) - min) * range)
    }  
  }

  def readImage( file: String ) = {

    val image = javax.imageio.ImageIO.read( new java.io.File( file ) )
    var h = image.getHeight()
    var w = image.getWidth()
    var n = h
    if( w > h ) n = w
    
    allocate(n,n)
    
    for( j <- (0 until h); i <- (0 until w)){
      var v = (new java.awt.Color( image.getRGB(i,j) )).getColorComponents(null)
      if( v.length > 1 ) set(i,h-1-j,(v(0)+v(1)+v(2))/3.f ) // TODO real grayscale conversion
      else set(i,h-1-j,v(0))
    }
  }

  override def onDraw( gl: GL2 ) = {
    import GL._; import gl._
    
    updateTexture( GLContext.getCurrent.getGL.getGL2 );
    
    glEnable(GL_TEXTURE_2D);
    glBegin(GL2.GL_QUADS);
    glTexCoord2f(0.0f, 0.0f); glVertex3f(-1.0f, -1.0f, 0.0f);
    glTexCoord2f(0.0f, 1.0f); glVertex3f(-1.0f, 1.0f,  0.0f);
    glTexCoord2f(1.0f, 1.0f); glVertex3f(1.0f, 1.0f, 0.0f);
    glTexCoord2f(1.0f, 0.0f); glVertex3f(1.0f, -1.0f, 0.0f);
    glEnd();
    glDisable(GL_TEXTURE_2D); 
  
  }
  override def step( dt: Float ) = { if( go ) sstep(dt) }
  def sstep( dt: Float ) = {}

  def updateTexture( gl: GL2 ) = {
    import GL._; import gl._

    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
    glTexImage2D(GL_TEXTURE_2D, 0, 3, w, h, 0, GL_LUMINANCE, GL_FLOAT, data);

    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexEnvf(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_DECAL);
    glEnable(GL_TEXTURE_2D);

  }

}

class Vec3Field2D extends GLAnimatable {

  var go = false
  var data:FloatBuffer = _
  var w:Int = _
  var h:Int = _
  if( w > 0 && h > 0 ) data = Buffers.newDirectFloatBuffer(3*w*h)

  def apply( i:Int ) = Vec3(data.get(3*i),data.get(3*i+1),data.get(3*i+2))
  def apply( x:Int, y:Int ) = Vec3( data.get( 3*(w*y + x)),  data.get( 3*(w*y + x)+1),  data.get( 3*(w*y + x)+2 ))

  def getToroidal( i:Int, j:Int ) = {
    var x = i; var y = j;
    while( x < 0 ) x += w; while( x >= w ) x -= w;
    while( y < 0 ) y += h; while( y >= h ) y -= h;
    this( x,y );
  }

  def allocate( x:Int, y:Int ) = { w=x; h=y; data = Buffers.newDirectFloatBuffer( 3*w*h ); }
  def set( i:Int, v:Vec3 ) = { data.put( 3*i, v.x );  data.put( 3*i+1, v.y ); data.put( 3*i+2, v.z ) }
  def set( x:Int, y:Int, v:Vec3) : Unit = set( w*y + x, v)
  def set( a: Array[Float] ) = {
    //should check array size matches field size
    data.put( a )
    data.rewind
  }

  def normalize() = {

    var max = 0.0f
    var min = Float.MaxValue
    for ( x <- ( 0 until data.capacity )){
      val v = data.get(x)
      if( v > max ) max = v
      if( v < min ) min = v
    }

    if( max.isInfinite ) max = Float.MaxValue
    val range = 1.0f / (max - min)
    //println( "min: "+min+"  max: "+max+"  range: " + range )
    for ( x <- (0 until data.capacity) ){
      data.put(x, (data.get(x) - min) * range)
    }  
  }

  def readImage( file: String ) = {

    val image = javax.imageio.ImageIO.read( new java.io.File( file ) )
    var h = image.getHeight()
    var w = image.getWidth()
    var n = h
    if( w > h ) n = w
    
    allocate(n,n)
    
    for( j <- (0 until h); i <- (0 until w)){
      var v = (new java.awt.Color( image.getRGB(i,j) )).getColorComponents(null)
      if( v.length > 1 ) set(i,h-1-j, Vec3(v(0),v(1),v(2)) ) // TODO real grayscale conversion
      else set(i,h-1-j,Vec3(v(0)) )
    }
  }

  override def onDraw( gl: GL2 ) = {
    import GL._; import gl._
    
    updateTexture( GLContext.getCurrent.getGL.getGL2 );
    
    glEnable(GL_TEXTURE_2D);
    glBegin(GL2.GL_QUADS);
    glTexCoord2f(0.0f, 0.0f); glVertex3f(-1.0f, -1.0f, 0.0f);
    glTexCoord2f(0.0f, 1.0f); glVertex3f(-1.0f, 1.0f,  0.0f);
    glTexCoord2f(1.0f, 1.0f); glVertex3f(1.0f, 1.0f, 0.0f);
    glTexCoord2f(1.0f, 0.0f); glVertex3f(1.0f, -1.0f, 0.0f);
    glEnd();
    glDisable(GL_TEXTURE_2D); 
  
  }
  override def step( dt: Float ) = { if( go ) sstep(dt) }
  def sstep( dt: Float ) = {}

  def updateTexture( gl: GL2 ) = {
    import GL._; import gl._

    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
    glTexImage2D(GL_TEXTURE_2D, 0, 3, w, h, 0, GL_RGB, GL_FLOAT, data);

    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexEnvf(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_DECAL);
    glEnable(GL_TEXTURE_2D);

  }

}
