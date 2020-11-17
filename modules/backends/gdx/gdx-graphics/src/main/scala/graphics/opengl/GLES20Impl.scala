// eww.. wrapping wrapper wrappers
package seer.graphics

import java.nio.Buffer
import java.nio.FloatBuffer 
import java.nio.IntBuffer

import com.badlogic.gdx.Gdx


class GLES20Impl extends GLES20 {

  def glActiveTexture (texture:Int):Unit = Gdx.gl.glActiveTexture(texture)

  def glBindTexture (target:Int, texture:Int):Unit = Gdx.gl.glBindTexture(target, texture)

  def glBlendFunc (sfactor:Int, dfactor:Int):Unit = {}

  def glClear (mask:Int):Unit = {}

  def glClearColor (red:Float, green:Float, blue:Float, alpha:Float):Unit = {}

  def glClearDepthf (depth:Float):Unit = {}

  def glClearStencil (s:Int):Unit = {}

  def glColorMask (red:Boolean, green:Boolean, blue:Boolean, alpha:Boolean):Unit = {}

  def glCompressedTexImage2D (target:Int, level:Int, internalformat:Int, width:Int, height:Int, border:Int, imageSize:Int, data:Buffer):Unit = {}

  def glCompressedTexSubImage2D (target:Int, level:Int, xoffset:Int, yoffset:Int, width:Int, height:Int, format:Int, imageSize:Int, data:Buffer):Unit = {}

  def glCopyTexImage2D (target:Int, level:Int, internalformat:Int, x:Int, y:Int, width:Int, height:Int, border:Int):Unit = {}

  def glCopyTexSubImage2D (target:Int, level:Int, xoffset:Int, yoffset:Int, x:Int, y:Int, width:Int, height:Int):Unit = {}

  def glCullFace (mode:Int):Unit = {}

  def glDeleteTextures (n:Int, textures:IntBuffer):Unit = Gdx.gl.glDeleteTextures(n,textures)
  
  def glDeleteTexture (texture:Int):Unit = Gdx.gl.glDeleteTexture(texture)

  def glDepthFunc (func:Int):Unit = {}

  def glDepthMask (flag:Boolean):Unit = {}

  def glDepthRangef (zNear:Float, zFar:Float):Unit = {}

  def glDisable (cap:Int):Unit = Gdx.gl.glDisable(cap)

  def glDrawArrays (mode:Int, first:Int, count:Int):Unit = {}

  def glDrawElements (mode:Int, count:Int, `type`:Int, indices:Buffer):Unit = {}

  def glEnable (cap:Int):Unit = Gdx.gl.glEnable(cap)

  def glFinish ():Unit = {}

  def glFlush ():Unit = {}

  def glFrontFace (mode:Int):Unit = {}

  def glGenTextures (n:Int, textures:IntBuffer):Unit = Gdx.gl.glGenTextures(n,textures)
  
  def glGenTexture ():Int = 0

  def glGetError ():Int = 0

  def glGetIntegerv (pname:Int, params:IntBuffer):Unit = {}

  def glGetString (name:Int):String = ""

  def glHint (target:Int, mode:Int):Unit = {}

  def glLineWidth (width:Float):Unit = {}

  def glPixelStorei (pname:Int, param:Int):Unit = {}

  def glPolygonOffset (factor:Float, units:Float):Unit = {}

  def glReadPixels (x:Int, y:Int, width:Int, height:Int, format:Int, `type`:Int, pixels:Buffer):Unit = Gdx.gl.glReadPixels(x,y,width, height, format, `type`, pixels)

  def glScissor (x:Int, y:Int, width:Int, height:Int):Unit = Gdx.gl.glScissor(x,y,width,height)

  def glStencilFunc (func:Int, ref:Int, mask:Int):Unit = {}

  def glStencilMask (mask:Int):Unit = {}

  def glStencilOp (fail:Int, zfail:Int, zpass:Int):Unit = {}

