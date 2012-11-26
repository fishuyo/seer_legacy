
package com.fishuyo
package graphics
import maths._

import javax.swing._

// import javax.media.opengl._
// import javax.media.opengl.awt._
// import javax.media.opengl.glu._
// import com.jogamp.opengl.util._
// import javax.media.opengl.fixedfunc.{GLLightingFunc => L}

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer10

package object salami {
  trait GL10 extends com.badlogic.gdx.graphics.GL10
  trait GL20 extends com.badlogic.gdx.graphics.GL20
}

object GLImmediate {
  val renderer = new ImmediateModeRenderer10
}

trait GLThis {
  def gli = GLImmediate.renderer
  def gl = Gdx.gl
  def gl10 = Gdx.gl10
  def gl11 = Gdx.gl11
  def gl20 = Gdx.gl20 //graphics.getGL20
}

trait GLDrawable extends GLThis {
  def draw(){}
}
trait GLAnimatable extends GLDrawable {
  def step( dt: Float){}
}
class GLLight {}

/*object GLDraw {

  def sphere( r:Float =1.0f ) = {

  }
  def cube( p:Vec3 = Vec3(0), s:Vec3=Vec3(1), c:RGB = RGB.green, wire:Boolean=true )( implicit gl:GL2 ) = {
    import gl._
    glPushMatrix()

    glLineWidth(2.0f);
    if( wire ) glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
    else glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL );

    //glColor3f(c.r,c.g,c.b);           // Set colour to green
    glMaterialfv(GL.GL_FRONT_AND_BACK, L.GL_AMBIENT_AND_DIFFUSE, Array(c.r, c.g, c.b, 0.f), 0 );
    //glDisable( L.GL_LIGHTING )

    glTranslatef (p.x, p.y, p.z); // viewing transformation
    val scale = s / 2.0f;
    glScalef (scale.x, scale.y, scale.z);      // modeling transformation
    // draw the cube

    glBegin(GL2.GL_QUADS);
    // Front Face
    glNormal3f( 0.0f, 0.0f, 1.0f);
    glTexCoord2f(0.0f, 0.0f); glVertex3f(-1.0f, -1.0f,  1.0f);
    glTexCoord2f(1.0f, 0.0f); glVertex3f( 1.0f, -1.0f,  1.0f);
    glTexCoord2f(1.0f, 1.0f); glVertex3f( 1.0f,  1.0f,  1.0f);
    glTexCoord2f(0.0f, 1.0f); glVertex3f(-1.0f,  1.0f,  1.0f);
    // Back Face
    glNormal3f( 0.0f, 0.0f,-1.0f);
    glTexCoord2f(1.0f, 0.0f); glVertex3f(-1.0f, -1.0f, -1.0f);
    glTexCoord2f(1.0f, 1.0f); glVertex3f(-1.0f,  1.0f, -1.0f);
    glTexCoord2f(0.0f, 1.0f); glVertex3f( 1.0f,  1.0f, -1.0f);
    glTexCoord2f(0.0f, 0.0f); glVertex3f( 1.0f, -1.0f, -1.0f);
    // Top Face
    glNormal3f( 0.0f, 1.0f, 0.0f);
    glTexCoord2f(0.0f, 1.0f); glVertex3f(-1.0f,  1.0f, -1.0f);
    glTexCoord2f(0.0f, 0.0f); glVertex3f(-1.0f,  1.0f,  1.0f);
    glTexCoord2f(1.0f, 0.0f); glVertex3f( 1.0f,  1.0f,  1.0f);
    glTexCoord2f(1.0f, 1.0f); glVertex3f( 1.0f,  1.0f, -1.0f);
    // Bottom Face
    glNormal3f( 0.0f,-1.0f, 0.0f);
    glTexCoord2f(1.0f, 1.0f); glVertex3f(-1.0f, -1.0f, -1.0f);
    glTexCoord2f(0.0f, 1.0f); glVertex3f( 1.0f, -1.0f, -1.0f);
    glTexCoord2f(0.0f, 0.0f); glVertex3f( 1.0f, -1.0f,  1.0f);
    glTexCoord2f(1.0f, 0.0f); glVertex3f(-1.0f, -1.0f,  1.0f);
    // Right face
    glNormal3f( 1.0f, 0.0f, 0.0f);
    glTexCoord2f(1.0f, 0.0f); glVertex3f( 1.0f, -1.0f, -1.0f);
    glTexCoord2f(1.0f, 1.0f); glVertex3f( 1.0f,  1.0f, -1.0f);
    glTexCoord2f(0.0f, 1.0f); glVertex3f( 1.0f,  1.0f,  1.0f);
    glTexCoord2f(0.0f, 0.0f); glVertex3f( 1.0f, -1.0f,  1.0f);
    // Left Face
    glNormal3f(-1.0f, 0.0f, 0.0f);
    glTexCoord2f(0.0f, 0.0f); glVertex3f(-1.0f, -1.0f, -1.0f);
    glTexCoord2f(1.0f, 0.0f); glVertex3f(-1.0f, -1.0f,  1.0f);
    glTexCoord2f(1.0f, 1.0f); glVertex3f(-1.0f,  1.0f,  1.0f);
    glTexCoord2f(0.0f, 1.0f); glVertex3f(-1.0f,  1.0f, -1.0f);
    glEnd();
   
    glEnable( L.GL_LIGHTING )
    glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
    glPopMatrix()
  }

  def rect = {

  }
}*/

