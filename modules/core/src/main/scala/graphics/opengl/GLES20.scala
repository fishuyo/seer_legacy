/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************

  Modified 2019 fishuyo

 ******************************************************************************/


package seer.graphics

import java.nio.Buffer
import java.nio.FloatBuffer 
import java.nio.IntBuffer

/** Interface wrapping all the methods of OpenGL ES 2.0
 * original @author mzechner */

trait  GLES20 {
  val GL_ES_VERSION_2_0 = 1
  val GL_DEPTH_BUFFER_BIT = 0x00000100
  val GL_STENCIL_BUFFER_BIT = 0x00000400
  val GL_COLOR_BUFFER_BIT = 0x00004000
  val GL_FALSE = 0
  val GL_TRUE = 1
  val GL_POINTS = 0x0000
  val GL_LINES = 0x0001
  val GL_LINE_LOOP = 0x0002
  val GL_LINE_STRIP = 0x0003
  val GL_TRIANGLES = 0x0004
  val GL_TRIANGLE_STRIP = 0x0005
  val GL_TRIANGLE_FAN = 0x0006
  val GL_ZERO = 0
  val GL_ONE = 1
  val GL_SRC_COLOR = 0x0300
  val GL_ONE_MINUS_SRC_COLOR = 0x0301
  val GL_SRC_ALPHA = 0x0302
  val GL_ONE_MINUS_SRC_ALPHA = 0x0303
  val GL_DST_ALPHA = 0x0304
  val GL_ONE_MINUS_DST_ALPHA = 0x0305
  val GL_DST_COLOR = 0x0306
  val GL_ONE_MINUS_DST_COLOR = 0x0307
  val GL_SRC_ALPHA_SATURATE = 0x0308
  val GL_FUNC_ADD = 0x8006
  val GL_BLEND_EQUATION = 0x8009
  val GL_BLEND_EQUATION_RGB = 0x8009
  val GL_BLEND_EQUATION_ALPHA = 0x883D
  val GL_FUNC_SUBTRACT = 0x800A
  val GL_FUNC_REVERSE_SUBTRACT = 0x800B
  val GL_BLEND_DST_RGB = 0x80C8
  val GL_BLEND_SRC_RGB = 0x80C9
  val GL_BLEND_DST_ALPHA = 0x80CA
  val GL_BLEND_SRC_ALPHA = 0x80CB
  val GL_CONSTANT_COLOR = 0x8001
  val GL_ONE_MINUS_CONSTANT_COLOR = 0x8002
  val GL_CONSTANT_ALPHA = 0x8003
  val GL_ONE_MINUS_CONSTANT_ALPHA = 0x8004
  val GL_BLEND_COLOR = 0x8005
  val GL_ARRAY_BUFFER = 0x8892
  val GL_ELEMENT_ARRAY_BUFFER = 0x8893
  val GL_ARRAY_BUFFER_BINDING = 0x8894
  val GL_ELEMENT_ARRAY_BUFFER_BINDING = 0x8895
  val GL_STREAM_DRAW = 0x88E0
  val GL_STATIC_DRAW = 0x88E4
  val GL_DYNAMIC_DRAW = 0x88E8
  val GL_BUFFER_SIZE = 0x8764
  val GL_BUFFER_USAGE = 0x8765
  val GL_CURRENT_VERTEX_ATTRIB = 0x8626
  val GL_FRONT = 0x0404
  val GL_BACK = 0x0405
  val GL_FRONT_AND_BACK = 0x0408
  val GL_TEXTURE_2D = 0x0DE1
  val GL_CULL_FACE = 0x0B44
  val GL_BLEND = 0x0BE2
  val GL_DITHER = 0x0BD0
  val GL_STENCIL_TEST = 0x0B90
  val GL_DEPTH_TEST = 0x0B71
  val GL_SCISSOR_TEST = 0x0C11
  val GL_POLYGON_OFFSET_FILL = 0x8037
  val GL_SAMPLE_ALPHA_TO_COVERAGE = 0x809E
  val GL_SAMPLE_COVERAGE = 0x80A0
  val GL_NO_ERROR = 0
  val GL_INVALID_ENUM = 0x0500
  val GL_INVALID_VALUE = 0x0501
  val GL_INVALID_OPERATION = 0x0502
  val GL_OUT_OF_MEMORY = 0x0505
  val GL_CW = 0x0900
  val GL_CCW = 0x0901
  val GL_LINE_WIDTH = 0x0B21
  val GL_ALIASED_POINT_SIZE_RANGE = 0x846D
  val GL_ALIASED_LINE_WIDTH_RANGE = 0x846E
  val GL_CULL_FACE_MODE = 0x0B45
  val GL_FRONT_FACE = 0x0B46
  val GL_DEPTH_RANGE = 0x0B70
  val GL_DEPTH_WRITEMASK = 0x0B72
  val GL_DEPTH_CLEAR_VALUE = 0x0B73
  val GL_DEPTH_FUNC = 0x0B74
  val GL_STENCIL_CLEAR_VALUE = 0x0B91
  val GL_STENCIL_FUNC = 0x0B92
  val GL_STENCIL_FAIL = 0x0B94
  val GL_STENCIL_PASS_DEPTH_FAIL = 0x0B95
  val GL_STENCIL_PASS_DEPTH_PASS = 0x0B96
  val GL_STENCIL_REF = 0x0B97
  val GL_STENCIL_VALUE_MASK = 0x0B93
  val GL_STENCIL_WRITEMASK = 0x0B98
  val GL_STENCIL_BACK_FUNC = 0x8800
  val GL_STENCIL_BACK_FAIL = 0x8801
  val GL_STENCIL_BACK_PASS_DEPTH_FAIL = 0x8802
  val GL_STENCIL_BACK_PASS_DEPTH_PASS = 0x8803
  val GL_STENCIL_BACK_REF = 0x8CA3
  val GL_STENCIL_BACK_VALUE_MASK = 0x8CA4
  val GL_STENCIL_BACK_WRITEMASK = 0x8CA5
  val GL_VIEWPORT = 0x0BA2
  val GL_SCISSOR_BOX = 0x0C10
  val GL_COLOR_CLEAR_VALUE = 0x0C22
  val GL_COLOR_WRITEMASK = 0x0C23
  val GL_UNPACK_ALIGNMENT = 0x0CF5
  val GL_PACK_ALIGNMENT = 0x0D05
  val GL_MAX_TEXTURE_SIZE = 0x0D33
  val GL_MAX_TEXTURE_UNITS = 0x84E2
  val GL_MAX_VIEWPORT_DIMS = 0x0D3A
  val GL_SUBPIXEL_BITS = 0x0D50
  val GL_RED_BITS = 0x0D52
  val GL_GREEN_BITS = 0x0D53
  val GL_BLUE_BITS = 0x0D54
  val GL_ALPHA_BITS = 0x0D55
  val GL_DEPTH_BITS = 0x0D56
  val GL_STENCIL_BITS = 0x0D57
  val GL_POLYGON_OFFSET_UNITS = 0x2A00
  val GL_POLYGON_OFFSET_FACTOR = 0x8038
  val GL_TEXTURE_BINDING_2D = 0x8069
  val GL_SAMPLE_BUFFERS = 0x80A8
  val GL_SAMPLES = 0x80A9
  val GL_SAMPLE_COVERAGE_VALUE = 0x80AA
  val GL_SAMPLE_COVERAGE_INVERT = 0x80AB
  val GL_NUM_COMPRESSED_TEXTURE_FORMATS = 0x86A2
  val GL_COMPRESSED_TEXTURE_FORMATS = 0x86A3
  val GL_DONT_CARE = 0x1100
  val GL_FASTEST = 0x1101
  val GL_NICEST = 0x1102
  val GL_GENERATE_MIPMAP = 0x8191
  val GL_GENERATE_MIPMAP_HINT = 0x8192
  val GL_BYTE = 0x1400
  val GL_UNSIGNED_BYTE = 0x1401
  val GL_SHORT = 0x1402
  val GL_UNSIGNED_SHORT = 0x1403
  val GL_INT = 0x1404
  val GL_UNSIGNED_INT = 0x1405
  val GL_FLOAT = 0x1406
  val GL_FIXED = 0x140C
  val GL_DEPTH_COMPONENT = 0x1902
  val GL_ALPHA = 0x1906
  val GL_RGB = 0x1907
  val GL_RGBA = 0x1908
  val GL_LUMINANCE = 0x1909
  val GL_LUMINANCE_ALPHA = 0x190A
  val GL_UNSIGNED_SHORT_4_4_4_4 = 0x8033
  val GL_UNSIGNED_SHORT_5_5_5_1 = 0x8034
  val GL_UNSIGNED_SHORT_5_6_5 = 0x8363
  val GL_FRAGMENT_SHADER = 0x8B30
  val GL_VERTEX_SHADER = 0x8B31
  val GL_MAX_VERTEX_ATTRIBS = 0x8869
  val GL_MAX_VERTEX_UNIFORM_VECTORS = 0x8DFB
  val GL_MAX_VARYING_VECTORS = 0x8DFC
  val GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS = 0x8B4D
  val GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS = 0x8B4C
  val GL_MAX_TEXTURE_IMAGE_UNITS = 0x8872
  val GL_MAX_FRAGMENT_UNIFORM_VECTORS = 0x8DFD
  val GL_SHADER_TYPE = 0x8B4F
  val GL_DELETE_STATUS = 0x8B80
  val GL_LINK_STATUS = 0x8B82
  val GL_VALIDATE_STATUS = 0x8B83
  val GL_ATTACHED_SHADERS = 0x8B85
  val GL_ACTIVE_UNIFORMS = 0x8B86
  val GL_ACTIVE_UNIFORM_MAX_LENGTH = 0x8B87
  val GL_ACTIVE_ATTRIBUTES = 0x8B89
  val GL_ACTIVE_ATTRIBUTE_MAX_LENGTH = 0x8B8A
  val GL_SHADING_LANGUAGE_VERSION = 0x8B8C
  val GL_CURRENT_PROGRAM = 0x8B8D
  val GL_NEVER = 0x0200
  val GL_LESS = 0x0201
  val GL_EQUAL = 0x0202
  val GL_LEQUAL = 0x0203
  val GL_GREATER = 0x0204
  val GL_NOTEQUAL = 0x0205
  val GL_GEQUAL = 0x0206
  val GL_ALWAYS = 0x0207
  val GL_KEEP = 0x1E00
  val GL_REPLACE = 0x1E01
  val GL_INCR = 0x1E02
  val GL_DECR = 0x1E03
  val GL_INVERT = 0x150A
  val GL_INCR_WRAP = 0x8507
  val GL_DECR_WRAP = 0x8508
  val GL_VENDOR = 0x1F00
  val GL_RENDERER = 0x1F01
  val GL_VERSION = 0x1F02
  val GL_EXTENSIONS = 0x1F03
  val GL_NEAREST = 0x2600
  val GL_LINEAR = 0x2601
  val GL_NEAREST_MIPMAP_NEAREST = 0x2700
  val GL_LINEAR_MIPMAP_NEAREST = 0x2701
  val GL_NEAREST_MIPMAP_LINEAR = 0x2702
  val GL_LINEAR_MIPMAP_LINEAR = 0x2703
  val GL_TEXTURE_MAG_FILTER = 0x2800
  val GL_TEXTURE_MIN_FILTER = 0x2801
  val GL_TEXTURE_WRAP_S = 0x2802
  val GL_TEXTURE_WRAP_T = 0x2803
  val GL_TEXTURE = 0x1702
  val GL_TEXTURE_CUBE_MAP = 0x8513
  val GL_TEXTURE_BINDING_CUBE_MAP = 0x8514
  val GL_TEXTURE_CUBE_MAP_POSITIVE_X = 0x8515
  val GL_TEXTURE_CUBE_MAP_NEGATIVE_X = 0x8516
  val GL_TEXTURE_CUBE_MAP_POSITIVE_Y = 0x8517
  val GL_TEXTURE_CUBE_MAP_NEGATIVE_Y = 0x8518
  val GL_TEXTURE_CUBE_MAP_POSITIVE_Z = 0x8519
  val GL_TEXTURE_CUBE_MAP_NEGATIVE_Z = 0x851A
  val GL_MAX_CUBE_MAP_TEXTURE_SIZE = 0x851C
  val GL_TEXTURE0 = 0x84C0
  val GL_TEXTURE1 = 0x84C1
  val GL_TEXTURE2 = 0x84C2
  val GL_TEXTURE3 = 0x84C3
  val GL_TEXTURE4 = 0x84C4
  val GL_TEXTURE5 = 0x84C5
  val GL_TEXTURE6 = 0x84C6
  val GL_TEXTURE7 = 0x84C7
  val GL_TEXTURE8 = 0x84C8
  val GL_TEXTURE9 = 0x84C9
  val GL_TEXTURE10 = 0x84CA
  val GL_TEXTURE11 = 0x84CB
  val GL_TEXTURE12 = 0x84CC
  val GL_TEXTURE13 = 0x84CD
  val GL_TEXTURE14 = 0x84CE
  val GL_TEXTURE15 = 0x84CF
  val GL_TEXTURE16 = 0x84D0
  val GL_TEXTURE17 = 0x84D1
  val GL_TEXTURE18 = 0x84D2
  val GL_TEXTURE19 = 0x84D3
  val GL_TEXTURE20 = 0x84D4
  val GL_TEXTURE21 = 0x84D5
  val GL_TEXTURE22 = 0x84D6
  val GL_TEXTURE23 = 0x84D7
  val GL_TEXTURE24 = 0x84D8
  val GL_TEXTURE25 = 0x84D9
  val GL_TEXTURE26 = 0x84DA
  val GL_TEXTURE27 = 0x84DB
  val GL_TEXTURE28 = 0x84DC
  val GL_TEXTURE29 = 0x84DD
  val GL_TEXTURE30 = 0x84DE
  val GL_TEXTURE31 = 0x84DF
  val GL_ACTIVE_TEXTURE = 0x84E0
  val GL_REPEAT = 0x2901
  val GL_CLAMP_TO_EDGE = 0x812F
  val GL_MIRRORED_REPEAT = 0x8370
  val GL_FLOAT_VEC2 = 0x8B50
  val GL_FLOAT_VEC3 = 0x8B51
  val GL_FLOAT_VEC4 = 0x8B52
  val GL_INT_VEC2 = 0x8B53
  val GL_INT_VEC3 = 0x8B54
  val GL_INT_VEC4 = 0x8B55
  val GL_BOOL = 0x8B56
  val GL_BOOL_VEC2 = 0x8B57
  val GL_BOOL_VEC3 = 0x8B58
  val GL_BOOL_VEC4 = 0x8B59
  val GL_FLOAT_MAT2 = 0x8B5A
  val GL_FLOAT_MAT3 = 0x8B5B
  val GL_FLOAT_MAT4 = 0x8B5C
  val GL_SAMPLER_2D = 0x8B5E
  val GL_SAMPLER_CUBE = 0x8B60
  val GL_VERTEX_ATTRIB_ARRAY_ENABLED = 0x8622
  val GL_VERTEX_ATTRIB_ARRAY_SIZE = 0x8623
  val GL_VERTEX_ATTRIB_ARRAY_STRIDE = 0x8624
  val GL_VERTEX_ATTRIB_ARRAY_TYPE = 0x8625
  val GL_VERTEX_ATTRIB_ARRAY_NORMALIZED = 0x886A
  val GL_VERTEX_ATTRIB_ARRAY_POINTER = 0x8645
  val GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = 0x889F
  val GL_IMPLEMENTATION_COLOR_READ_TYPE = 0x8B9A
  val GL_IMPLEMENTATION_COLOR_READ_FORMAT = 0x8B9B
  val GL_COMPILE_STATUS = 0x8B81
  val GL_INFO_LOG_LENGTH = 0x8B84
  val GL_SHADER_SOURCE_LENGTH = 0x8B88
  val GL_SHADER_COMPILER = 0x8DFA
  val GL_SHADER_BINARY_FORMATS = 0x8DF8
  val GL_NUM_SHADER_BINARY_FORMATS = 0x8DF9
  val GL_LOW_FLOAT = 0x8DF0
  val GL_MEDIUM_FLOAT = 0x8DF1
  val GL_HIGH_FLOAT = 0x8DF2
  val GL_LOW_INT = 0x8DF3
  val GL_MEDIUM_INT = 0x8DF4
  val GL_HIGH_INT = 0x8DF5
  val GL_FRAMEBUFFER = 0x8D40
  val GL_RENDERBUFFER = 0x8D41
  val GL_RGBA4 = 0x8056
  val GL_RGB5_A1 = 0x8057
  val GL_RGB565 = 0x8D62
  val GL_DEPTH_COMPONENT16 = 0x81A5
  val GL_STENCIL_INDEX = 0x1901
  val GL_STENCIL_INDEX8 = 0x8D48
  val GL_RENDERBUFFER_WIDTH = 0x8D42
  val GL_RENDERBUFFER_HEIGHT = 0x8D43
  val GL_RENDERBUFFER_INTERNAL_FORMAT = 0x8D44
  val GL_RENDERBUFFER_RED_SIZE = 0x8D50
  val GL_RENDERBUFFER_GREEN_SIZE = 0x8D51
  val GL_RENDERBUFFER_BLUE_SIZE = 0x8D52
  val GL_RENDERBUFFER_ALPHA_SIZE = 0x8D53
  val GL_RENDERBUFFER_DEPTH_SIZE = 0x8D54
  val GL_RENDERBUFFER_STENCIL_SIZE = 0x8D55
  val GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE = 0x8CD0
  val GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME = 0x8CD1
  val GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL = 0x8CD2
  val GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE = 0x8CD3
  val GL_COLOR_ATTACHMENT0 = 0x8CE0
  val GL_DEPTH_ATTACHMENT = 0x8D00
  val GL_STENCIL_ATTACHMENT = 0x8D20
  val GL_NONE = 0
  val GL_FRAMEBUFFER_COMPLETE = 0x8CD5
  val GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 0x8CD6
  val GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 0x8CD7
  val GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS = 0x8CD9
  val GL_FRAMEBUFFER_UNSUPPORTED = 0x8CDD
  val GL_FRAMEBUFFER_BINDING = 0x8CA6
  val GL_RENDERBUFFER_BINDING = 0x8CA7
  val GL_MAX_RENDERBUFFER_SIZE = 0x84E8
  val GL_INVALID_FRAMEBUFFER_OPERATION = 0x0506
  val GL_VERTEX_PROGRAM_POINT_SIZE = 0x8642