  def glTexImage2D (target:Int, level:Int, internalformat:Int, width:Int, height:Int, border:Int, format:Int, `type`:Int, pixels:Buffer):Unit = Gdx.gl.glTexImage2D(target, level, internalformat, width, height, border, format, `type`, pixels)

  def glTexParameterf (target:Int, pname:Int, param:Float):Unit = Gdx.gl.glTexParameterf(target,pname,param)

  def glTexSubImage2D (target:Int, level:Int, xoffset:Int, yoffset:Int, width:Int, height:Int, format:Int, `type`:Int, pixels:Buffer):Unit = {}

  def glViewport (x:Int, y:Int, width:Int, height:Int):Unit = {}

  def glAttachShader (program:Int, shader:Int):Unit = {}

  def glBindAttribLocation (program:Int, index:Int, name:String):Unit = {}

  def glBindBuffer (target:Int, buffer:Int):Unit = {}

  def glBindFramebuffer (target:Int, framebuffer:Int):Unit = {}

  def glBindRenderbuffer (target:Int, renderbuffer:Int):Unit = {}

  def glBlendColor (red:Float, green:Float, blue:Float, alpha:Float):Unit = {}

  def glBlendEquation (mode:Int):Unit = {}

  def glBlendEquationSeparate (modeRGB:Int, modeAlpha:Int):Unit = {}

  def glBlendFuncSeparate (srcRGB:Int, dstRGB:Int, srcAlpha:Int, dstAlpha:Int):Unit = {}

  def glBufferData (target:Int, size:Int, data:Buffer, usage:Int):Unit = {}

  def glBufferSubData (target:Int, offset:Int, size:Int, data:Buffer):Unit = {}

  def glCheckFramebufferStatus (target:Int):Int = 0

  def glCompileShader (shader:Int):Unit = {}

  def glCreateProgram ():Int = 0

  def glCreateShader (`type`:Int):Int = 0

  def glDeleteBuffer (buffer:Int):Unit = {}

  def glDeleteBuffers (n:Int, buffers:IntBuffer):Unit = {}

  def glDeleteFramebuffer (framebuffer:Int):Unit = {}
  
  def glDeleteFramebuffers (n:Int, framebuffers:IntBuffer):Unit = {}

  def glDeleteProgram (program:Int):Unit = {}

  def glDeleteRenderbuffer (renderbuffer:Int):Unit = {}
  
  def glDeleteRenderbuffers (n:Int, renderbuffers:IntBuffer):Unit = {}

  def glDeleteShader (shader:Int):Unit = {}

  def glDetachShader (program:Int, shader:Int):Unit = {}

  def glDisableVertexAttribArray (index:Int):Unit = {}

  def glDrawElements (mode:Int, count:Int, `type`:Int, indices:Int):Unit = {}

  def glEnableVertexAttribArray (index:Int):Unit = {}

  def glFramebufferRenderbuffer (target:Int, attachment:Int, renderbuffertarget:Int, renderbuffer:Int):Unit = {}

  def glFramebufferTexture2D (target:Int, attachment:Int, textarget:Int, texture:Int, level:Int):Unit = {}

  def glGenBuffer ():Int = 0

  def glGenBuffers (n:Int, buffers:IntBuffer):Unit = {}

  def glGenerateMipmap (target:Int):Unit = Gdx.gl.glGenerateMipmap(target)

  def glGenFramebuffer ():Int = 0

  def glGenFramebuffers (n:Int, framebuffers:IntBuffer):Unit = {}

  def glGenRenderbuffer ():Int = 0

  def glGenRenderbuffers (n:Int, renderbuffers:IntBuffer):Unit = {}

  // deviates
  def glGetActiveAttrib (program:Int, index:Int, size:IntBuffer, `type`:Buffer):String = ""

  // deviates
  def glGetActiveUniform (program:Int, index:Int, size:IntBuffer, `type`:Buffer):String = ""

  def glGetAttachedShaders (program:Int, maxcount:Int, count:Buffer, shaders:IntBuffer):Unit = {}

  def glGetAttribLocation (program:Int, name:String):Int = 0

