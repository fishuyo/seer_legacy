package seer.graphics
package backend.webgl

import java.nio.Buffer
import java.nio.FloatBuffer 
import java.nio.IntBuffer

import org.scalajs.dom.raw._
// import typings.webgl2._ 
import typings.std.{WebGLVertexArrayObject, WebGL2RenderingContext}
// import WebGL2RenderingContext._

import collection.mutable.HashMap

import scala.scalajs.js.typedarray.TypedArrayBufferOps._

object VertexArrayUtil {
  val buffers = HashMap[Int,WebGLVertexArrayObject]()
  implicit def int2buffer(id:Int) = buffers(id) 
  
  def apply(id:Int) = buffers(id)
  def nextId():Int = {
    for(i <- 0 until buffers.size) 
      if(!buffers.contains(i)) return i
    return buffers.size
  }

  def createVertexArray()(implicit gl:WebGL2RenderingContext):Int = { 
    val id = nextId()
    buffers += id -> gl.createVertexArray().asInstanceOf[WebGLVertexArrayObject]
    id 
  }

  def deleteVertexArray(id:Int)(implicit gl:WebGL2RenderingContext) = {
    gl.deleteVertexArray(id)
    buffers -= id
  }
}

class GLES30WebGLImpl(webgl:WebGLRenderingContext) extends GLES20WebGLImpl(webgl) with GLES30 {
  
  // C function void glReadBuffer ( GLenum mode )

  def glReadBuffer (mode:Int): Unit = {} //Gdx.gl30.glReadBuffer(mode)

  // C function void glDrawRangeElements ( GLenum mode, GLuint start, GLuint end, GLsizei count, GLenum `type`, const GLvoid
// *indices )

  def glDrawRangeElements (mode:Int, start:Int, end:Int, count:Int, `type`:Int, indices:java.nio.Buffer): Unit = {}

  // C function void glDrawRangeElements ( GLenum mode, GLuint start, GLuint end, GLsizei count, GLenum `type`, GLsizei offset )

  def glDrawRangeElements (mode:Int, start:Int, end:Int, count:Int, `type`:Int, offset:Int): Unit = {}

  // C function void glTexImage3D ( GLenum target, GLint level, GLint internalformat, GLsizei width, GLsizei height, GLsizei
// depth, GLint border, GLenum format, GLenum `type`, const GLvoid *pixels )

  def glTexImage3D (target:Int, level:Int, internalformat:Int, width:Int, height:Int, depth:Int, border:Int, format:Int, `type`:Int, pixels:java.nio.Buffer): Unit = {}

  // C function void glTexImage3D ( GLenum target, GLint level, GLint internalformat, GLsizei width, GLsizei height, GLsizei
// depth, GLint border, GLenum format, GLenum `type`, GLsizei offset )

  def glTexImage3D (target:Int, level:Int, internalformat:Int, width:Int, height:Int, depth:Int, border:Int, format:Int, `type`:Int, offset:Int): Unit = {}

  // C function void glTexSubImage3D ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint zoffset, GLsizei width,
// GLsizei height, GLsizei depth, GLenum format, GLenum `type`, const GLvoid *pixels )

  def glTexSubImage3D (target:Int, level:Int, xoffset:Int, yoffset:Int, zoffset:Int, width:Int, height:Int, depth:Int, format:Int, `type`:Int, pixels:java.nio.Buffer): Unit = {}

  // C function void glTexSubImage3D ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint zoffset, GLsizei width,
// GLsizei height, GLsizei depth, GLenum format, GLenum `type`, GLsizei offset )

  def glTexSubImage3D (target:Int, level:Int, xoffset:Int, yoffset:Int, zoffset:Int, width:Int, height:Int, depth:Int, format:Int, `type`:Int, offset:Int): Unit = {}

  // C function void glCopyTexSubImage3D ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint zoffset, GLint x,
// GLint y, GLsizei width, GLsizei height )

  def glCopyTexSubImage3D (target:Int, level:Int, xoffset:Int, yoffset:Int, zoffset:Int, x:Int, y:Int, width:Int, height:Int): Unit = {}

// // C function void glCompressedTexImage3D ( GLenum target, GLint level, GLenum internalformat, GLsizei width, GLsizei height,
// GLsizei depth, GLint border, GLsizei imageSize, const GLvoid *data )
//
// def glCompressedTexImage3D: Unit = {}
// target:Int,
// level:Int,
// internalformat:Int,
// width:Int,
// height:Int,
// depth:Int,
// border:Int,
// imageSize:Int,
// data:java.nio.Buffer
// );
//
// // C function void glCompressedTexImage3D ( GLenum target, GLint level, GLenum internalformat, GLsizei width, GLsizei height,
// GLsizei depth, GLint border, GLsizei imageSize, GLsizei offset )
//
// def glCompressedTexImage3D: Unit = {}
// target:Int,
// level:Int,
// internalformat:Int,
// width:Int,
// height:Int,
// depth:Int,
// border:Int,
// imageSize:Int,
// offset:Int
// );
//
// // C function void glCompressedTexSubImage3D ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint zoffset, GLsizei
// width, GLsizei height, GLsizei depth, GLenum format, GLsizei imageSize, const GLvoid *data )
//
// def glCompressedTexSubImage3D: Unit = {}
// target:Int,
// level:Int,
// xoffset:Int,
// yoffset:Int,
// zoffset:Int,
// width:Int,
// height:Int,
// depth:Int,
// format:Int,
// imageSize:Int,
// data:java.nio.Buffer
// );
//
// // C function void glCompressedTexSubImage3D ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint zoffset, GLsizei
// width, GLsizei height, GLsizei depth, GLenum format, GLsizei imageSize, GLsizei offset )
//
// def glCompressedTexSubImage3D: Unit = {}
// target:Int,
// level:Int,
// xoffset:Int,
// yoffset:Int,
// zoffset:Int,
// width:Int,
// height:Int,
// depth:Int,
// format:Int,
// imageSize:Int,
// offset:Int
// );