  // Extensions
  val GL_COVERAGE_BUFFER_BIT_NV = 0x8000
  val GL_TEXTURE_MAX_ANISOTROPY_EXT = 0x84FE
  val GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT = 0x84FF

  def glActiveTexture (texture:Int):Unit

  def glBindTexture (target:Int, texture:Int):Unit

  def glBlendFunc (sfactor:Int, dfactor:Int):Unit

  def glClear (mask:Int):Unit

  def glClearColor (red:Float, green:Float, blue:Float, alpha:Float):Unit

  def glClearDepthf (depth:Float):Unit

  def glClearStencil (s:Int):Unit

  def glColorMask (red:Boolean, green:Boolean, blue:Boolean, alpha:Boolean):Unit

  def glCompressedTexImage2D (target:Int, level:Int, internalformat:Int, width:Int, height:Int, border:Int, imageSize:Int, data:Buffer):Unit

  def glCompressedTexSubImage2D (target:Int, level:Int, xoffset:Int, yoffset:Int, width:Int, height:Int, format:Int, imageSize:Int, data:Buffer):Unit

  def glCopyTexImage2D (target:Int, level:Int, internalformat:Int, x:Int, y:Int, width:Int, height:Int, border:Int):Unit

  def glCopyTexSubImage2D (target:Int, level:Int, xoffset:Int, yoffset:Int, x:Int, y:Int, width:Int, height:Int):Unit