  def glGetBooleanv (pname:Int, params:Buffer):Unit = {}

  def glGetBufferParameteriv (target:Int, pname:Int, params:IntBuffer):Unit = {}

  def glGetFloatv (pname:Int, params:FloatBuffer):Unit = {}

  def glGetFramebufferAttachmentParameteriv (target:Int, attachment:Int, pname:Int, params:IntBuffer):Unit = {}

  def glGetProgramiv (program:Int, pname:Int, params:IntBuffer):Unit = {}

  // deviates
  def glGetProgramInfoLog (program:Int):String = ""

  def glGetRenderbufferParameteriv (target:Int, pname:Int, params:IntBuffer):Unit = {}

  def glGetShaderiv (shader:Int, pname:Int, params:IntBuffer):Unit = {}

  // deviates
  def glGetShaderInfoLog (shader:Int):String = ""

  def glGetShaderPrecisionFormat (shadertype:Int, precisiontype:Int, range:IntBuffer, precision:IntBuffer):Unit = {}

  def glGetTexParameterfv (target:Int, pname:Int, params:FloatBuffer):Unit = {}

  def glGetTexParameteriv (target:Int, pname:Int, params:IntBuffer):Unit = {}

  def glGetUniformfv (program:Int, location:Int, params:FloatBuffer):Unit = {}

  def glGetUniformiv (program:Int, location:Int, params:IntBuffer):Unit = {}

  def glGetUniformLocation (program:Int, name:String):Int = 0

  def glGetVertexAttribfv (index:Int, pname:Int, params:FloatBuffer):Unit = {}

  def glGetVertexAttribiv (index:Int, pname:Int, params:IntBuffer):Unit = {}

  def glGetVertexAttribPointerv (index:Int, pname:Int, pointer:Buffer):Unit = {}

  def glIsBuffer (buffer:Int):Boolean = false

  def glIsEnabled (cap:Int):Boolean = false

  def glIsFramebuffer (framebuffer:Int):Boolean = false

  def glIsProgram (program:Int):Boolean = false

  def glIsRenderbuffer (renderbuffer:Int):Boolean = false

  def glIsShader (shader:Int):Boolean = false

  def glIsTexture (texture:Int):Boolean = false

  def glLinkProgram (program:Int):Unit = {}

  def glReleaseShaderCompiler ():Unit = {}

  def glRenderbufferStorage (target:Int, internalformat:Int, width:Int, height:Int):Unit = {}

  def glSampleCoverage (value:Float, invert:Boolean):Unit = {}

  def glShaderBinary (n:Int, shaders:IntBuffer, binaryformat:Int, binary:Buffer, length:Int):Unit = {}

  // Deviates
  def glShaderSource (shader:Int, string:String):Unit = {}

  def glStencilFuncSeparate (face:Int, func:Int, ref:Int, mask:Int):Unit = {}

  def glStencilMaskSeparate (face:Int, mask:Int):Unit = {}

  def glStencilOpSeparate (face:Int, fail:Int, zfail:Int, zpass:Int):Unit = {}

  def glTexParameterfv (target:Int, pname:Int, params:FloatBuffer):Unit = Gdx.gl.glTexParameterfv(target,pname,params)

  def glTexParameteri (target:Int, pname:Int, param:Int):Unit = Gdx.gl.glTexParameteri(target,pname,param)

  def glTexParameteriv (target:Int, pname:Int, params:IntBuffer):Unit = Gdx.gl.glTexParameteriv(target,pname,params)

  def glUniform1f (location:Int, x:Float):Unit = {}

  def glUniform1fv (location:Int, count:Int, v:FloatBuffer):Unit = {}
  
  def glUniform1fv (location:Int, count:Int, v:Array[Float], offset:Int):Unit = {}

  def glUniform1i (location:Int, x:Int):Unit = {}

  def glUniform1iv (location:Int, count:Int, v:IntBuffer):Unit = {}
  
  def glUniform1iv (location:Int, count:Int, v:Array[Int], offset:Int):Unit = {}

