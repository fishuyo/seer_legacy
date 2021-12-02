package seer.graphics
package backend.webgl

import java.nio.Buffer
import java.nio.FloatBuffer 
import java.nio.IntBuffer

import org.scalajs.dom.raw._
import WebGLRenderingContext._
// import typings.std.{WebGLProgram, WebGLTexture, WebGLBuffer, WebGLShader, WebGLUniformLocation, WebGLRenderingContext}


import collection.mutable.HashMap

import scala.scalajs.js.typedarray
import scala.scalajs.js.typedarray._
import scala.scalajs.js.typedarray.TypedArrayBufferOps._

object TextureUtil {
  val textures = HashMap[Int,WebGLTexture]()
  implicit def int2texture(id:Int) = textures(id) 
  
  def apply(id:Int) = textures(id)
  def nextId():Int = {
    for(i <- 0 until textures.size) 
      if(!textures.contains(i)) return i
    return textures.size
  }

  def createTexture()(implicit gl:WebGLRenderingContext):Int = { 
    val id = nextId()
    textures += id -> gl.createTexture()
    id 
  }

  def deleteTexture(id:Int)(implicit gl:WebGLRenderingContext) = {
    gl.deleteTexture(id)
    textures -= id
  }
}

object BufferUtil {
  val buffers = HashMap[Int,WebGLBuffer]()
  implicit def int2buffer(id:Int) = buffers(id) 
  
  def apply(id:Int) = buffers(id)
  def nextId():Int = {
    for(i <- 0 until buffers.size) 
      if(!buffers.contains(i)) return i
    return buffers.size
  }

  def createBuffer()(implicit gl:WebGLRenderingContext):Int = { 
    val id = nextId()
    buffers += id -> gl.createBuffer()
    id 
  }

  def deleteBuffer(id:Int)(implicit gl:WebGLRenderingContext) = {
    gl.deleteBuffer(id)
    buffers -= id
  }
}

object ShaderUtil {
  val shaders = HashMap[Int,WebGLShader]()
  implicit def int2shader(id:Int) = shaders(id) 
  
  def apply(id:Int) = shaders(id)
  def nextId():Int = {
    for(i <- 0 until shaders.size) 
      if(!shaders.contains(i)) return i
    return shaders.size
  }

  def createShader(typ:Int)(implicit gl:WebGLRenderingContext):Int = { 
    val id = nextId()
    shaders += id -> gl.createShader(typ)
    id 
  }

  def deleteShader(id:Int)(implicit gl:WebGLRenderingContext) = {
    gl.deleteShader(id)
    shaders -= id
  }
}

object ProgramUtil {
  val programs = HashMap[Int,WebGLProgram]()
  implicit def int2program(id:Int) = programs(id) 
  
  def apply(id:Int) = programs(id)
  def nextId():Int = {
    for(i <- 0 until programs.size) 
      if(!programs.contains(i)) return i
    return programs.size
  }

  def createProgram()(implicit gl:WebGLRenderingContext):Int = { 
    val id = nextId()
    programs += id -> gl.createProgram()
    id 
  }

  def deleteProgram(id:Int)(implicit gl:WebGLRenderingContext) = {
    gl.deleteProgram(id)
    programs -= id
  }
}

object UniformLocationUtil {
  val uniforms = HashMap[Int,WebGLUniformLocation]()
  implicit def int2uniform(id:Int) = uniforms(id) 
  
  def apply(id:Int) = uniforms(id)
  def nextId():Int = {
    for(i <- 0 until uniforms.size) 
      if(!uniforms.contains(i)) return i
    return uniforms.size
  }

  def getUniformLocation(program:WebGLProgram, name:String)(implicit gl:WebGLRenderingContext):Int = { 
    val id = nextId()
    uniforms += id -> gl.getUniformLocation(program,name)
    id 
  }