  // C function void glGenQueries ( GLsizei n, GLuint *ids )

  def glGenQueries (n:Int, ids:Array[Int], offset:Int): Unit = {}

  // C function void glGenQueries ( GLsizei n, GLuint *ids )

  def glGenQueries (n:Int, ids:java.nio.IntBuffer): Unit = {}

  // C function void glDeleteQueries ( GLsizei n, const GLuint *ids )

  def glDeleteQueries (n:Int, ids:Array[Int], offset:Int): Unit = {}

  // C function void glDeleteQueries ( GLsizei n, const GLuint *ids )

  def glDeleteQueries (n:Int, ids:java.nio.IntBuffer): Unit = {}

  // C function GLboolean glIsQuery ( GLuint id )

  def glIsQuery (id:Int): Boolean = false

  // C function void glBeginQuery ( GLenum target, GLuint id )

  def glBeginQuery (target:Int, id:Int): Unit = {}

  // C function void glEndQuery ( GLenum target )

  def glEndQuery (target:Int): Unit = {}

// // C function void glGetQueryiv ( GLenum target, GLenum pname, GLint *params )
//
// def glGetQueryiv: Unit = {}
// target:Int,
// pname:Int,
// params:Array[Int],
// offset:Int
// );

  // C function void glGetQueryiv ( GLenum target, GLenum pname, GLint *params )

  def glGetQueryiv (target:Int, pname:Int, params:java.nio.IntBuffer): Unit = {}

// // C function void glGetQueryObjectuiv ( GLuint id, GLenum pname, GLuint *params )
//
// def glGetQueryObjectuiv: Unit = {}
// id:Int,
// pname:Int,
// params:Array[Int],
// offset:Int
// );

  // C function void glGetQueryObjectuiv ( GLuint id, GLenum pname, GLuint *params )

  def glGetQueryObjectuiv (id:Int, pname:Int, params:java.nio.IntBuffer): Unit = {}

  // C function GLboolean glUnmapBuffer ( GLenum target )

  def glUnmapBuffer (target:Int): Boolean = false

  // C function void glGetBufferPointerv ( GLenum target, GLenum pname, GLvoid** params )

  def glGetBufferPointerv (target:Int, pname:Int): java.nio.Buffer = java.nio.FloatBuffer.allocate(1)

// // C function void glDrawBuffers ( GLsizei n, const GLenum *bufs )
//
// def glDrawBuffers: Unit = {}
// n:Int,
// bufs:Array[Int],
// offset:Int
// );

  // C function void glDrawBuffers ( GLsizei n, const GLenum *bufs )

  def glDrawBuffers (n:Int, bufs:java.nio.IntBuffer): Unit = {}

// // C function void glUniformMatrix2x3fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )
//
// def glUniformMatrix2x3fv: Unit = {}
// location:Int,
// count:Int,
// transpose:Boolean,
// ]:Float value,
// offset:Int
// );

  // C function void glUniformMatrix2x3fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

  def glUniformMatrix2x3fv (location:Int, count:Int, transpose:Boolean, value:java.nio.FloatBuffer): Unit = {}

// // C function void glUniformMatrix3x2fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )
//
// def glUniformMatrix3x2fv: Unit = {}
// location:Int,
// count:Int,
// transpose:Boolean,
// ]:Float value,
// offset:Int
// );

  // C function void glUniformMatrix3x2fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

  def glUniformMatrix3x2fv (location:Int, count:Int, transpose:Boolean, value:java.nio.FloatBuffer): Unit = {}

// // C function void glUniformMatrix2x4fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )
//
// def glUniformMatrix2x4fv: Unit = {}
// location:Int,
// count:Int,
// transpose:Boolean,
// ]:Float value,
// offset:Int
// );

  // C function void glUniformMatrix2x4fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

  def glUniformMatrix2x4fv (location:Int, count:Int, transpose:Boolean, value:java.nio.FloatBuffer): Unit = {}

// // C function void glUniformMatrix4x2fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )
//
// def glUniformMatrix4x2fv: Unit = {}
// location:Int,
// count:Int,
// transpose:Boolean,
// ]:Float value,
// offset:Int
// );

  // C function void glUniformMatrix4x2fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