  def glCullFace (mode:Int):Unit

  def glDeleteTextures (n:Int, textures:IntBuffer):Unit
  
  def glDeleteTexture (texture:Int):Unit

  def glDepthFunc (func:Int):Unit

  def glDepthMask (flag:Boolean):Unit

  def glDepthRangef (zNear:Float, zFar:Float):Unit

  def glDisable (cap:Int):Unit

  def glDrawArrays (mode:Int, first:Int, count:Int):Unit

  def glDrawElements (mode:Int, count:Int, `type`:Int, indices:Buffer):Unit

  def glEnable (cap:Int):Unit

  def glFinish ():Unit

  def glFlush ():Unit

  def glFrontFace (mode:Int):Unit

  def glGenTextures (n:Int, textures:IntBuffer):Unit
  
  def glGenTexture ():Int

  def glGetError ():Int

  def glGetIntegerv (pname:Int, params:IntBuffer):Unit

  def glGetString (name:Int):String

  def glHint (target:Int, mode:Int):Unit

  def glLineWidth (width:Float):Unit

  def glPixelStorei (pname:Int, param:Int):Unit

  def glPolygonOffset (factor:Float, units:Float):Unit

  def glReadPixels (x:Int, y:Int, width:Int, height:Int, format:Int, `type`:Int, pixels:Buffer):Unit