  // def deleteUniformLocation(id:Int)(implicit gl:WebGLRenderingContext) = {
  //   gl.deleteUniformLocation(id)
  //   uniforms -= id
  // }
}


class GLES20WebGLImpl(gl:WebGLRenderingContext) extends GLES20 {
  import TextureUtil._
  import BufferUtil._
  import ShaderUtil._
  import ProgramUtil._
  import UniformLocationUtil._

  implicit val g = gl

  def glActiveTexture (texture:Int):Unit = gl.activeTexture(texture)

  def glBindTexture (target:Int, texture:Int):Unit = gl.bindTexture(target, texture)

  def glBlendFunc (sfactor:Int, dfactor:Int):Unit = gl.blendFunc(sfactor, dfactor)

  def glClear (mask:Int):Unit = gl.clear(mask)

  def glClearColor (red:Float, green:Float, blue:Float, alpha:Float):Unit = gl.clearColor(red,green,blue,alpha)

  def glClearDepthf (depth:Float):Unit = gl.clearDepth(depth) 

  def glClearStencil (s:Int):Unit = gl.clearStencil(s)

  def glColorMask (red:Boolean, green:Boolean, blue:Boolean, alpha:Boolean):Unit = gl.colorMask(red,green,blue,alpha)

  def glCompressedTexImage2D (target:Int, level:Int, internalformat:Int, width:Int, height:Int, border:Int, imageSize:Int, data:Buffer):Unit = {println("error not implemented!")}

  def glCompressedTexSubImage2D (target:Int, level:Int, xoffset:Int, yoffset:Int, width:Int, height:Int, format:Int, imageSize:Int, data:Buffer):Unit = {println("error not implemented!")}

  def glCopyTexImage2D (target:Int, level:Int, internalformat:Int, x:Int, y:Int, width:Int, height:Int, border:Int):Unit = {println("error not implemented!")}

  def glCopyTexSubImage2D (target:Int, level:Int, xoffset:Int, yoffset:Int, x:Int, y:Int, width:Int, height:Int):Unit = {println("error not implemented!")}

  def glCullFace (mode:Int):Unit = {println("error not implemented!")}

  def glDeleteTextures (n:Int, textures:IntBuffer):Unit = {
    for(i <- 0 until n)
      glDeleteTexture(textures.get(i))
  }
  
  def glDeleteTexture(texture:Int):Unit = TextureUtil.deleteTexture(texture)
  

  def glDepthFunc (func:Int):Unit = {println("error not implemented!")}

  def glDepthMask (flag:Boolean):Unit = {println("error not implemented!")}

  def glDepthRangef (zNear:Float, zFar:Float):Unit = {println("error not implemented!")}

  def glDisable (cap:Int):Unit = gl.disable(cap)

  def glDrawArrays (mode:Int, first:Int, count:Int):Unit = gl.drawArrays(mode, first, count)

  def glDrawElements (mode:Int, count:Int, `type`:Int, indices:Buffer):Unit = {println("error not implemented!")}

  def glEnable (cap:Int):Unit = gl.enable(cap)

  def glFinish ():Unit = {println("error not implemented!")}

  def glFlush ():Unit = {println("error not implemented!")}

  def glFrontFace (mode:Int):Unit = {println("error not implemented!")}

  def glGenTextures (n:Int, textures:IntBuffer):Unit = {
    for(i <- 0 until n){
      val id = glGenTexture()
      textures.put(i, id)
    }
  }
  
  def glGenTexture ():Int = TextureUtil.createTexture()
  

  def glGetError ():Int = 0

  def glGetIntegerv (pname:Int, params:IntBuffer):Unit = {println("error not implemented!")}

  def glGetString (name:Int):String = ""

  def glHint (target:Int, mode:Int):Unit = {println("error not implemented!")}

  def glLineWidth (width:Float):Unit = {println("error not implemented!")}

  def glPixelStorei (pname:Int, param:Int):Unit = {println("error not implemented!")}