  def glUniform2f (location:Int, x:Float, y:Float):Unit = {}

  def glUniform2fv (location:Int, count:Int, v:FloatBuffer):Unit = {}
  
  def glUniform2fv (location:Int, count:Int, v:Array[Float], offset:Int):Unit = {}

  def glUniform2i (location:Int, x:Int, y:Int):Unit = {}

  def glUniform2iv (location:Int, count:Int, v:IntBuffer):Unit = {}
  
  def glUniform2iv (location:Int, count:Int, v:Array[Int], offset:Int):Unit = {}

  def glUniform3f (location:Int, x:Float, y:Float, z:Float):Unit = {}

  def glUniform3fv (location:Int, count:Int, v:FloatBuffer):Unit = {}
  
  def glUniform3fv (location:Int, count:Int, v:Array[Float], offset:Int):Unit = {}

  def glUniform3i (location:Int, x:Int, y:Int, z:Int):Unit = {}

  def glUniform3iv (location:Int, count:Int, v:IntBuffer):Unit = {}
  
  def glUniform3iv (location:Int, count:Int, v:Array[Int], offset:Int):Unit = {}

  def glUniform4f (location:Int, x:Float, y:Float, z:Float, w:Float):Unit = {}

  def glUniform4fv (location:Int, count:Int, v:FloatBuffer):Unit = {}
  
  def glUniform4fv (location:Int, count:Int, v:Array[Float], offset:Int):Unit = {}

  def glUniform4i (location:Int, x:Int, y:Int, z:Int, w:Int):Unit = {}

  def glUniform4iv (location:Int, count:Int, v:IntBuffer):Unit = {}
  
  def glUniform4iv (location:Int, count:Int, v:Array[Int], offset:Int):Unit = {}

  def glUniformMatrix2fv (location:Int, count:Int, transpose:Boolean, value:FloatBuffer):Unit = {}
  
  def glUniformMatrix2fv (location:Int, count:Int, transpose:Boolean, value:Array[Float], offset:Int):Unit = {}

  def glUniformMatrix3fv (location:Int, count:Int, transpose:Boolean, value:FloatBuffer):Unit = {}
  
  def glUniformMatrix3fv (location:Int, count:Int, transpose:Boolean, value:Array[Float], offset:Int):Unit = {}

  def glUniformMatrix4fv (location:Int, count:Int, transpose:Boolean, value:FloatBuffer):Unit = {}
  
  def glUniformMatrix4fv (location:Int, count:Int, transpose:Boolean, value:Array[Float], offset:Int):Unit = {}

  def glUseProgram (program:Int):Unit = {}

  def glValidateProgram (program:Int):Unit = {}

  def glVertexAttrib1f (indx:Int, x:Float):Unit = {}

  def glVertexAttrib1fv (indx:Int, values:FloatBuffer):Unit = {}

  def glVertexAttrib2f (indx:Int, x:Float, y:Float):Unit = {}

  def glVertexAttrib2fv (indx:Int, values:FloatBuffer):Unit = {}

  def glVertexAttrib3f (indx:Int, x:Float, y:Float, z:Float):Unit = {}

  def glVertexAttrib3fv (indx:Int, values:FloatBuffer):Unit = {}

  def glVertexAttrib4f (indx:Int, x:Float, y:Float, z:Float, w:Float):Unit = {}

  def glVertexAttrib4fv (indx:Int, values:FloatBuffer):Unit = {}

  /**
   * In OpenGl core profiles (3.1+), passing a pointer to client memory is not valid.
   * In 3.0 and later, use the other version of this function instead, pass a zero-based
   * offset which references the buffer currently bound to GL_ARRAY_BUFFER.
   */
  def glVertexAttribPointer (indx:Int, size:Int, `type`:Int, normalized:Boolean, stride:Int, ptr:Buffer):Unit = {}

  def glVertexAttribPointer (indx:Int, size:Int, `type`:Int, normalized:Boolean, stride:Int, ptr:Int):Unit = {}

}