  def glScissor (x:Int, y:Int, width:Int, height:Int):Unit

  def glStencilFunc (func:Int, ref:Int, mask:Int):Unit

  def glStencilMask (mask:Int):Unit

  def glStencilOp (fail:Int, zfail:Int, zpass:Int):Unit

  def glTexImage2D (target:Int, level:Int, internalformat:Int, width:Int, height:Int, border:Int, format:Int, `type`:Int, pixels:Buffer):Unit

  def glTexParameterf (target:Int, pname:Int, param:Float):Unit

  def glTexSubImage2D (target:Int, level:Int, xoffset:Int, yoffset:Int, width:Int, height:Int, format:Int, `type`:Int, pixels:Buffer):Unit

  def glViewport (x:Int, y:Int, width:Int, height:Int):Unit

  def glAttachShader (program:Int, shader:Int):Unit

  def glBindAttribLocation (program:Int, index:Int, name:String):Unit

  def glBindBuffer (target:Int, buffer:Int):Unit

  def glBindFramebuffer (target:Int, framebuffer:Int):Unit

  def glBindRenderbuffer (target:Int, renderbuffer:Int):Unit

  def glBlendColor (red:Float, green:Float, blue:Float, alpha:Float):Unit

  def glBlendEquation (mode:Int):Unit