  def glPolygonOffset (factor:Float, units:Float):Unit = {println("error not implemented!")}

  def glReadPixels (x:Int, y:Int, width:Int, height:Int, format:Int, `type`:Int, pixels:Buffer):Unit = gl.readPixels(x,y,width, height, format, `type`, pixels.typedArray())

  def glScissor (x:Int, y:Int, width:Int, height:Int):Unit = gl.scissor(x,y,width,height)

  def glStencilFunc (func:Int, ref:Int, mask:Int):Unit = {println("error not implemented!")}

  def glStencilMask (mask:Int):Unit = {println("error not implemented!")}

  def glStencilOp (fail:Int, zfail:Int, zpass:Int):Unit = {println("error not implemented!")}

  def glTexImage2D (target:Int, level:Int, internalformat:Int, width:Int, height:Int, border:Int, format:Int, `type`:Int, pixels:Buffer):Unit = gl.texImage2D(target, level, internalformat, width, height, border, format, `type`, pixels.typedArray())

  def glTexParameterf (target:Int, pname:Int, param:Float):Unit = gl.texParameterf(target,pname,param)

  def glTexSubImage2D (target:Int, level:Int, xoffset:Int, yoffset:Int, width:Int, height:Int, format:Int, `type`:Int, pixels:Buffer):Unit = {println("error not implemented!")}

  def glViewport (x:Int, y:Int, width:Int, height:Int):Unit = {println("error not implemented!")}

  def glAttachShader (program:Int, shader:Int):Unit = gl.attachShader(program, shader)

  def glBindAttribLocation (program:Int, index:Int, name:String):Unit = {println("error not implemented!")}

  def glBindBuffer (target:Int, buffer:Int):Unit = gl.bindBuffer(target,buffer)

  def glBindFramebuffer (target:Int, framebuffer:Int):Unit = {println("error not implemented!")}

  def glBindRenderbuffer (target:Int, renderbuffer:Int):Unit = {println("error not implemented!")}

  def glBlendColor (red:Float, green:Float, blue:Float, alpha:Float):Unit = {println("error not implemented!")}

  def glBlendEquation (mode:Int):Unit = {println("error not implemented!")}

  def glBlendEquationSeparate (modeRGB:Int, modeAlpha:Int):Unit = {println("error not implemented!")}

  def glBlendFuncSeparate (srcRGB:Int, dstRGB:Int, srcAlpha:Int, dstAlpha:Int):Unit = {println("error not implemented!")}

  // def glBufferData (target:Int, data: typedarray.ArrayBuffer, usage:Int):Unit = gl.bufferData(target, data, usage)
  def glBufferData (target:Int, size:Int, data:Buffer, usage:Int):Unit = gl.bufferData(target, data.arrayBuffer(), usage)

  def glBufferSubData (target:Int, offset:Int, size:Int, data:Buffer):Unit = {println("error not implemented!")}

  def glCheckFramebufferStatus (target:Int):Int = 0

  def glCompileShader (shader:Int):Unit = gl.compileShader(shader)

  def glCreateProgram ():Int = ProgramUtil.createProgram()

  def glCreateShader (`type`:Int):Int = ShaderUtil.createShader(`type`)

  def glDeleteBuffer (buffer:Int):Unit = {println("error not implemented!")}

  def glDeleteBuffers (n:Int, buffers:IntBuffer):Unit = {println("error not implemented!")}

  def glDeleteFramebuffer (framebuffer:Int):Unit = {println("error not implemented!")}
  
  def glDeleteFramebuffers (n:Int, framebuffers:IntBuffer):Unit = {println("error not implemented!")}

  def glDeleteProgram (program:Int):Unit = ProgramUtil.deleteProgram(program)

  def glDeleteRenderbuffer (renderbuffer:Int):Unit = {println("error not implemented!")}
  
  def glDeleteRenderbuffers (n:Int, renderbuffers:IntBuffer):Unit = {println("error not implemented!")}