  def glUniformMatrix4x2fv (location:Int, count:Int, transpose:Boolean, value:java.nio.FloatBuffer): Unit = {}

// // C function void glUniformMatrix3x4fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )
//
// def glUniformMatrix3x4fv: Unit = {}
// location:Int,
// count:Int,
// transpose:Boolean,
// ]:Float value,
// offset:Int
// );

  // C function void glUniformMatrix3x4fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

  def glUniformMatrix3x4fv (location:Int, count:Int, transpose:Boolean, value:java.nio.FloatBuffer): Unit = {}

// // C function void glUniformMatrix4x3fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )
//
// def glUniformMatrix4x3fv: Unit = {}
// location:Int,
// count:Int,
// transpose:Boolean,
// ]:Float value,
// offset:Int
// );

  // C function void glUniformMatrix4x3fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

  def glUniformMatrix4x3fv (location:Int, count:Int, transpose:Boolean, value:java.nio.FloatBuffer): Unit = {}

  // C function void glBlitFramebuffer ( GLint srcX0, GLint srcY0, GLint srcX1, GLint srcY1, GLint dstX0, GLint dstY0, GLint
// dstX1, GLint dstY1, GLbitfield mask, GLenum filter )

  def glBlitFramebuffer (srcX0:Int, srcY0:Int, srcX1:Int, srcY1:Int, dstX0:Int, dstY0:Int, dstX1:Int, dstY1:Int, mask:Int, filter:Int): Unit = {}

  // C function void glRenderbufferStorageMultisample ( GLenum target, GLsizei samples, GLenum internalformat, GLsizei width,
// GLsizei height )

  def glRenderbufferStorageMultisample (target:Int, samples:Int, internalformat:Int, width:Int, height:Int): Unit = {}

  // C function void glFramebufferTextureLayer ( GLenum target, GLenum attachment, GLuint texture, GLint level, GLint layer )

  def glFramebufferTextureLayer (target:Int, attachment:Int, texture:Int, level:Int, layer:Int): Unit = {}

// // C function GLvoid * glMapBufferRange ( GLenum target, GLintptr offset, GLsizeiptr length, GLbitfield access )
//
// public glMapBufferRange:java.nio.Buffer(
// target:Int,
// offset:Int,
// length:Int,
// access:Int
// );

  // C function void glFlushMappedBufferRange ( GLenum target, GLintptr offset, GLsizeiptr length )

  def glFlushMappedBufferRange (target:Int, offset:Int, length:Int): Unit = {}

  // C function void glBindVertexArray ( GLuint array )

  def glBindVertexArray (array:Int): Unit = {}

  // C function void glDeleteVertexArrays ( GLsizei n, const GLuint *arrays )

  def glDeleteVertexArrays (n:Int, arrays:Array[Int], offset:Int): Unit = {}

  // C function void glDeleteVertexArrays ( GLsizei n, const GLuint *arrays )

  def glDeleteVertexArrays (n:Int, arrays:java.nio.IntBuffer): Unit = {}

  // C function void glGenVertexArrays ( GLsizei n, GLuint *arrays )

  def glGenVertexArrays (n:Int, arrays:Array[Int], offset:Int): Unit = {}

  // C function void glGenVertexArrays ( GLsizei n, GLuint *arrays )

  def glGenVertexArrays (n:Int, arrays:java.nio.IntBuffer): Unit = {}

  // C function GLboolean glIsVertexArray ( GLuint array )

  def glIsVertexArray (array:Int): Boolean = false

//
// // C function void glGetIntegeri_v ( GLenum target, GLuint index, GLint *data )
//
// def glGetIntegeri_v: Unit = {}
// target:Int,
// index:Int,
// data:Array[Int],
// offset:Int
// );
//
// // C function void glGetIntegeri_v ( GLenum target, GLuint index, GLint *data )
//
// def glGetIntegeri_v: Unit = {}
// target:Int,
// index:Int,
// data:java.nio.IntBuffer
// );

  // C function void glBeginTransformFeedback ( GLenum primitiveMode )

  def glBeginTransformFeedback (primitiveMode:Int): Unit = {}

  // C function void glEndTransformFeedback ( void )

  def glEndTransformFeedback (): Unit = {}

  // C function void glBindBufferRange ( GLenum target, GLuint index, GLuint buffer, GLintptr offset, GLsizeiptr size )

  def glBindBufferRange (target:Int, index:Int, buffer:Int, offset:Int, size:Int): Unit = {}

  // C function void glBindBufferBase ( GLenum target, GLuint index, GLuint buffer )

  def glBindBufferBase (target:Int, index:Int, buffer:Int): Unit = {}

  // C function void glTransformFeedbackVaryings ( GLuint program, GLsizei count, const GLchar *varyings, GLenum bufferMode )