  def glBlendEquationSeparate (modeRGB:Int, modeAlpha:Int):Unit

  def glBlendFuncSeparate (srcRGB:Int, dstRGB:Int, srcAlpha:Int, dstAlpha:Int):Unit

  def glBufferData (target:Int, size:Int, data:Buffer, usage:Int):Unit

  def glBufferSubData (target:Int, offset:Int, size:Int, data:Buffer):Unit

  def glCheckFramebufferStatus (target:Int):Int

  def glCompileShader (shader:Int):Unit

  def glCreateProgram ():Int

  def glCreateShader (`type`:Int):Int

  def glDeleteBuffer (buffer:Int):Unit

  def glDeleteBuffers (n:Int, buffers:IntBuffer):Unit

  def glDeleteFramebuffer (framebuffer:Int):Unit
  
  def glDeleteFramebuffers (n:Int, framebuffers:IntBuffer):Unit

  def glDeleteProgram (program:Int):Unit

  def glDeleteRenderbuffer (renderbuffer:Int):Unit
  
  def glDeleteRenderbuffers (n:Int, renderbuffers:IntBuffer):Unit

  def glDeleteShader (shader:Int):Unit

  def glDetachShader (program:Int, shader:Int):Unit

  def glDisableVertexAttribArray (index:Int):Unit