  def glDeleteShader (shader:Int):Unit = ShaderUtil.deleteShader(shader)

  def glDetachShader (program:Int, shader:Int):Unit = gl.detachShader(program, shader)

  def glDisableVertexAttribArray (index:Int):Unit = gl.disableVertexAttribArray(index)

  def glDrawElements (mode:Int, count:Int, `type`:Int, indices:Int):Unit = {println("error not implemented!")}

  def glEnableVertexAttribArray (index:Int):Unit = gl.enableVertexAttribArray(index)

  def glFramebufferRenderbuffer (target:Int, attachment:Int, renderbuffertarget:Int, renderbuffer:Int):Unit = {println("error not implemented!")}

  def glFramebufferTexture2D (target:Int, attachment:Int, textarget:Int, texture:Int, level:Int):Unit = {println("error not implemented!")}
  
  def glGenBuffer ():Int = BufferUtil.createBuffer()

  def glGenBuffers (n:Int, buffers:IntBuffer):Unit = {
    for(i <- 0 until n){
      val id = glGenBuffer()
      buffers.put(i, id)
    }
  }

  def glGenerateMipmap (target:Int):Unit = gl.generateMipmap(target)

  def glGenFramebuffer ():Int = 0

  def glGenFramebuffers (n:Int, framebuffers:IntBuffer):Unit = {println("error not implemented!")}

  def glGenRenderbuffer ():Int = 0

  def glGenRenderbuffers (n:Int, renderbuffers:IntBuffer):Unit = {println("error not implemented!")}

  // deviates
  def glGetActiveAttrib (program:Int, index:Int, size:IntBuffer, `type`:Buffer):String = ""

  // deviates
  def glGetActiveUniform (program:Int, index:Int, size:IntBuffer, `type`:Buffer):String = ""

  def glGetAttachedShaders (program:Int, maxcount:Int, count:Buffer, shaders:IntBuffer):Unit = {println("error not implemented!")}

  def glGetAttribLocation (program:Int, name:String):Int = gl.getAttribLocation(program, name)

  def glGetBooleanv (pname:Int, params:Buffer):Unit = {println("error not implemented!")}

  def glGetBufferParameteriv (target:Int, pname:Int, params:IntBuffer):Unit = {println("error not implemented!")}

  def glGetFloatv (pname:Int, params:FloatBuffer):Unit = {println("error not implemented!")}

  def glGetFramebufferAttachmentParameteriv (target:Int, attachment:Int, pname:Int, params:IntBuffer):Unit = {println("error not implemented!")}

  def glGetProgramiv (program:Int, pname:Int, params:IntBuffer):Unit = {println("error not implemented!")}

  // deviates
  def glGetProgramInfoLog (program:Int):String = ""

  def glGetRenderbufferParameteriv (target:Int, pname:Int, params:IntBuffer):Unit = {println("error not implemented!")}

  def glGetShaderiv (shader:Int, pname:Int, params:IntBuffer):Unit = {println("error not implemented!")}

  // deviates
  def glGetShaderInfoLog (shader:Int):String = ""

  def glGetShaderPrecisionFormat (shadertype:Int, precisiontype:Int, range:IntBuffer, precision:IntBuffer):Unit = {println("error not implemented!")}

  def glGetTexParameterfv (target:Int, pname:Int, params:FloatBuffer):Unit = {println("error not implemented!")}

  def glGetTexParameteriv (target:Int, pname:Int, params:IntBuffer):Unit = {println("error not implemented!")}

  def glGetUniformfv (program:Int, location:Int, params:FloatBuffer):Unit = {println("error not implemented!")}

  def glGetUniformiv (program:Int, location:Int, params:IntBuffer):Unit = {println("error not implemented!")}

  def glGetUniformLocation (program:Int, name:String):Int = UniformLocationUtil.getUniformLocation(program, name)