  def glTransformFeedbackVaryings (program:Int, varyings:Array[String], bufferMode:Int): Unit = {}

// // C function void glGetTransformFeedbackVarying ( GLuint program, GLuint index, GLsizei bufSize, GLsizei *length, GLint *size,
// GLenum *`type`, GLchar *name )
//
// def glGetTransformFeedbackVarying: Unit = {}
// program:Int,
// index:Int,
// bufsize:Int,
// length:Array[Int],
// lengthOffset:Int,
// size:Array[Int],
// sizeOffset:Int,
// `type`:Array[Int],
// typeOffset:Int,
// byte[] name,
// nameOffset:Int
// );
//
// // C function void glGetTransformFeedbackVarying ( GLuint program, GLuint index, GLsizei bufSize, GLsizei *length, GLint *size,
// GLenum *`type`, GLchar *name )
//
// def glGetTransformFeedbackVarying: Unit = {}
// program:Int,
// index:Int,
// bufsize:Int,
// length:java.nio.IntBuffer,
// size:java.nio.IntBuffer,
// `type`:java.nio.IntBuffer,
// byte name
// );
//
// // C function void glGetTransformFeedbackVarying ( GLuint program, GLuint index, GLsizei bufSize, GLsizei *length, GLint *size,
// GLenum *`type`, GLchar *name )
//
// def glGetTransformFeedbackVarying: String
// program:Int,
// index:Int,
// size:Array[Int],
// sizeOffset:Int,
// `type`:Array[Int],
// typeOffset:Int
// );
//
// // C function void glGetTransformFeedbackVarying ( GLuint program, GLuint index, GLsizei bufSize, GLsizei *length, GLint *size,
// GLenum *`type`, GLchar *name )
//
// def glGetTransformFeedbackVarying: String
// program:Int,
// index:Int,
// size:java.nio.IntBuffer,
// `type`:java.nio.IntBuffer
// );

  // C function void glVertexAttribIPointer ( GLuint index, GLint size, GLenum `type`, GLsizei stride, GLsizei offset )

  def glVertexAttribIPointer (index:Int, size:Int, `type`:Int, stride:Int, offset:Int): Unit = {}

// // C function void glGetVertexAttribIiv ( GLuint index, GLenum pname, GLint *params )
//
// def glGetVertexAttribIiv: Unit = {}
// index:Int,
// pname:Int,
// params:Array[Int],
// offset:Int
// );

  // C function void glGetVertexAttribIiv ( GLuint index, GLenum pname, GLint *params )

  def glGetVertexAttribIiv (index:Int, pname:Int, params:java.nio.IntBuffer): Unit = {}

// // C function void glGetVertexAttribIuiv ( GLuint index, GLenum pname, GLuint *params )
//
// def glGetVertexAttribIuiv: Unit = {}
// index:Int,
// pname:Int,
// params:Array[Int],
// offset:Int
// );

  // C function void glGetVertexAttribIuiv ( GLuint index, GLenum pname, GLuint *params )

  def glGetVertexAttribIuiv (index:Int, pname:Int, params:java.nio.IntBuffer): Unit = {}

  // C function void glVertexAttribI4i ( GLuint index, GLint x, GLint y, GLint z, GLint w )

  def glVertexAttribI4i (index:Int, x:Int, y:Int, z:Int, w:Int): Unit = {}

  // C function void glVertexAttribI4ui ( GLuint index, GLuint x, GLuint y, GLuint z, GLuint w )

  def glVertexAttribI4ui (index:Int, x:Int, y:Int, z:Int, w:Int): Unit = {}

// // C function void glVertexAttribI4iv ( GLuint index, const GLint *v )
//
// def glVertexAttribI4iv: Unit = {}
// index:Int,
// v:Array[Int],
// offset:Int
// );
//
// // C function void glVertexAttribI4iv ( GLuint index, const GLint *v )
//
// def glVertexAttribI4iv: Unit = {}
// index:Int,
// v:java.nio.IntBuffer
// );
//
// // C function void glVertexAttribI4uiv ( GLuint index, const GLuint *v )
//
// def glVertexAttribI4uiv: Unit = {}
// index:Int,
// v:Array[Int],
// offset:Int
// );
//
// // C function void glVertexAttribI4uiv ( GLuint index, const GLuint *v )
//
// def glVertexAttribI4uiv: Unit = {}
// index:Int,
// v:java.nio.IntBuffer
// );
//
// // C function void glGetUniformuiv ( GLuint program, GLint location, GLuint *params )
//
// def glGetUniformuiv: Unit = {}
// program:Int,
// location:Int,
// params:Array[Int],
// offset:Int
// );

  // C function void glGetUniformuiv ( GLuint program, GLint location, GLuint *params )

  def glGetUniformuiv (program:Int, location:Int, params:java.nio.IntBuffer): Unit = {}

  // C function GLint glGetFragDataLocation ( GLuint program, const GLchar *name )

  def glGetFragDataLocation (program:Int, name:String): Int = 0

// // C function void glUniform1ui ( GLint location, GLuint v0 )
//
// def glUniform1ui: Unit = {}
// location:Int,
// v0:Int
// );
//
// // C function void glUniform2ui ( GLint location, GLuint v0, GLuint v1 )
//
// def glUniform2ui: Unit = {}
// location:Int,
// v0:Int,
// v1:Int
// );
//
// // C function void glUniform3ui ( GLint location, GLuint v0, GLuint v1, GLuint v2 )
//
// def glUniform3ui: Unit = {}
// location:Int,
// v0:Int,
// v1:Int,
// v2:Int
// );
//
// // C function void glUniform4ui ( GLint location, GLuint v0, GLuint v1, GLuint v2, GLuint v3 )
//
// def glUniform4ui: Unit = {}
// location:Int,
// v0:Int,
// v1:Int,
// v2:Int,
// v3:Int
// );
//
// // C function void glUniform1uiv ( GLint location, GLsizei count, const GLuint *value )
//
// def glUniform1uiv: Unit = {}
// location:Int,
// count:Int,
// value:Array[Int],
// offset:Int
// );