  def glDrawElements (mode:Int, count:Int, `type`:Int, indices:Int):Unit

  def glEnableVertexAttribArray (index:Int):Unit

  def glFramebufferRenderbuffer (target:Int, attachment:Int, renderbuffertarget:Int, renderbuffer:Int):Unit

  def glFramebufferTexture2D (target:Int, attachment:Int, textarget:Int, texture:Int, level:Int):Unit

  def glGenBuffer ():Int

  def glGenBuffers (n:Int, buffers:IntBuffer):Unit

  def glGenerateMipmap (target:Int):Unit

  def glGenFramebuffer ():Int

  def glGenFramebuffers (n:Int, framebuffers:IntBuffer):Unit

  def glGenRenderbuffer ():Int

  def glGenRenderbuffers (n:Int, renderbuffers:IntBuffer):Unit

  // deviates
  def glGetActiveAttrib (program:Int, index:Int, size:IntBuffer, `type`:Buffer):String

  // deviates
  def glGetActiveUniform (program:Int, index:Int, size:IntBuffer, `type`:Buffer):String

  def glGetAttachedShaders (program:Int, maxcount:Int, count:Buffer, shaders:IntBuffer):Unit

  def glGetAttribLocation (program:Int, name:String):Int

  def glGetBooleanv (pname:Int, params:Buffer):Unit