  def glGetVertexAttribfv (index:Int, pname:Int, params:FloatBuffer):Unit = {println("error not implemented!")}

  def glGetVertexAttribiv (index:Int, pname:Int, params:IntBuffer):Unit = {println("error not implemented!")}

  def glGetVertexAttribPointerv (index:Int, pname:Int, pointer:Buffer):Unit = {println("error not implemented!")}

  def glIsBuffer (buffer:Int):Boolean = false

  def glIsEnabled (cap:Int):Boolean = false

  def glIsFramebuffer (framebuffer:Int):Boolean = false

  def glIsProgram (program:Int):Boolean = false

  def glIsRenderbuffer (renderbuffer:Int):Boolean = false

  def glIsShader (shader:Int):Boolean = false

  def glIsTexture (texture:Int):Boolean = false

  def glLinkProgram (program:Int):Unit = gl.linkProgram(program)

  def glReleaseShaderCompiler ():Unit = {println("error not implemented!")}

  def glRenderbufferStorage (target:Int, internalformat:Int, width:Int, height:Int):Unit = {println("error not implemented!")}

  def glSampleCoverage (value:Float, invert:Boolean):Unit = {println("error not implemented!")}

  def glShaderBinary (n:Int, shaders:IntBuffer, binaryformat:Int, binary:Buffer, length:Int):Unit = {println("error not implemented!")}

  // Deviates
  def glShaderSource (shader:Int, string:String):Unit = gl.shaderSource(shader, string)

  def glStencilFuncSeparate (face:Int, func:Int, ref:Int, mask:Int):Unit = {println("error not implemented!")}

  def glStencilMaskSeparate (face:Int, mask:Int):Unit = {println("error not implemented!")}

  def glStencilOpSeparate (face:Int, fail:Int, zfail:Int, zpass:Int):Unit = {println("error not implemented!")}

  def glTexParameterfv (target:Int, pname:Int, params:FloatBuffer):Unit = {println("error not implemented!")}

  def glTexParameteri (target:Int, pname:Int, param:Int):Unit = gl.texParameteri(target,pname,param)

  def glTexParameteriv (target:Int, pname:Int, params:IntBuffer):Unit = {println("error not implemented!")}

  def glUniform1f (location:Int, x:Float):Unit = {println("error not implemented!")}

  def glUniform1fv (location:Int, count:Int, v:FloatBuffer):Unit = {println("error not implemented!")}
  
  def glUniform1fv (location:Int, count:Int, v:Array[Float], offset:Int):Unit = {println("error not implemented!")}

  def glUniform1i (location:Int, x:Int):Unit = {println("error not implemented!")}

  def glUniform1iv (location:Int, count:Int, v:IntBuffer):Unit = {println("error not implemented!")}
  
  def glUniform1iv (location:Int, count:Int, v:Array[Int], offset:Int):Unit = {println("error not implemented!")}

  def glUniform2f (location:Int, x:Float, y:Float):Unit = {println("error not implemented!")}

  def glUniform2fv (location:Int, count:Int, v:FloatBuffer):Unit = {println("error not implemented!")}
  
  def glUniform2fv (location:Int, count:Int, v:Array[Float], offset:Int):Unit = {println("error not implemented!")}

  def glUniform2i (location:Int, x:Int, y:Int):Unit = {println("error not implemented!")}

  def glUniform2iv (location:Int, count:Int, v:IntBuffer):Unit = {println("error not implemented!")}
  
  def glUniform2iv (location:Int, count:Int, v:Array[Int], offset:Int):Unit = {println("error not implemented!")}

  def glUniform3f (location:Int, x:Float, y:Float, z:Float):Unit = {println("error not implemented!")}

  def glUniform3fv (location:Int, count:Int, v:FloatBuffer):Unit = {println("error not implemented!")}
  
  def glUniform3fv (location:Int, count:Int, v:Array[Float], offset:Int):Unit = {println("error not implemented!")}