  // C function void glUniform1uiv ( GLint location, GLsizei count, const GLuint *value )

  def glUniform1uiv (location:Int, count:Int, value:java.nio.IntBuffer): Unit = {}

// // C function void glUniform2uiv ( GLint location, GLsizei count, const GLuint *value )
//
// def glUniform2uiv: Unit = {}
// location:Int,
// count:Int,
// value:Array[Int],
// offset:Int
// );
//
// // C function void glUniform2uiv ( GLint location, GLsizei count, const GLuint *value )
//
// def glUniform2uiv: Unit = {}
// location:Int,
// count:Int,
// value:java.nio.IntBuffer
// );
//
// // C function void glUniform3uiv ( GLint location, GLsizei count, const GLuint *value )
//
// def glUniform3uiv: Unit = {}
// location:Int,
// count:Int,
// value:Array[Int],
// offset:Int
// );

  // C function void glUniform3uiv ( GLint location, GLsizei count, const GLuint *value )

  def glUniform3uiv (location:Int, count:Int, value:java.nio.IntBuffer): Unit = {}

// // C function void glUniform4uiv ( GLint location, GLsizei count, const GLuint *value )
//
// def glUniform4uiv: Unit = {}
// location:Int,
// count:Int,
// value:Array[Int],
// offset:Int
// );

  // C function void glUniform4uiv ( GLint location, GLsizei count, const GLuint *value )

  def glUniform4uiv (location:Int, count:Int, value:java.nio.IntBuffer): Unit = {}

// // C function void glClearBufferiv ( GLenum buffer, GLint drawbuffer, const GLint *value )
//
// def glClearBufferiv: Unit = {}
// buffer:Int,
// drawbuffer:Int,
// value:Array[Int],
// offset:Int
// );

  // C function void glClearBufferiv ( GLenum buffer, GLint drawbuffer, const GLint *value )

  def glClearBufferiv (buffer:Int, drawbuffer:Int, value:java.nio.IntBuffer): Unit = {}

// // C function void glClearBufferuiv ( GLenum buffer, GLint drawbuffer, const GLuint *value )
//
// def glClearBufferuiv: Unit = {}
// buffer:Int,
// drawbuffer:Int,
// value:Array[Int],
// offset:Int
// );

  // C function void glClearBufferuiv ( GLenum buffer, GLint drawbuffer, const GLuint *value )

  def glClearBufferuiv (buffer:Int, drawbuffer:Int, value:java.nio.IntBuffer): Unit = {}

// // C function void glClearBufferfv ( GLenum buffer, GLint drawbuffer, const GLfloat *value )
//
// def glClearBufferfv: Unit = {}
// buffer:Int,
// drawbuffer:Int,
// ]:Float value,
// offset:Int
// );

  // C function void glClearBufferfv ( GLenum buffer, GLint drawbuffer, const GLfloat *value )

  def glClearBufferfv (buffer:Int, drawbuffer:Int, value:java.nio.FloatBuffer): Unit = {}

  // C function void glClearBufferfi ( GLenum buffer, GLint drawbuffer, GLfloat depth, GLint stencil )

  def glClearBufferfi (buffer:Int, drawbuffer:Int, depth:Float, stencil:Int): Unit = {}

  // C function const GLubyte * glGetStringi ( GLenum name, GLuint index )

  def glGetStringi (name:Int, index:Int): String = ""

  // C function void glCopyBufferSubData ( GLenum readTarget, GLenum writeTarget, GLintptr readOffset, GLintptr writeOffset,
// GLsizeiptr size )

  def glCopyBufferSubData (readTarget:Int, writeTarget:Int, readOffset:Int, writeOffset:Int, size:Int): Unit = {}

// // C function void glGetUniformIndices ( GLuint program, GLsizei uniformCount, const GLchar *const *uniformNames, GLuint
// *uniformIndices )
//
// def glGetUniformIndices: Unit = {}
// program:Int,
// uniformNames:Array[String],
// uniformIndices:Array[Int],
// uniformIndicesOffset:Int
// );

  // C function void glGetUniformIndices ( GLuint program, GLsizei uniformCount, const GLchar *const *uniformNames, GLuint
// *uniformIndices )

  def glGetUniformIndices (program:Int, uniformNames:Array[String], uniformIndices:java.nio.IntBuffer): Unit = {}

// // C function void glGetActiveUniformsiv ( GLuint program, GLsizei uniformCount, const GLuint *uniformIndices, GLenum pname,
// GLint *params )
//
// def glGetActiveUniformsiv: Unit = {}
// program:Int,
// uniformCount:Int,
// uniformIndices:Array[Int],
// uniformIndicesOffset:Int,
// pname:Int,
// params:Array[Int],
// paramsOffset:Int
// );