  def glGetBufferParameteriv (target:Int, pname:Int, params:IntBuffer):Unit

  def glGetFloatv (pname:Int, params:FloatBuffer):Unit

  def glGetFramebufferAttachmentParameteriv (target:Int, attachment:Int, pname:Int, params:IntBuffer):Unit

  def glGetProgramiv (program:Int, pname:Int, params:IntBuffer):Unit

  // deviates
  def glGetProgramInfoLog (program:Int):String

  def glGetRenderbufferParameteriv (target:Int, pname:Int, params:IntBuffer):Unit

  def glGetShaderiv (shader:Int, pname:Int, params:IntBuffer):Unit

  // deviates
  def glGetShaderInfoLog (shader:Int):String

  def glGetShaderPrecisionFormat (shadertype:Int, precisiontype:Int, range:IntBuffer, precision:IntBuffer):Unit

  def glGetTexParameterfv (target:Int, pname:Int, params:FloatBuffer):Unit

  def glGetTexParameteriv (target:Int, pname:Int, params:IntBuffer):Unit

  def glGetUniformfv (program:Int, location:Int, params:FloatBuffer):Unit

  def glGetUniformiv (program:Int, location:Int, params:IntBuffer):Unit

  def glGetUniformLocation (program:Int, name:String):Int

  def glGetVertexAttribfv (index:Int, pname:Int, params:FloatBuffer):Unit

  def glGetVertexAttribiv (index:Int, pname:Int, params:IntBuffer):Unit

  def glGetVertexAttribPointerv (index:Int, pname:Int, pointer:Buffer):Unit

  def glIsBuffer (buffer:Int):Boolean

  def glIsEnabled (cap:Int):Boolean

  def glIsFramebuffer (framebuffer:Int):Boolean

  def glIsProgram (program:Int):Boolean

  def glIsRenderbuffer (renderbuffer:Int):Boolean

  def glIsShader (shader:Int):Boolean

  def glIsTexture (texture:Int):Boolean

  def glLinkProgram (program:Int):Unit

  def glReleaseShaderCompiler ():Unit

  def glRenderbufferStorage (target:Int, internalformat:Int, width:Int, height:Int):Unit

  def glSampleCoverage (value:Float, invert:Boolean):Unit

  def glShaderBinary (n:Int, shaders:IntBuffer, binaryformat:Int, binary:Buffer, length:Int):Unit

  // Deviates
  def glShaderSource (shader:Int, string:String):Unit

  def glStencilFuncSeparate (face:Int, func:Int, ref:Int, mask:Int):Unit

  def glStencilMaskSeparate (face:Int, mask:Int):Unit

  def glStencilOpSeparate (face:Int, fail:Int, zfail:Int, zpass:Int):Unit

  def glTexParameterfv (target:Int, pname:Int, params:FloatBuffer):Unit

  def glTexParameteri (target:Int, pname:Int, param:Int):Unit

  def glTexParameteriv (target:Int, pname:Int, params:IntBuffer):Unit

  def glUniform1f (location:Int, x:Float):Unit