  def glUniform3i (location:Int, x:Int, y:Int, z:Int):Unit = {println("error not implemented!")}

  def glUniform3iv (location:Int, count:Int, v:IntBuffer):Unit = {println("error not implemented!")}
  
  def glUniform3iv (location:Int, count:Int, v:Array[Int], offset:Int):Unit = {println("error not implemented!")}

  def glUniform4f (location:Int, x:Float, y:Float, z:Float, w:Float):Unit = {println("error not implemented!")}

  def glUniform4fv (location:Int, count:Int, v:FloatBuffer):Unit = gl.uniform4fv(location, new Float32Array(v.arrayBuffer()))
  
  def glUniform4fv (location:Int, count:Int, v:Array[Float], offset:Int):Unit = {println("error not implemented!")}

  def glUniform4i (location:Int, x:Int, y:Int, z:Int, w:Int):Unit = {println("error not implemented!")}

  def glUniform4iv (location:Int, count:Int, v:IntBuffer):Unit = {println("error not implemented!")}
  
  def glUniform4iv (location:Int, count:Int, v:Array[Int], offset:Int):Unit = {println("error not implemented!")}

  def glUniformMatrix2fv (location:Int, count:Int, transpose:Boolean, value:FloatBuffer):Unit = {println("error not implemented!")}
  
  def glUniformMatrix2fv (location:Int, count:Int, transpose:Boolean, value:Array[Float], offset:Int):Unit = {println("error not implemented!")}

  def glUniformMatrix3fv (location:Int, count:Int, transpose:Boolean, value:FloatBuffer):Unit = {println("error not implemented!")}
  
  def glUniformMatrix3fv (location:Int, count:Int, transpose:Boolean, value:Array[Float], offset:Int):Unit = {println("error not implemented!")}

  def glUniformMatrix4fv (location:Int, count:Int, transpose:Boolean, value:FloatBuffer):Unit = {println("error not implemented!")}
  
  def glUniformMatrix4fv (location:Int, count:Int, transpose:Boolean, value:Array[Float], offset:Int):Unit = {println("error not implemented!")}

  def glUseProgram (program:Int):Unit = gl.useProgram(program)

  def glValidateProgram (program:Int):Unit = {println("error not implemented!")}

  def glVertexAttrib1f (indx:Int, x:Float):Unit = {println("error not implemented!")}

  def glVertexAttrib1fv (indx:Int, values:FloatBuffer):Unit = {println("error not implemented!")}

  def glVertexAttrib2f (indx:Int, x:Float, y:Float):Unit = {println("error not implemented!")}

  def glVertexAttrib2fv (indx:Int, values:FloatBuffer):Unit = {println("error not implemented!")}

  def glVertexAttrib3f (indx:Int, x:Float, y:Float, z:Float):Unit = {println("error not implemented!")}

  def glVertexAttrib3fv (indx:Int, values:FloatBuffer):Unit = {println("error not implemented!")}

  def glVertexAttrib4f (indx:Int, x:Float, y:Float, z:Float, w:Float):Unit = {println("error not implemented!")}

  def glVertexAttrib4fv (indx:Int, values:FloatBuffer):Unit = {println("error not implemented!")}

  /**
   * In OpenGl core profiles (3.1+), passing a pointer to client memory is not valid.
   * In 3.0 and later, use the other version of this function instead, pass a zero-based
   * offset which references the buffer currently bound to GL_ARRAY_BUFFER.
   */
  def glVertexAttribPointer (indx:Int, size:Int, `type`:Int, normalized:Boolean, stride:Int, ptr:Buffer):Unit = {println("error not implemented!")}

  def glVertexAttribPointer (indx:Int, size:Int, `type`:Int, normalized:Boolean, stride:Int, ptr:Int):Unit = gl.vertexAttribPointer(indx,size,`type`,normalized,stride,ptr)

}