  // C function void glGetActiveUniformsiv ( GLuint program, GLsizei uniformCount, const GLuint *uniformIndices, GLenum pname,
// GLint *params )

  def glGetActiveUniformsiv (program:Int, uniformCount:Int, uniformIndices:java.nio.IntBuffer, pname:Int, params:java.nio.IntBuffer): Unit = {}

  // C function GLuint glGetUniformBlockIndex ( GLuint program, const GLchar *uniformBlockName )

  def glGetUniformBlockIndex (program:Int, uniformBlockName:String): Int = 0

// // C function void glGetActiveUniformBlockiv ( GLuint program, GLuint uniformBlockIndex, GLenum pname, GLint *params )
//
// def glGetActiveUniformBlockiv: Unit = {}
// program:Int,
// uniformBlockIndex:Int,
// pname:Int,
// params:Array[Int],
// offset:Int
// );

  // C function void glGetActiveUniformBlockiv ( GLuint program, GLuint uniformBlockIndex, GLenum pname, GLint *params )

  def glGetActiveUniformBlockiv (program:Int, uniformBlockIndex:Int, pname:Int, params:java.nio.IntBuffer): Unit = {}

// // C function void glGetActiveUniformBlockName ( GLuint program, GLuint uniformBlockIndex, GLsizei bufSize, GLsizei *length,
// GLchar *uniformBlockName )
//
// def glGetActiveUniformBlockName: Unit = {}
// program:Int,
// uniformBlockIndex:Int,
// bufSize:Int,
// length:Array[Int],
// lengthOffset:Int,
// byte[] uniformBlockName,
// uniformBlockNameOffset:Int
// );

  // C function void glGetActiveUniformBlockName ( GLuint program, GLuint uniformBlockIndex, GLsizei bufSize, GLsizei *length,
// GLchar *uniformBlockName )

  def glGetActiveUniformBlockName (program:Int, uniformBlockIndex:Int, length:java.nio.Buffer, uniformBlockName:java.nio.Buffer): Unit = {}

  // C function void glGetActiveUniformBlockName ( GLuint program, GLuint uniformBlockIndex, GLsizei bufSize, GLsizei *length,
// GLchar *uniformBlockName )

  def glGetActiveUniformBlockName (program:Int, uniformBlockIndex:Int): String = ""

  // C function void glUniformBlockBinding ( GLuint program, GLuint uniformBlockIndex, GLuint uniformBlockBinding )

  def glUniformBlockBinding (program:Int, uniformBlockIndex:Int, uniformBlockBinding:Int): Unit = {}

  // C function void glDrawArraysInstanced ( GLenum mode, GLint first, GLsizei count, GLsizei instanceCount )

  def glDrawArraysInstanced (mode:Int, first:Int, count:Int, instanceCount:Int): Unit = {}

// // C function void glDrawElementsInstanced ( GLenum mode, GLsizei count, GLenum `type`, const GLvoid *indices, GLsizei
// instanceCount )
//
// def glDrawElementsInstanced: Unit = {}
// mode:Int,
// count:Int,
// `type`:Int,
// indices:java.nio.Buffer,
// instanceCount:Int
// );

  // C function void glDrawElementsInstanced ( GLenum mode, GLsizei count, GLenum `type`, const GLvoid *indices, GLsizei
// instanceCount )

  def glDrawElementsInstanced (mode:Int, count:Int, `type`:Int, indicesOffset:Int, instanceCount:Int): Unit = {}

// // C function GLsync glFenceSync ( GLenum condition, GLbitfield flags )
//
// public long glFenceSync(
// condition:Int,
// flags:Int
// );
//
// // C function GLboolean glIsSync ( GLsync sync )
//
// def glIsSync: Boolean = false
// long sync
// );
//
// // C function void glDeleteSync ( GLsync sync )
//
// def glDeleteSync: Unit = {}
// long sync
// );
//
// // C function GLenum glClientWaitSync ( GLsync sync, GLbitfield flags, GLuint64 timeout )
//
// def glClientWaitSync: Int
// long sync,
// flags:Int,
// long timeout
// );
//
// // C function void glWaitSync ( GLsync sync, GLbitfield flags, GLuint64 timeout )
//
// def glWaitSync: Unit = {}
// long sync,
// flags:Int,
// long timeout
// );

// // C function void glGetInteger64v ( GLenum pname, GLint64 *params )
//
// def glGetInteger64v: Unit = {}
// pname:Int,
// long[] params,
// offset:Int
// );

  // C function void glGetInteger64v ( GLenum pname, GLint64 *params )