  def glUniform1fv (location:Int, count:Int, v:FloatBuffer):Unit
  
  def glUniform1fv (location:Int, count:Int, v:Array[Float], offset:Int):Unit

  def glUniform1i (location:Int, x:Int):Unit

  def glUniform1iv (location:Int, count:Int, v:IntBuffer):Unit
  
  def glUniform1iv (location:Int, count:Int, v:Array[Int], offset:Int):Unit

  def glUniform2f (location:Int, x:Float, y:Float):Unit

  def glUniform2fv (location:Int, count:Int, v:FloatBuffer):Unit
  
  def glUniform2fv (location:Int, count:Int, v:Array[Float], offset:Int):Unit

  def glUniform2i (location:Int, x:Int, y:Int):Unit

  def glUniform2iv (location:Int, count:Int, v:IntBuffer):Unit
  
  def glUniform2iv (location:Int, count:Int, v:Array[Int], offset:Int):Unit

  def glUniform3f (location:Int, x:Float, y:Float, z:Float):Unit

  def glUniform3fv (location:Int, count:Int, v:FloatBuffer):Unit
  
  def glUniform3fv (location:Int, count:Int, v:Array[Float], offset:Int):Unit

  def glUniform3i (location:Int, x:Int, y:Int, z:Int):Unit

  def glUniform3iv (location:Int, count:Int, v:IntBuffer):Unit
  
  def glUniform3iv (location:Int, count:Int, v:Array[Int], offset:Int):Unit

  def glUniform4f (location:Int, x:Float, y:Float, z:Float, w:Float):Unit

  def glUniform4fv (location:Int, count:Int, v:FloatBuffer):Unit
  
  def glUniform4fv (location:Int, count:Int, v:Array[Float], offset:Int):Unit

  def glUniform4i (location:Int, x:Int, y:Int, z:Int, w:Int):Unit

  def glUniform4iv (location:Int, count:Int, v:IntBuffer):Unit
  
  def glUniform4iv (location:Int, count:Int, v:Array[Int], offset:Int):Unit

  def glUniformMatrix2fv (location:Int, count:Int, transpose:Boolean, value:FloatBuffer):Unit
  
  def glUniformMatrix2fv (location:Int, count:Int, transpose:Boolean, value:Array[Float], offset:Int):Unit

  def glUniformMatrix3fv (location:Int, count:Int, transpose:Boolean, value:FloatBuffer):Unit
  
  def glUniformMatrix3fv (location:Int, count:Int, transpose:Boolean, value:Array[Float], offset:Int):Unit

  def glUniformMatrix4fv (location:Int, count:Int, transpose:Boolean, value:FloatBuffer):Unit
  
  def glUniformMatrix4fv (location:Int, count:Int, transpose:Boolean, value:Array[Float], offset:Int):Unit

  def glUseProgram (program:Int):Unit

  def glValidateProgram (program:Int):Unit

  def glVertexAttrib1f (indx:Int, x:Float):Unit

  def glVertexAttrib1fv (indx:Int, values:FloatBuffer):Unit

  def glVertexAttrib2f (indx:Int, x:Float, y:Float):Unit

  def glVertexAttrib2fv (indx:Int, values:FloatBuffer):Unit

  def glVertexAttrib3f (indx:Int, x:Float, y:Float, z:Float):Unit

  def glVertexAttrib3fv (indx:Int, values:FloatBuffer):Unit

  def glVertexAttrib4f (indx:Int, x:Float, y:Float, z:Float, w:Float):Unit

  def glVertexAttrib4fv (indx:Int, values:FloatBuffer):Unit

  /**
   * In OpenGl core profiles (3.1+), passing a pointer to client memory is not valid.
   * In 3.0 and later, use the other version of this function instead, pass a zero-based
   * offset which references the buffer currently bound to GL_ARRAY_BUFFER.
   */
  def glVertexAttribPointer (indx:Int, size:Int, `type`:Int, normalized:Boolean, stride:Int, ptr:Buffer):Unit

  def glVertexAttribPointer (indx:Int, size:Int, `type`:Int, normalized:Boolean, stride:Int, ptr:Int):Unit

}
