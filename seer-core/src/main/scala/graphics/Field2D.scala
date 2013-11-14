
package com.fishuyo
package graphics

import maths._

//import javax.media.opengl._
//import com.jogamp.common.nio.Buffers
import java.nio.FloatBuffer

import com.badlogic.gdx._
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.glutils._
import com.badlogic.gdx.utils.BufferUtils
import com.badlogic.gdx.graphics.Pixmap


object Field2D {
  def apply(width:Int=256, height:Int=256 ) = new Field2D(width,height)
}

class Field2D(var w:Int, var h:Int) extends GLAnimatable {

  var resized = true

  var textureId = 0
  //val pix = new Pixmap(w,h, Pixmap.Format.RGBA8888)

  var data:FloatBuffer = BufferUtils.newFloatBuffer( 4*w*h );
  val quad = Model(Quad())
  //var data: FloatTextureData = _
  //if( w > 0 && h > 0 ) data = Buffers.newDirectFloatBuffer(w*h)

  def apply( i:Int ):RGBA = RGBA(data.get(4*i),data.get(4*i+1),data.get(4*i+2),data.get(4*i+3))
  def apply( x:Int, y:Int ):RGBA = this( w*y + x)
  def get(x:Int,y:Int) = this(w*y+x)
  def getToroidal( i:Int, j:Int ) = {
    var x = i; var y = j;
    while( x < 0 ) x += w; while( x >= w ) x -= w;
    while( y < 0 ) y += h; while( y >= h ) y -= h;
    this( w*y + x );
  }

  def resize( x:Int, y:Int ) = {
    if( x != w || y != h){
      w=x; h=y;
      data = BufferUtils.newFloatBuffer( 4*w*h );
      resized = true
    }
    //if( pixmap != null) pixmap.dispose()
    //pixmap = new Pixmap(w,h,Pixmap.Format.Intensity)
  }
  
  def set( i:Int, v:Float ) { 
    data.position(4*i)
    data.put( Array(v,v,v,1.f),0, 4)
    data.rewind
  }
  def set( i:Int, c:RGBA ) { 
    data.position(4*i)
    data.put( Array(c.r,c.g,c.b,c.a),0, 4)
    data.rewind
  }
  def set( x:Int, y:Int, v:Float){ synchronized{set(w*y+x, v)} }
  def set( x:Int, y:Int, c:RGBA){ set(w*y+x, c) }

  def set( v:Float ){ for( i <- ( 0 until w*h )) set( i, v ) }
  def set( a: Array[Float] ) = {
    //should check array size matches field size
    data.put( a )
    data.rewind
  }

  def set( f:Field2D ){
    data.rewind
    f.data.rewind
    data.put(f.data)
    data.rewind
    f.data.rewind
  }

  def multiplyKernel( x:Int, y:Int, k:Array[Float] ) : Float = {

    var s = scala.math.sqrt(k.length).toInt //assume square kernel
    s = s / 2
    var v = 0.f
    var c = 0
    for( j<-(-s to s); i<-(-s to s)){
      v += k(c) * getToroidal(x+i,y+j).r
      c += 1
    }
    v
  }

  def normalize() = {

    var max = 0.0f
    var min = Float.MaxValue
    for ( x <- ( 0 until w*h )){
      val v = this(x).toGray
      if( v > max ) max = v
      if( v < min ) min = v
    }

    if( max.isInfinite ) max = Float.MaxValue
    val range = 1.0f / (max - min)
    //println( "min: "+min+"  max: "+max+"  range: " + range )
    for ( x <- (0 until w*h) ){
      set(x, (this(x).toGray - min) * range)
    }  
  }

  def readImage( file: String ) = {

    val image = javax.imageio.ImageIO.read( new java.io.File( file ) )
    var h = image.getHeight()
    var w = image.getWidth()
    var n = h
    if( w > h ) n = w
    
    resize(n,n)
    
    for( j <- (0 until h); i <- (0 until w)){
      var v = (new java.awt.Color( image.getRGB(i,j) )).getColorComponents(null)
      if( v.length > 1 ) set(i,h-1-j,(v(0)+v(1)+v(2))/3.f ) // TODO real grayscale conversion
      else set(i,h-1-j,v(0))
    }
  }


  override def init(){
    //textureId = Texture(w,h,data)
  }
  
  def bind(){
    synchronized{
      if(resized){ textureId = Texture(w,h,data); resized = false }

      Texture.bind(textureId)
      Texture(textureId).getTextureData().consumeCompressedData(0) // ? check this
    }
  }

  override def draw(){
    bind()
    quad.draw()
  }
  override def step( dt: Float ) = {}

}