  def glGetInteger64v (pname:Int, params:java.nio.LongBuffer): Unit = {}

// // C function void glGetSynciv ( GLsync sync, GLenum pname, GLsizei bufSize, GLsizei *length, GLint *values )
//
// def glGetSynciv: Unit = {}
// long sync,
// pname:Int,
// bufSize:Int,
// length:Array[Int],
// lengthOffset:Int,
// values:Array[Int],
// valuesOffset:Int
// );
//
// // C function void glGetSynciv ( GLsync sync, GLenum pname, GLsizei bufSize, GLsizei *length, GLint *values )
//
// def glGetSynciv: Unit = {}
// long sync,
// pname:Int,
// bufSize:Int,
// length:java.nio.IntBuffer,
// values:java.nio.IntBuffer
// );
//
// // C function void glGetInteger64i_v ( GLenum target, GLuint index, GLint64 *data )
//
// def glGetInteger64i_v: Unit = {}
// target:Int,
// index:Int,
// long[] data,
// offset:Int
// );
//
// // C function void glGetInteger64i_v ( GLenum target, GLuint index, GLint64 *data )
//
// def glGetInteger64i_v: Unit = {}
// target:Int,
// index:Int,
// data:java.nio.LongBuffer
// );
//
// // C function void glGetBufferParameteri64v ( GLenum target, GLenum pname, GLint64 *params )
//
// def glGetBufferParameteri64v: Unit = {}
// target:Int,
// pname:Int,
// long[] params,
// offset:Int
// );

  // C function void glGetBufferParameteri64v ( GLenum target, GLenum pname, GLint64 *params )

  def glGetBufferParameteri64v (target:Int, pname:Int, params:java.nio.LongBuffer): Unit = {}

  // C function void glGenSamplers ( GLsizei count, GLuint *samplers )

  def glGenSamplers (count:Int, samplers:Array[Int], offset:Int): Unit = {}

  // C function void glGenSamplers ( GLsizei count, GLuint *samplers )

  def glGenSamplers (count:Int, samplers:java.nio.IntBuffer): Unit = {}

  // C function void glDeleteSamplers ( GLsizei count, const GLuint *samplers )

  def glDeleteSamplers (count:Int, samplers:Array[Int], offset:Int): Unit = {}

  // C function void glDeleteSamplers ( GLsizei count, const GLuint *samplers )

  def glDeleteSamplers (count:Int, samplers:java.nio.IntBuffer): Unit = {}

  // C function GLboolean glIsSampler ( GLuint sampler )

  def glIsSampler (sampler:Int): Boolean = false

  // C function void glBindSampler ( GLuint unit, GLuint sampler )

  def glBindSampler (unit:Int, sampler:Int): Unit = {}

  // C function void glSamplerParameteri ( GLuint sampler, GLenum pname, GLint param )

  def glSamplerParameteri (sampler:Int, pname:Int, param:Int): Unit = {}

// // C function void glSamplerParameteriv ( GLuint sampler, GLenum pname, const GLint *param )
//
// def glSamplerParameteriv: Unit = {}
// sampler:Int,
// pname:Int,
// param:Array[Int],
// offset:Int
// );

  // C function void glSamplerParameteriv ( GLuint sampler, GLenum pname, const GLint *param )

  def glSamplerParameteriv (sampler:Int, pname:Int, param:java.nio.IntBuffer): Unit = {}

  // C function void glSamplerParameterf ( GLuint sampler, GLenum pname, GLfloat param )

  def glSamplerParameterf (sampler:Int, pname:Int, param:Float): Unit = {}

// // C function void glSamplerParameterfv ( GLuint sampler, GLenum pname, const GLfloat *param )
//
// def glSamplerParameterfv: Unit = {}
// sampler:Int,
// pname:Int,
// ]:Float param,
// offset:Int
// );

  // C function void glSamplerParameterfv ( GLuint sampler, GLenum pname, const GLfloat *param )

  def glSamplerParameterfv (sampler:Int, pname:Int, param:java.nio.FloatBuffer): Unit = {}

// // C function void glGetSamplerParameteriv ( GLuint sampler, GLenum pname, GLint *params )
//
// def glGetSamplerParameteriv: Unit = {}
// sampler:Int,
// pname:Int,
// params:Array[Int],
// offset:Int
// );

  // C function void glGetSamplerParameteriv ( GLuint sampler, GLenum pname, GLint *params )

  def glGetSamplerParameteriv (sampler:Int, pname:Int, params:java.nio.IntBuffer): Unit = {}

// // C function void glGetSamplerParameterfv ( GLuint sampler, GLenum pname, GLfloat *params )
//
// def glGetSamplerParameterfv: Unit = {}
// sampler:Int,
// pname:Int,
// ]:Float params,
// offset:Int
// );

  // C function void glGetSamplerParameterfv ( GLuint sampler, GLenum pname, GLfloat *params )

  def glGetSamplerParameterfv (sampler:Int, pname:Int, params:java.nio.FloatBuffer): Unit = {}

  // C function void glVertexAttribDivisor ( GLuint index, GLuint divisor )

  def glVertexAttribDivisor (index:Int, divisor:Int): Unit = {}

  // C function void glBindTransformFeedback ( GLenum target, GLuint id )

  def glBindTransformFeedback (target:Int, id:Int): Unit = {}

  // C function void glDeleteTransformFeedbacks ( GLsizei n, const GLuint *ids )

  def glDeleteTransformFeedbacks (n:Int, ids:Array[Int], offset:Int): Unit = {}

  // C function void glDeleteTransformFeedbacks ( GLsizei n, const GLuint *ids )

  def glDeleteTransformFeedbacks (n:Int, ids:java.nio.IntBuffer): Unit = {}

  // C function void glGenTransformFeedbacks ( GLsizei n, GLuint *ids )

  def glGenTransformFeedbacks (n:Int, ids:Array[Int], offset:Int): Unit = {}

  // C function void glGenTransformFeedbacks ( GLsizei n, GLuint *ids )

  def glGenTransformFeedbacks (n:Int, ids:java.nio.IntBuffer): Unit = {}

  // C function GLboolean glIsTransformFeedback ( GLuint id )

  def glIsTransformFeedback (id:Int): Boolean = false

  // C function void glPauseTransformFeedback ( void )

  def glPauseTransformFeedback (): Unit = {}

  // C function void glResumeTransformFeedback ( void )

  def glResumeTransformFeedback (): Unit = {}

// // C function void glGetProgramBinary ( GLuint program, GLsizei bufSize, GLsizei *length, GLenum *binaryFormat, GLvoid *binary
// )
//
// def glGetProgramBinary: Unit = {}
// program:Int,
// bufSize:Int,
// length:Array[Int],
// lengthOffset:Int,
// binaryFormat:Array[Int],
// binaryFormatOffset:Int,
// binary:java.nio.Buffer
// );
//
// // C function void glGetProgramBinary ( GLuint program, GLsizei bufSize, GLsizei *length, GLenum *binaryFormat, GLvoid *binary
// )
//
// def glGetProgramBinary: Unit = {}
// program:Int,
// bufSize:Int,
// length:java.nio.IntBuffer,
// binaryFormat:java.nio.IntBuffer,
// binary:java.nio.Buffer
// );
//
// // C function void glProgramBinary ( GLuint program, GLenum binaryFormat, const GLvoid *binary, GLsizei length )
//
// def glProgramBinary: Unit = {}
// program:Int,
// binaryFormat:Int,
// binary:java.nio.Buffer,
// length:Int
// );

  // C function void glProgramParameteri ( GLuint program, GLenum pname, GLint value )

  def glProgramParameteri (program:Int, pname:Int, value:Int): Unit = {}

// // C function void glInvalidateFramebuffer ( GLenum target, GLsizei numAttachments, const GLenum *attachments )
//
// def glInvalidateFramebuffer: Unit = {}
// target:Int,
// numAttachments:Int,
// attachments:Array[Int],
// offset:Int
// );

  // C function void glInvalidateFramebuffer ( GLenum target, GLsizei numAttachments, const GLenum *attachments )

  def glInvalidateFramebuffer (target:Int, numAttachments:Int, attachments:java.nio.IntBuffer): Unit = {}

// // C function void glInvalidateSubFramebuffer ( GLenum target, GLsizei numAttachments, const GLenum *attachments, GLint x,
// GLint y, GLsizei width, GLsizei height )
//
// def glInvalidateSubFramebuffer: Unit = {}
// target:Int,
// numAttachments:Int,
// attachments:Array[Int],
// offset:Int,
// x:Int,
// y:Int,
// width:Int,
// height:Int
// );

  // C function void glInvalidateSubFramebuffer ( GLenum target, GLsizei numAttachments, const GLenum *attachments, GLint x,
// GLint y, GLsizei width, GLsizei height )

  def glInvalidateSubFramebuffer (target:Int, numAttachments:Int, attachments:java.nio.IntBuffer, x:Int, y:Int, width:Int, height:Int): Unit = {}

// // C function void glTexStorage2D ( GLenum target, GLsizei levels, GLenum internalformat, GLsizei width, GLsizei height )
//
// def glTexStorage2D: Unit = {}
// target:Int,
// levels:Int,
// internalformat:Int,
// width:Int,
// height:Int
// );
//
// // C function void glTexStorage3D ( GLenum target, GLsizei levels, GLenum internalformat, GLsizei width, GLsizei height,
// GLsizei depth )
//
// def glTexStorage3D: Unit = {}
// target:Int,
// levels:Int,
// internalformat:Int,
// width:Int,
// height:Int,
// depth:Int
// );
//
// // C function void glGetInternalformativ ( GLenum target, GLenum internalformat, GLenum pname, GLsizei bufSize, GLint *params )
//
// def glGetInternalformativ: Unit = {}
// target:Int,
// internalformat:Int,
// pname:Int,
// bufSize:Int,
// params:Array[Int],
// offset:Int
// );
//
// // C function void glGetInternalformativ ( GLenum target, GLenum internalformat, GLenum pname, GLsizei bufSize, GLint *params )
//
// def glGetInternalformativ: Unit = {}
// target:Int,
// internalformat:Int,
// pname:Int,
// bufSize:Int,
// params:java.nio.IntBuffer
// );

  // @Override
  // @Deprecated
  /**
   * In OpenGl core profiles (3.1+), passing a pointer to client memory is not valid.
   * Use the other version of this function instead, pass a zero-based offset which references
   * the buffer currently bound to GL_ARRAY_BUFFER.
   */
  // void glVertexAttribPointer(indx:Int, size:Int, `type`:Int, normalized:Boolean, stride:Int, Buffer ptr);

}