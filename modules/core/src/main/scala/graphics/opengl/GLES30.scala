/*
 **
 ** Copyright 2013, The Android Open Source Project
 **
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */


package seer.graphics

import java.nio.Buffer

/** OpenGL ES 3.0 */
trait GLES30 extends GLES20 {
  val GL_READ_BUFFER = 0x0C02
  val GL_UNPACK_ROW_LENGTH = 0x0CF2
  val GL_UNPACK_SKIP_ROWS = 0x0CF3
  val GL_UNPACK_SKIP_PIXELS = 0x0CF4
  val GL_PACK_ROW_LENGTH = 0x0D02
  val GL_PACK_SKIP_ROWS = 0x0D03
  val GL_PACK_SKIP_PIXELS = 0x0D04
  val GL_COLOR = 0x1800
  val GL_DEPTH = 0x1801
  val GL_STENCIL = 0x1802
  val GL_RED = 0x1903
  val GL_RGB8 = 0x8051
  val GL_RGBA8 = 0x8058
  val GL_RGB10_A2 = 0x8059
  val GL_TEXTURE_BINDING_3D = 0x806A
  val GL_UNPACK_SKIP_IMAGES = 0x806D
  val GL_UNPACK_IMAGE_HEIGHT = 0x806E
  val GL_TEXTURE_3D = 0x806F
  val GL_TEXTURE_WRAP_R = 0x8072
  val GL_MAX_3D_TEXTURE_SIZE = 0x8073
  val GL_UNSIGNED_INT_2_10_10_10_REV = 0x8368
  val GL_MAX_ELEMENTS_VERTICES = 0x80E8
  val GL_MAX_ELEMENTS_INDICES = 0x80E9
  val GL_TEXTURE_MIN_LOD = 0x813A
  val GL_TEXTURE_MAX_LOD = 0x813B
  val GL_TEXTURE_BASE_LEVEL = 0x813C
  val GL_TEXTURE_MAX_LEVEL = 0x813D
  val GL_MIN = 0x8007
  val GL_MAX = 0x8008
  val GL_DEPTH_COMPONENT24 = 0x81A6
  val GL_MAX_TEXTURE_LOD_BIAS = 0x84FD
  val GL_TEXTURE_COMPARE_MODE = 0x884C
  val GL_TEXTURE_COMPARE_FUNC = 0x884D
  val GL_CURRENT_QUERY = 0x8865
  val GL_QUERY_RESULT = 0x8866
  val GL_QUERY_RESULT_AVAILABLE = 0x8867
  val GL_BUFFER_MAPPED = 0x88BC
  val GL_BUFFER_MAP_POINTER = 0x88BD
  val GL_STREAM_READ = 0x88E1
  val GL_STREAM_COPY = 0x88E2
  val GL_STATIC_READ = 0x88E5
  val GL_STATIC_COPY = 0x88E6
  val GL_DYNAMIC_READ = 0x88E9
  val GL_DYNAMIC_COPY = 0x88EA
  val GL_MAX_DRAW_BUFFERS = 0x8824
  val GL_DRAW_BUFFER0 = 0x8825
  val GL_DRAW_BUFFER1 = 0x8826
  val GL_DRAW_BUFFER2 = 0x8827
  val GL_DRAW_BUFFER3 = 0x8828
  val GL_DRAW_BUFFER4 = 0x8829
  val GL_DRAW_BUFFER5 = 0x882A
  val GL_DRAW_BUFFER6 = 0x882B
  val GL_DRAW_BUFFER7 = 0x882C
  val GL_DRAW_BUFFER8 = 0x882D
  val GL_DRAW_BUFFER9 = 0x882E
  val GL_DRAW_BUFFER10 = 0x882F
  val GL_DRAW_BUFFER11 = 0x8830
  val GL_DRAW_BUFFER12 = 0x8831
  val GL_DRAW_BUFFER13 = 0x8832
  val GL_DRAW_BUFFER14 = 0x8833
  val GL_DRAW_BUFFER15 = 0x8834
  val GL_MAX_FRAGMENT_UNIFORM_COMPONENTS = 0x8B49
  val GL_MAX_VERTEX_UNIFORM_COMPONENTS = 0x8B4A
  val GL_SAMPLER_3D = 0x8B5F
  val GL_SAMPLER_2D_SHADOW = 0x8B62
  val GL_FRAGMENT_SHADER_DERIVATIVE_HINT = 0x8B8B
  val GL_PIXEL_PACK_BUFFER = 0x88EB
  val GL_PIXEL_UNPACK_BUFFER = 0x88EC
  val GL_PIXEL_PACK_BUFFER_BINDING = 0x88ED
  val GL_PIXEL_UNPACK_BUFFER_BINDING = 0x88EF
  val GL_FLOAT_MAT2x3 = 0x8B65
  val GL_FLOAT_MAT2x4 = 0x8B66
  val GL_FLOAT_MAT3x2 = 0x8B67
  val GL_FLOAT_MAT3x4 = 0x8B68
  val GL_FLOAT_MAT4x2 = 0x8B69
  val GL_FLOAT_MAT4x3 = 0x8B6A
  val GL_SRGB = 0x8C40
  val GL_SRGB8 = 0x8C41
  val GL_SRGB8_ALPHA8 = 0x8C43
  val GL_COMPARE_REF_TO_TEXTURE = 0x884E
  val GL_MAJOR_VERSION = 0x821B
  val GL_MINOR_VERSION = 0x821C
  val GL_NUM_EXTENSIONS = 0x821D
  val GL_RGBA32F = 0x8814
  val GL_RGB32F = 0x8815
  val GL_RGBA16F = 0x881A
  val GL_RGB16F = 0x881B
  val GL_VERTEX_ATTRIB_ARRAY_INTEGER = 0x88FD
  val GL_MAX_ARRAY_TEXTURE_LAYERS = 0x88FF
  val GL_MIN_PROGRAM_TEXEL_OFFSET = 0x8904
  val GL_MAX_PROGRAM_TEXEL_OFFSET = 0x8905
  val GL_MAX_VARYING_COMPONENTS = 0x8B4B
  val GL_TEXTURE_2D_ARRAY = 0x8C1A
  val GL_TEXTURE_BINDING_2D_ARRAY = 0x8C1D
  val GL_R11F_G11F_B10F = 0x8C3A
  val GL_UNSIGNED_INT_10F_11F_11F_REV = 0x8C3B
  val GL_RGB9_E5 = 0x8C3D
  val GL_UNSIGNED_INT_5_9_9_9_REV = 0x8C3E
  val GL_TRANSFORM_FEEDBACK_VARYING_MAX_LENGTH = 0x8C76
  val GL_TRANSFORM_FEEDBACK_BUFFER_MODE = 0x8C7F
  val GL_MAX_TRANSFORM_FEEDBACK_SEPARATE_COMPONENTS = 0x8C80
  val GL_TRANSFORM_FEEDBACK_VARYINGS = 0x8C83
  val GL_TRANSFORM_FEEDBACK_BUFFER_START = 0x8C84
  val GL_TRANSFORM_FEEDBACK_BUFFER_SIZE = 0x8C85
  val GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN = 0x8C88
  val GL_RASTERIZER_DISCARD = 0x8C89
  val GL_MAX_TRANSFORM_FEEDBACK_INTERLEAVED_COMPONENTS = 0x8C8A
  val GL_MAX_TRANSFORM_FEEDBACK_SEPARATE_ATTRIBS = 0x8C8B
  val GL_INTERLEAVED_ATTRIBS = 0x8C8C
  val GL_SEPARATE_ATTRIBS = 0x8C8D
  val GL_TRANSFORM_FEEDBACK_BUFFER = 0x8C8E
  val GL_TRANSFORM_FEEDBACK_BUFFER_BINDING = 0x8C8F
  val GL_RGBA32UI = 0x8D70
  val GL_RGB32UI = 0x8D71
  val GL_RGBA16UI = 0x8D76
  val GL_RGB16UI = 0x8D77
  val GL_RGBA8UI = 0x8D7C
  val GL_RGB8UI = 0x8D7D
  val GL_RGBA32I = 0x8D82
  val GL_RGB32I = 0x8D83
  val GL_RGBA16I = 0x8D88
  val GL_RGB16I = 0x8D89
  val GL_RGBA8I = 0x8D8E
  val GL_RGB8I = 0x8D8F
  val GL_RED_INTEGER = 0x8D94
  val GL_RGB_INTEGER = 0x8D98
  val GL_RGBA_INTEGER = 0x8D99
  val GL_SAMPLER_2D_ARRAY = 0x8DC1
  val GL_SAMPLER_2D_ARRAY_SHADOW = 0x8DC4
  val GL_SAMPLER_CUBE_SHADOW = 0x8DC5
  val GL_UNSIGNED_INT_VEC2 = 0x8DC6
  val GL_UNSIGNED_INT_VEC3 = 0x8DC7
  val GL_UNSIGNED_INT_VEC4 = 0x8DC8
  val GL_INT_SAMPLER_2D = 0x8DCA
  val GL_INT_SAMPLER_3D = 0x8DCB
  val GL_INT_SAMPLER_CUBE = 0x8DCC
  val GL_INT_SAMPLER_2D_ARRAY = 0x8DCF
  val GL_UNSIGNED_INT_SAMPLER_2D = 0x8DD2
  val GL_UNSIGNED_INT_SAMPLER_3D = 0x8DD3
  val GL_UNSIGNED_INT_SAMPLER_CUBE = 0x8DD4
  val GL_UNSIGNED_INT_SAMPLER_2D_ARRAY = 0x8DD7
  val GL_BUFFER_ACCESS_FLAGS = 0x911F
  val GL_BUFFER_MAP_LENGTH = 0x9120
  val GL_BUFFER_MAP_OFFSET = 0x9121
  val GL_DEPTH_COMPONENT32F = 0x8CAC
  val GL_DEPTH32F_STENCIL8 = 0x8CAD
  val GL_FLOAT_32_UNSIGNED_INT_24_8_REV = 0x8DAD
  val GL_FRAMEBUFFER_ATTACHMENT_COLOR_ENCODING = 0x8210
  val GL_FRAMEBUFFER_ATTACHMENT_COMPONENT_TYPE = 0x8211
  val GL_FRAMEBUFFER_ATTACHMENT_RED_SIZE = 0x8212
  val GL_FRAMEBUFFER_ATTACHMENT_GREEN_SIZE = 0x8213
  val GL_FRAMEBUFFER_ATTACHMENT_BLUE_SIZE = 0x8214
  val GL_FRAMEBUFFER_ATTACHMENT_ALPHA_SIZE = 0x8215
  val GL_FRAMEBUFFER_ATTACHMENT_DEPTH_SIZE = 0x8216
  val GL_FRAMEBUFFER_ATTACHMENT_STENCIL_SIZE = 0x8217
  val GL_FRAMEBUFFER_DEFAULT = 0x8218
  val GL_FRAMEBUFFER_UNDEFINED = 0x8219
  val GL_DEPTH_STENCIL_ATTACHMENT = 0x821A
  val GL_DEPTH_STENCIL = 0x84F9
  val GL_UNSIGNED_INT_24_8 = 0x84FA
  val GL_DEPTH24_STENCIL8 = 0x88F0
  val GL_UNSIGNED_NORMALIZED = 0x8C17
  val GL_DRAW_FRAMEBUFFER_BINDING = GL_FRAMEBUFFER_BINDING
  val GL_READ_FRAMEBUFFER = 0x8CA8
  val GL_DRAW_FRAMEBUFFER = 0x8CA9
  val GL_READ_FRAMEBUFFER_BINDING = 0x8CAA
  val GL_RENDERBUFFER_SAMPLES = 0x8CAB
  val GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LAYER = 0x8CD4
  val GL_MAX_COLOR_ATTACHMENTS = 0x8CDF
  val GL_COLOR_ATTACHMENT1 = 0x8CE1
  val GL_COLOR_ATTACHMENT2 = 0x8CE2
  val GL_COLOR_ATTACHMENT3 = 0x8CE3
  val GL_COLOR_ATTACHMENT4 = 0x8CE4
  val GL_COLOR_ATTACHMENT5 = 0x8CE5
  val GL_COLOR_ATTACHMENT6 = 0x8CE6
  val GL_COLOR_ATTACHMENT7 = 0x8CE7
  val GL_COLOR_ATTACHMENT8 = 0x8CE8
  val GL_COLOR_ATTACHMENT9 = 0x8CE9
  val GL_COLOR_ATTACHMENT10 = 0x8CEA
  val GL_COLOR_ATTACHMENT11 = 0x8CEB
  val GL_COLOR_ATTACHMENT12 = 0x8CEC
  val GL_COLOR_ATTACHMENT13 = 0x8CED
  val GL_COLOR_ATTACHMENT14 = 0x8CEE
  val GL_COLOR_ATTACHMENT15 = 0x8CEF
  val GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE = 0x8D56
  val GL_MAX_SAMPLES = 0x8D57
  val GL_HALF_FLOAT = 0x140B
  val GL_MAP_READ_BIT = 0x0001
  val GL_MAP_WRITE_BIT = 0x0002
  val GL_MAP_INVALIDATE_RANGE_BIT = 0x0004
  val GL_MAP_INVALIDATE_BUFFER_BIT = 0x0008
  val GL_MAP_FLUSH_EXPLICIT_BIT = 0x0010
  val GL_MAP_UNSYNCHRONIZED_BIT = 0x0020
  val GL_RG = 0x8227
  val GL_RG_INTEGER = 0x8228
  val GL_R8 = 0x8229
  val GL_RG8 = 0x822B
  val GL_R16F = 0x822D
  val GL_R32F = 0x822E
  val GL_RG16F = 0x822F
  val GL_RG32F = 0x8230
  val GL_R8I = 0x8231
  val GL_R8UI = 0x8232
  val GL_R16I = 0x8233
  val GL_R16UI = 0x8234
  val GL_R32I = 0x8235
  val GL_R32UI = 0x8236
  val GL_RG8I = 0x8237
  val GL_RG8UI = 0x8238
  val GL_RG16I = 0x8239
  val GL_RG16UI = 0x823A
  val GL_RG32I = 0x823B
  val GL_RG32UI = 0x823C
  val GL_VERTEX_ARRAY_BINDING = 0x85B5
  val GL_R8_SNORM = 0x8F94
  val GL_RG8_SNORM = 0x8F95
  val GL_RGB8_SNORM = 0x8F96
  val GL_RGBA8_SNORM = 0x8F97
  val GL_SIGNED_NORMALIZED = 0x8F9C
  val GL_PRIMITIVE_RESTART_FIXED_INDEX = 0x8D69
  val GL_COPY_READ_BUFFER = 0x8F36
  val GL_COPY_WRITE_BUFFER = 0x8F37
  val GL_COPY_READ_BUFFER_BINDING = GL_COPY_READ_BUFFER
  val GL_COPY_WRITE_BUFFER_BINDING = GL_COPY_WRITE_BUFFER
  val GL_UNIFORM_BUFFER = 0x8A11
  val GL_UNIFORM_BUFFER_BINDING = 0x8A28
  val GL_UNIFORM_BUFFER_START = 0x8A29
  val GL_UNIFORM_BUFFER_SIZE = 0x8A2A
  val GL_MAX_VERTEX_UNIFORM_BLOCKS = 0x8A2B
  val GL_MAX_FRAGMENT_UNIFORM_BLOCKS = 0x8A2D
  val GL_MAX_COMBINED_UNIFORM_BLOCKS = 0x8A2E
  val GL_MAX_UNIFORM_BUFFER_BINDINGS = 0x8A2F
  val GL_MAX_UNIFORM_BLOCK_SIZE = 0x8A30
  val GL_MAX_COMBINED_VERTEX_UNIFORM_COMPONENTS = 0x8A31
  val GL_MAX_COMBINED_FRAGMENT_UNIFORM_COMPONENTS = 0x8A33
  val GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT = 0x8A34
  val GL_ACTIVE_UNIFORM_BLOCK_MAX_NAME_LENGTH = 0x8A35
  val GL_ACTIVE_UNIFORM_BLOCKS = 0x8A36
  val GL_UNIFORM_TYPE = 0x8A37
  val GL_UNIFORM_SIZE = 0x8A38
  val GL_UNIFORM_NAME_LENGTH = 0x8A39
  val GL_UNIFORM_BLOCK_INDEX = 0x8A3A
  val GL_UNIFORM_OFFSET = 0x8A3B
  val GL_UNIFORM_ARRAY_STRIDE = 0x8A3C
  val GL_UNIFORM_MATRIX_STRIDE = 0x8A3D
  val GL_UNIFORM_IS_ROW_MAJOR = 0x8A3E
  val GL_UNIFORM_BLOCK_BINDING = 0x8A3F
  val GL_UNIFORM_BLOCK_DATA_SIZE = 0x8A40
  val GL_UNIFORM_BLOCK_NAME_LENGTH = 0x8A41
  val GL_UNIFORM_BLOCK_ACTIVE_UNIFORMS = 0x8A42
  val GL_UNIFORM_BLOCK_ACTIVE_UNIFORM_INDICES = 0x8A43
  val GL_UNIFORM_BLOCK_REFERENCED_BY_VERTEX_SHADER = 0x8A44
  val GL_UNIFORM_BLOCK_REFERENCED_BY_FRAGMENT_SHADER = 0x8A46
  // GL_INVALID_INDEX is defined as 0xFFFFFFFFu in C.
  val GL_INVALID_INDEX = -1
  val GL_MAX_VERTEX_OUTPUT_COMPONENTS = 0x9122
  val GL_MAX_FRAGMENT_INPUT_COMPONENTS = 0x9125
  val GL_MAX_SERVER_WAIT_TIMEOUT = 0x9111
  val GL_OBJECT_TYPE = 0x9112
  val GL_SYNC_CONDITION = 0x9113
  val GL_SYNC_STATUS = 0x9114
  val GL_SYNC_FLAGS = 0x9115
  val GL_SYNC_FENCE = 0x9116
  val GL_SYNC_GPU_COMMANDS_COMPLETE = 0x9117
  val GL_UNSIGNALED = 0x9118
  val GL_SIGNALED = 0x9119
  val GL_ALREADY_SIGNALED = 0x911A
  val GL_TIMEOUT_EXPIRED = 0x911B
  val GL_CONDITION_SATISFIED = 0x911C
  val GL_WAIT_FAILED = 0x911D
  val GL_SYNC_FLUSH_COMMANDS_BIT = 0x00000001
  // GL_TIMEOUT_IGNORED is defined as 0xFFFFFFFFFFFFFFFFull in C.
  val GL_TIMEOUT_IGNORED:Long = -1
  val GL_VERTEX_ATTRIB_ARRAY_DIVISOR = 0x88FE
  val GL_ANY_SAMPLES_PASSED = 0x8C2F
  val GL_ANY_SAMPLES_PASSED_CONSERVATIVE = 0x8D6A
  val GL_SAMPLER_BINDING = 0x8919
  val GL_RGB10_A2UI = 0x906F
  val GL_TEXTURE_SWIZZLE_R = 0x8E42
  val GL_TEXTURE_SWIZZLE_G = 0x8E43
  val GL_TEXTURE_SWIZZLE_B = 0x8E44
  val GL_TEXTURE_SWIZZLE_A = 0x8E45
  val GL_GREEN = 0x1904
  val GL_BLUE = 0x1905
  val GL_INT_2_10_10_10_REV = 0x8D9F
  val GL_TRANSFORM_FEEDBACK = 0x8E22
  val GL_TRANSFORM_FEEDBACK_PAUSED = 0x8E23
  val GL_TRANSFORM_FEEDBACK_ACTIVE = 0x8E24
  val GL_TRANSFORM_FEEDBACK_BINDING = 0x8E25
  val GL_PROGRAM_BINARY_RETRIEVABLE_HINT = 0x8257
  val GL_PROGRAM_BINARY_LENGTH = 0x8741
  val GL_NUM_PROGRAM_BINARY_FORMATS = 0x87FE
  val GL_PROGRAM_BINARY_FORMATS = 0x87FF
  val GL_COMPRESSED_R11_EAC = 0x9270
  val GL_COMPRESSED_SIGNED_R11_EAC = 0x9271
  val GL_COMPRESSED_RG11_EAC = 0x9272
  val GL_COMPRESSED_SIGNED_RG11_EAC = 0x9273
  val GL_COMPRESSED_RGB8_ETC2 = 0x9274
  val GL_COMPRESSED_SRGB8_ETC2 = 0x9275
  val GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2 = 0x9276
  val GL_COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2 = 0x9277
  val GL_COMPRESSED_RGBA8_ETC2_EAC = 0x9278
  val GL_COMPRESSED_SRGB8_ALPHA8_ETC2_EAC = 0x9279
  val GL_TEXTURE_IMMUTABLE_FORMAT = 0x912F
  val GL_MAX_ELEMENT_INDEX = 0x8D6B
  val GL_NUM_SAMPLE_COUNTS = 0x9380
  val GL_TEXTURE_IMMUTABLE_LEVELS = 0x82DF

  // C function void glReadBuffer ( GLenum mode )

  def glReadBuffer (mode:Int): Unit

  // C function void glDrawRangeElements ( GLenum mode, GLuint start, GLuint end, GLsizei count, GLenum `type`, const GLvoid
// *indices )

  def glDrawRangeElements (mode:Int, start:Int, end:Int, count:Int, `type`:Int, indices:java.nio.Buffer): Unit

  // C function void glDrawRangeElements ( GLenum mode, GLuint start, GLuint end, GLsizei count, GLenum `type`, GLsizei offset )

  def glDrawRangeElements (mode:Int, start:Int, end:Int, count:Int, `type`:Int, offset:Int): Unit

  // C function void glTexImage3D ( GLenum target, GLint level, GLint internalformat, GLsizei width, GLsizei height, GLsizei
// depth, GLint border, GLenum format, GLenum `type`, const GLvoid *pixels )

  def glTexImage3D (target:Int, level:Int, internalformat:Int, width:Int, height:Int, depth:Int, border:Int, format:Int, `type`:Int, pixels:java.nio.Buffer): Unit

  // C function void glTexImage3D ( GLenum target, GLint level, GLint internalformat, GLsizei width, GLsizei height, GLsizei
// depth, GLint border, GLenum format, GLenum `type`, GLsizei offset )

  def glTexImage3D (target:Int, level:Int, internalformat:Int, width:Int, height:Int, depth:Int, border:Int, format:Int, `type`:Int, offset:Int): Unit

  // C function void glTexSubImage3D ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint zoffset, GLsizei width,
// GLsizei height, GLsizei depth, GLenum format, GLenum `type`, const GLvoid *pixels )

  def glTexSubImage3D (target:Int, level:Int, xoffset:Int, yoffset:Int, zoffset:Int, width:Int, height:Int, depth:Int, format:Int, `type`:Int, pixels:java.nio.Buffer): Unit

  // C function void glTexSubImage3D ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint zoffset, GLsizei width,
// GLsizei height, GLsizei depth, GLenum format, GLenum `type`, GLsizei offset )

  def glTexSubImage3D (target:Int, level:Int, xoffset:Int, yoffset:Int, zoffset:Int, width:Int, height:Int, depth:Int, format:Int, `type`:Int, offset:Int): Unit

  // C function void glCopyTexSubImage3D ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint zoffset, GLint x,
// GLint y, GLsizei width, GLsizei height )

  def glCopyTexSubImage3D (target:Int, level:Int, xoffset:Int, yoffset:Int, zoffset:Int, x:Int, y:Int, width:Int, height:Int): Unit

// // C function void glCompressedTexImage3D ( GLenum target, GLint level, GLenum internalformat, GLsizei width, GLsizei height,
// GLsizei depth, GLint border, GLsizei imageSize, const GLvoid *data )
//
// def glCompressedTexImage3D: Unit
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
// def glCompressedTexImage3D: Unit
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
// def glCompressedTexSubImage3D: Unit
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
// def glCompressedTexSubImage3D: Unit
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

  def glGenQueries (n:Int, ids:Array[Int], offset:Int): Unit

  // C function void glGenQueries ( GLsizei n, GLuint *ids )

  def glGenQueries (n:Int, ids:java.nio.IntBuffer): Unit

  // C function void glDeleteQueries ( GLsizei n, const GLuint *ids )

  def glDeleteQueries (n:Int, ids:Array[Int], offset:Int): Unit

  // C function void glDeleteQueries ( GLsizei n, const GLuint *ids )

  def glDeleteQueries (n:Int, ids:java.nio.IntBuffer): Unit

  // C function GLboolean glIsQuery ( GLuint id )

  def glIsQuery (id:Int): Boolean

  // C function void glBeginQuery ( GLenum target, GLuint id )

  def glBeginQuery (target:Int, id:Int): Unit

  // C function void glEndQuery ( GLenum target )

  def glEndQuery (target:Int): Unit

// // C function void glGetQueryiv ( GLenum target, GLenum pname, GLint *params )
//
// def glGetQueryiv: Unit
// target:Int,
// pname:Int,
// params:Array[Int],
// offset:Int
// );

  // C function void glGetQueryiv ( GLenum target, GLenum pname, GLint *params )

  def glGetQueryiv (target:Int, pname:Int, params:java.nio.IntBuffer): Unit

// // C function void glGetQueryObjectuiv ( GLuint id, GLenum pname, GLuint *params )
//
// def glGetQueryObjectuiv: Unit
// id:Int,
// pname:Int,
// params:Array[Int],
// offset:Int
// );

  // C function void glGetQueryObjectuiv ( GLuint id, GLenum pname, GLuint *params )

  def glGetQueryObjectuiv (id:Int, pname:Int, params:java.nio.IntBuffer): Unit

  // C function GLboolean glUnmapBuffer ( GLenum target )

  def glUnmapBuffer (target:Int): Boolean

  // C function void glGetBufferPointerv ( GLenum target, GLenum pname, GLvoid** params )

  def glGetBufferPointerv (target:Int, pname:Int): java.nio.Buffer

// // C function void glDrawBuffers ( GLsizei n, const GLenum *bufs )
//
// def glDrawBuffers: Unit
// n:Int,
// bufs:Array[Int],
// offset:Int
// );

  // C function void glDrawBuffers ( GLsizei n, const GLenum *bufs )

  def glDrawBuffers (n:Int, bufs:java.nio.IntBuffer): Unit

// // C function void glUniformMatrix2x3fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )
//
// def glUniformMatrix2x3fv: Unit
// location:Int,
// count:Int,
// transpose:Boolean,
// ]:Float value,
// offset:Int
// );

  // C function void glUniformMatrix2x3fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

  def glUniformMatrix2x3fv (location:Int, count:Int, transpose:Boolean, value:java.nio.FloatBuffer): Unit

// // C function void glUniformMatrix3x2fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )
//
// def glUniformMatrix3x2fv: Unit
// location:Int,
// count:Int,
// transpose:Boolean,
// ]:Float value,
// offset:Int
// );

  // C function void glUniformMatrix3x2fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

  def glUniformMatrix3x2fv (location:Int, count:Int, transpose:Boolean, value:java.nio.FloatBuffer): Unit

// // C function void glUniformMatrix2x4fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )
//
// def glUniformMatrix2x4fv: Unit
// location:Int,
// count:Int,
// transpose:Boolean,
// ]:Float value,
// offset:Int
// );

  // C function void glUniformMatrix2x4fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

  def glUniformMatrix2x4fv (location:Int, count:Int, transpose:Boolean, value:java.nio.FloatBuffer): Unit

// // C function void glUniformMatrix4x2fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )
//
// def glUniformMatrix4x2fv: Unit
// location:Int,
// count:Int,
// transpose:Boolean,
// ]:Float value,
// offset:Int
// );

  // C function void glUniformMatrix4x2fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

  def glUniformMatrix4x2fv (location:Int, count:Int, transpose:Boolean, value:java.nio.FloatBuffer): Unit

// // C function void glUniformMatrix3x4fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )
//
// def glUniformMatrix3x4fv: Unit
// location:Int,
// count:Int,
// transpose:Boolean,
// ]:Float value,
// offset:Int
// );

  // C function void glUniformMatrix3x4fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

  def glUniformMatrix3x4fv (location:Int, count:Int, transpose:Boolean, value:java.nio.FloatBuffer): Unit

// // C function void glUniformMatrix4x3fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )
//
// def glUniformMatrix4x3fv: Unit
// location:Int,
// count:Int,
// transpose:Boolean,
// ]:Float value,
// offset:Int
// );

  // C function void glUniformMatrix4x3fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

  def glUniformMatrix4x3fv (location:Int, count:Int, transpose:Boolean, value:java.nio.FloatBuffer): Unit

  // C function void glBlitFramebuffer ( GLint srcX0, GLint srcY0, GLint srcX1, GLint srcY1, GLint dstX0, GLint dstY0, GLint
// dstX1, GLint dstY1, GLbitfield mask, GLenum filter )

  def glBlitFramebuffer (srcX0:Int, srcY0:Int, srcX1:Int, srcY1:Int, dstX0:Int, dstY0:Int, dstX1:Int, dstY1:Int, mask:Int, filter:Int): Unit

  // C function void glRenderbufferStorageMultisample ( GLenum target, GLsizei samples, GLenum internalformat, GLsizei width,
// GLsizei height )

  def glRenderbufferStorageMultisample (target:Int, samples:Int, internalformat:Int, width:Int, height:Int): Unit

  // C function void glFramebufferTextureLayer ( GLenum target, GLenum attachment, GLuint texture, GLint level, GLint layer )

  def glFramebufferTextureLayer (target:Int, attachment:Int, texture:Int, level:Int, layer:Int): Unit

// // C function GLvoid * glMapBufferRange ( GLenum target, GLintptr offset, GLsizeiptr length, GLbitfield access )
//
// public glMapBufferRange:java.nio.Buffer(
// target:Int,
// offset:Int,
// length:Int,
// access:Int
// );

  // C function void glFlushMappedBufferRange ( GLenum target, GLintptr offset, GLsizeiptr length )

  def glFlushMappedBufferRange (target:Int, offset:Int, length:Int): Unit

  // C function void glBindVertexArray ( GLuint array )

  def glBindVertexArray (array:Int): Unit

  // C function void glDeleteVertexArrays ( GLsizei n, const GLuint *arrays )

  def glDeleteVertexArrays (n:Int, arrays:Array[Int], offset:Int): Unit

  // C function void glDeleteVertexArrays ( GLsizei n, const GLuint *arrays )

  def glDeleteVertexArrays (n:Int, arrays:java.nio.IntBuffer): Unit

  // C function void glGenVertexArrays ( GLsizei n, GLuint *arrays )

  def glGenVertexArrays (n:Int, arrays:Array[Int], offset:Int): Unit

  // C function void glGenVertexArrays ( GLsizei n, GLuint *arrays )

  def glGenVertexArrays (n:Int, arrays:java.nio.IntBuffer): Unit

  // C function GLboolean glIsVertexArray ( GLuint array )

  def glIsVertexArray (array:Int): Boolean

//
// // C function void glGetIntegeri_v ( GLenum target, GLuint index, GLint *data )
//
// def glGetIntegeri_v: Unit
// target:Int,
// index:Int,
// data:Array[Int],
// offset:Int
// );
//
// // C function void glGetIntegeri_v ( GLenum target, GLuint index, GLint *data )
//
// def glGetIntegeri_v: Unit
// target:Int,
// index:Int,
// data:java.nio.IntBuffer
// );

  // C function void glBeginTransformFeedback ( GLenum primitiveMode )

  def glBeginTransformFeedback (primitiveMode:Int): Unit

  // C function void glEndTransformFeedback ( void )

  def glEndTransformFeedback (): Unit

  // C function void glBindBufferRange ( GLenum target, GLuint index, GLuint buffer, GLintptr offset, GLsizeiptr size )

  def glBindBufferRange (target:Int, index:Int, buffer:Int, offset:Int, size:Int): Unit

  // C function void glBindBufferBase ( GLenum target, GLuint index, GLuint buffer )

  def glBindBufferBase (target:Int, index:Int, buffer:Int): Unit

  // C function void glTransformFeedbackVaryings ( GLuint program, GLsizei count, const GLchar *varyings, GLenum bufferMode )

  def glTransformFeedbackVaryings (program:Int, varyings:Array[String], bufferMode:Int): Unit

// // C function void glGetTransformFeedbackVarying ( GLuint program, GLuint index, GLsizei bufSize, GLsizei *length, GLint *size,
// GLenum *`type`, GLchar *name )
//
// def glGetTransformFeedbackVarying: Unit
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
// def glGetTransformFeedbackVarying: Unit
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

  def glVertexAttribIPointer (index:Int, size:Int, `type`:Int, stride:Int, offset:Int): Unit

// // C function void glGetVertexAttribIiv ( GLuint index, GLenum pname, GLint *params )
//
// def glGetVertexAttribIiv: Unit
// index:Int,
// pname:Int,
// params:Array[Int],
// offset:Int
// );

  // C function void glGetVertexAttribIiv ( GLuint index, GLenum pname, GLint *params )

  def glGetVertexAttribIiv (index:Int, pname:Int, params:java.nio.IntBuffer): Unit

// // C function void glGetVertexAttribIuiv ( GLuint index, GLenum pname, GLuint *params )
//
// def glGetVertexAttribIuiv: Unit
// index:Int,
// pname:Int,
// params:Array[Int],
// offset:Int
// );

  // C function void glGetVertexAttribIuiv ( GLuint index, GLenum pname, GLuint *params )

  def glGetVertexAttribIuiv (index:Int, pname:Int, params:java.nio.IntBuffer): Unit

  // C function void glVertexAttribI4i ( GLuint index, GLint x, GLint y, GLint z, GLint w )

  def glVertexAttribI4i (index:Int, x:Int, y:Int, z:Int, w:Int): Unit

  // C function void glVertexAttribI4ui ( GLuint index, GLuint x, GLuint y, GLuint z, GLuint w )

  def glVertexAttribI4ui (index:Int, x:Int, y:Int, z:Int, w:Int): Unit

// // C function void glVertexAttribI4iv ( GLuint index, const GLint *v )
//
// def glVertexAttribI4iv: Unit
// index:Int,
// v:Array[Int],
// offset:Int
// );
//
// // C function void glVertexAttribI4iv ( GLuint index, const GLint *v )
//
// def glVertexAttribI4iv: Unit
// index:Int,
// v:java.nio.IntBuffer
// );
//
// // C function void glVertexAttribI4uiv ( GLuint index, const GLuint *v )
//
// def glVertexAttribI4uiv: Unit
// index:Int,
// v:Array[Int],
// offset:Int
// );
//
// // C function void glVertexAttribI4uiv ( GLuint index, const GLuint *v )
//
// def glVertexAttribI4uiv: Unit
// index:Int,
// v:java.nio.IntBuffer
// );
//
// // C function void glGetUniformuiv ( GLuint program, GLint location, GLuint *params )
//
// def glGetUniformuiv: Unit
// program:Int,
// location:Int,
// params:Array[Int],
// offset:Int
// );

  // C function void glGetUniformuiv ( GLuint program, GLint location, GLuint *params )

  def glGetUniformuiv (program:Int, location:Int, params:java.nio.IntBuffer): Unit

  // C function GLint glGetFragDataLocation ( GLuint program, const GLchar *name )

  def glGetFragDataLocation (program:Int, name:String): Int

// // C function void glUniform1ui ( GLint location, GLuint v0 )
//
// def glUniform1ui: Unit
// location:Int,
// v0:Int
// );
//
// // C function void glUniform2ui ( GLint location, GLuint v0, GLuint v1 )
//
// def glUniform2ui: Unit
// location:Int,
// v0:Int,
// v1:Int
// );
//
// // C function void glUniform3ui ( GLint location, GLuint v0, GLuint v1, GLuint v2 )
//
// def glUniform3ui: Unit
// location:Int,
// v0:Int,
// v1:Int,
// v2:Int
// );
//
// // C function void glUniform4ui ( GLint location, GLuint v0, GLuint v1, GLuint v2, GLuint v3 )
//
// def glUniform4ui: Unit
// location:Int,
// v0:Int,
// v1:Int,
// v2:Int,
// v3:Int
// );
//
// // C function void glUniform1uiv ( GLint location, GLsizei count, const GLuint *value )
//
// def glUniform1uiv: Unit
// location:Int,
// count:Int,
// value:Array[Int],
// offset:Int
// );

  // C function void glUniform1uiv ( GLint location, GLsizei count, const GLuint *value )

  def glUniform1uiv (location:Int, count:Int, value:java.nio.IntBuffer): Unit

// // C function void glUniform2uiv ( GLint location, GLsizei count, const GLuint *value )
//
// def glUniform2uiv: Unit
// location:Int,
// count:Int,
// value:Array[Int],
// offset:Int
// );
//
// // C function void glUniform2uiv ( GLint location, GLsizei count, const GLuint *value )
//
// def glUniform2uiv: Unit
// location:Int,
// count:Int,
// value:java.nio.IntBuffer
// );
//
// // C function void glUniform3uiv ( GLint location, GLsizei count, const GLuint *value )
//
// def glUniform3uiv: Unit
// location:Int,
// count:Int,
// value:Array[Int],
// offset:Int
// );

  // C function void glUniform3uiv ( GLint location, GLsizei count, const GLuint *value )

  def glUniform3uiv (location:Int, count:Int, value:java.nio.IntBuffer): Unit

// // C function void glUniform4uiv ( GLint location, GLsizei count, const GLuint *value )
//
// def glUniform4uiv: Unit
// location:Int,
// count:Int,
// value:Array[Int],
// offset:Int
// );

  // C function void glUniform4uiv ( GLint location, GLsizei count, const GLuint *value )

  def glUniform4uiv (location:Int, count:Int, value:java.nio.IntBuffer): Unit

// // C function void glClearBufferiv ( GLenum buffer, GLint drawbuffer, const GLint *value )
//
// def glClearBufferiv: Unit
// buffer:Int,
// drawbuffer:Int,
// value:Array[Int],
// offset:Int
// );

  // C function void glClearBufferiv ( GLenum buffer, GLint drawbuffer, const GLint *value )

  def glClearBufferiv (buffer:Int, drawbuffer:Int, value:java.nio.IntBuffer): Unit

// // C function void glClearBufferuiv ( GLenum buffer, GLint drawbuffer, const GLuint *value )
//
// def glClearBufferuiv: Unit
// buffer:Int,
// drawbuffer:Int,
// value:Array[Int],
// offset:Int
// );

  // C function void glClearBufferuiv ( GLenum buffer, GLint drawbuffer, const GLuint *value )

  def glClearBufferuiv (buffer:Int, drawbuffer:Int, value:java.nio.IntBuffer): Unit

// // C function void glClearBufferfv ( GLenum buffer, GLint drawbuffer, const GLfloat *value )
//
// def glClearBufferfv: Unit
// buffer:Int,
// drawbuffer:Int,
// ]:Float value,
// offset:Int
// );

  // C function void glClearBufferfv ( GLenum buffer, GLint drawbuffer, const GLfloat *value )

  def glClearBufferfv (buffer:Int, drawbuffer:Int, value:java.nio.FloatBuffer): Unit

  // C function void glClearBufferfi ( GLenum buffer, GLint drawbuffer, GLfloat depth, GLint stencil )

  def glClearBufferfi (buffer:Int, drawbuffer:Int, depth:Float, stencil:Int): Unit

  // C function const GLubyte * glGetStringi ( GLenum name, GLuint index )

  def glGetStringi (name:Int, index:Int): String

  // C function void glCopyBufferSubData ( GLenum readTarget, GLenum writeTarget, GLintptr readOffset, GLintptr writeOffset,
// GLsizeiptr size )

  def glCopyBufferSubData (readTarget:Int, writeTarget:Int, readOffset:Int, writeOffset:Int, size:Int): Unit

// // C function void glGetUniformIndices ( GLuint program, GLsizei uniformCount, const GLchar *const *uniformNames, GLuint
// *uniformIndices )
//
// def glGetUniformIndices: Unit
// program:Int,
// uniformNames:Array[String],
// uniformIndices:Array[Int],
// uniformIndicesOffset:Int
// );

  // C function void glGetUniformIndices ( GLuint program, GLsizei uniformCount, const GLchar *const *uniformNames, GLuint
// *uniformIndices )

  def glGetUniformIndices (program:Int, uniformNames:Array[String], uniformIndices:java.nio.IntBuffer): Unit

// // C function void glGetActiveUniformsiv ( GLuint program, GLsizei uniformCount, const GLuint *uniformIndices, GLenum pname,
// GLint *params )
//
// def glGetActiveUniformsiv: Unit
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

  def glGetActiveUniformsiv (program:Int, uniformCount:Int, uniformIndices:java.nio.IntBuffer, pname:Int, params:java.nio.IntBuffer): Unit

  // C function GLuint glGetUniformBlockIndex ( GLuint program, const GLchar *uniformBlockName )

  def glGetUniformBlockIndex (program:Int, uniformBlockName:String): Int

// // C function void glGetActiveUniformBlockiv ( GLuint program, GLuint uniformBlockIndex, GLenum pname, GLint *params )
//
// def glGetActiveUniformBlockiv: Unit
// program:Int,
// uniformBlockIndex:Int,
// pname:Int,
// params:Array[Int],
// offset:Int
// );

  // C function void glGetActiveUniformBlockiv ( GLuint program, GLuint uniformBlockIndex, GLenum pname, GLint *params )

  def glGetActiveUniformBlockiv (program:Int, uniformBlockIndex:Int, pname:Int, params:java.nio.IntBuffer): Unit

// // C function void glGetActiveUniformBlockName ( GLuint program, GLuint uniformBlockIndex, GLsizei bufSize, GLsizei *length,
// GLchar *uniformBlockName )
//
// def glGetActiveUniformBlockName: Unit
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

  def glGetActiveUniformBlockName (program:Int, uniformBlockIndex:Int, length:java.nio.Buffer, uniformBlockName:java.nio.Buffer): Unit

  // C function void glGetActiveUniformBlockName ( GLuint program, GLuint uniformBlockIndex, GLsizei bufSize, GLsizei *length,
// GLchar *uniformBlockName )

  def glGetActiveUniformBlockName (program:Int, uniformBlockIndex:Int): String

  // C function void glUniformBlockBinding ( GLuint program, GLuint uniformBlockIndex, GLuint uniformBlockBinding )

  def glUniformBlockBinding (program:Int, uniformBlockIndex:Int, uniformBlockBinding:Int): Unit

  // C function void glDrawArraysInstanced ( GLenum mode, GLint first, GLsizei count, GLsizei instanceCount )

  def glDrawArraysInstanced (mode:Int, first:Int, count:Int, instanceCount:Int): Unit

// // C function void glDrawElementsInstanced ( GLenum mode, GLsizei count, GLenum `type`, const GLvoid *indices, GLsizei
// instanceCount )
//
// def glDrawElementsInstanced: Unit
// mode:Int,
// count:Int,
// `type`:Int,
// indices:java.nio.Buffer,
// instanceCount:Int
// );

  // C function void glDrawElementsInstanced ( GLenum mode, GLsizei count, GLenum `type`, const GLvoid *indices, GLsizei
// instanceCount )

  def glDrawElementsInstanced (mode:Int, count:Int, `type`:Int, indicesOffset:Int, instanceCount:Int): Unit

// // C function GLsync glFenceSync ( GLenum condition, GLbitfield flags )
//
// public long glFenceSync(
// condition:Int,
// flags:Int
// );
//
// // C function GLboolean glIsSync ( GLsync sync )
//
// def glIsSync: Boolean
// long sync
// );
//
// // C function void glDeleteSync ( GLsync sync )
//
// def glDeleteSync: Unit
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
// def glWaitSync: Unit
// long sync,
// flags:Int,
// long timeout
// );

// // C function void glGetInteger64v ( GLenum pname, GLint64 *params )
//
// def glGetInteger64v: Unit
// pname:Int,
// long[] params,
// offset:Int
// );

  // C function void glGetInteger64v ( GLenum pname, GLint64 *params )

  def glGetInteger64v (pname:Int, params:java.nio.LongBuffer): Unit

// // C function void glGetSynciv ( GLsync sync, GLenum pname, GLsizei bufSize, GLsizei *length, GLint *values )
//
// def glGetSynciv: Unit
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
// def glGetSynciv: Unit
// long sync,
// pname:Int,
// bufSize:Int,
// length:java.nio.IntBuffer,
// values:java.nio.IntBuffer
// );
//
// // C function void glGetInteger64i_v ( GLenum target, GLuint index, GLint64 *data )
//
// def glGetInteger64i_v: Unit
// target:Int,
// index:Int,
// long[] data,
// offset:Int
// );
//
// // C function void glGetInteger64i_v ( GLenum target, GLuint index, GLint64 *data )
//
// def glGetInteger64i_v: Unit
// target:Int,
// index:Int,
// data:java.nio.LongBuffer
// );
//
// // C function void glGetBufferParameteri64v ( GLenum target, GLenum pname, GLint64 *params )
//
// def glGetBufferParameteri64v: Unit
// target:Int,
// pname:Int,
// long[] params,
// offset:Int
// );

  // C function void glGetBufferParameteri64v ( GLenum target, GLenum pname, GLint64 *params )

  def glGetBufferParameteri64v (target:Int, pname:Int, params:java.nio.LongBuffer): Unit

  // C function void glGenSamplers ( GLsizei count, GLuint *samplers )

  def glGenSamplers (count:Int, samplers:Array[Int], offset:Int): Unit

  // C function void glGenSamplers ( GLsizei count, GLuint *samplers )

  def glGenSamplers (count:Int, samplers:java.nio.IntBuffer): Unit

  // C function void glDeleteSamplers ( GLsizei count, const GLuint *samplers )

  def glDeleteSamplers (count:Int, samplers:Array[Int], offset:Int): Unit

  // C function void glDeleteSamplers ( GLsizei count, const GLuint *samplers )

  def glDeleteSamplers (count:Int, samplers:java.nio.IntBuffer): Unit

  // C function GLboolean glIsSampler ( GLuint sampler )

  def glIsSampler (sampler:Int): Boolean

  // C function void glBindSampler ( GLuint unit, GLuint sampler )

  def glBindSampler (unit:Int, sampler:Int): Unit

  // C function void glSamplerParameteri ( GLuint sampler, GLenum pname, GLint param )

  def glSamplerParameteri (sampler:Int, pname:Int, param:Int): Unit

// // C function void glSamplerParameteriv ( GLuint sampler, GLenum pname, const GLint *param )
//
// def glSamplerParameteriv: Unit
// sampler:Int,
// pname:Int,
// param:Array[Int],
// offset:Int
// );

  // C function void glSamplerParameteriv ( GLuint sampler, GLenum pname, const GLint *param )

  def glSamplerParameteriv (sampler:Int, pname:Int, param:java.nio.IntBuffer): Unit

  // C function void glSamplerParameterf ( GLuint sampler, GLenum pname, GLfloat param )

  def glSamplerParameterf (sampler:Int, pname:Int, param:Float): Unit

// // C function void glSamplerParameterfv ( GLuint sampler, GLenum pname, const GLfloat *param )
//
// def glSamplerParameterfv: Unit
// sampler:Int,
// pname:Int,
// ]:Float param,
// offset:Int
// );

  // C function void glSamplerParameterfv ( GLuint sampler, GLenum pname, const GLfloat *param )

  def glSamplerParameterfv (sampler:Int, pname:Int, param:java.nio.FloatBuffer): Unit

// // C function void glGetSamplerParameteriv ( GLuint sampler, GLenum pname, GLint *params )
//
// def glGetSamplerParameteriv: Unit
// sampler:Int,
// pname:Int,
// params:Array[Int],
// offset:Int
// );

  // C function void glGetSamplerParameteriv ( GLuint sampler, GLenum pname, GLint *params )

  def glGetSamplerParameteriv (sampler:Int, pname:Int, params:java.nio.IntBuffer): Unit

// // C function void glGetSamplerParameterfv ( GLuint sampler, GLenum pname, GLfloat *params )
//
// def glGetSamplerParameterfv: Unit
// sampler:Int,
// pname:Int,
// ]:Float params,
// offset:Int
// );

  // C function void glGetSamplerParameterfv ( GLuint sampler, GLenum pname, GLfloat *params )

  def glGetSamplerParameterfv (sampler:Int, pname:Int, params:java.nio.FloatBuffer): Unit

  // C function void glVertexAttribDivisor ( GLuint index, GLuint divisor )

  def glVertexAttribDivisor (index:Int, divisor:Int): Unit

  // C function void glBindTransformFeedback ( GLenum target, GLuint id )

  def glBindTransformFeedback (target:Int, id:Int): Unit

  // C function void glDeleteTransformFeedbacks ( GLsizei n, const GLuint *ids )

  def glDeleteTransformFeedbacks (n:Int, ids:Array[Int], offset:Int): Unit

  // C function void glDeleteTransformFeedbacks ( GLsizei n, const GLuint *ids )

  def glDeleteTransformFeedbacks (n:Int, ids:java.nio.IntBuffer): Unit

  // C function void glGenTransformFeedbacks ( GLsizei n, GLuint *ids )

  def glGenTransformFeedbacks (n:Int, ids:Array[Int], offset:Int): Unit

  // C function void glGenTransformFeedbacks ( GLsizei n, GLuint *ids )

  def glGenTransformFeedbacks (n:Int, ids:java.nio.IntBuffer): Unit

  // C function GLboolean glIsTransformFeedback ( GLuint id )

  def glIsTransformFeedback (id:Int): Boolean

  // C function void glPauseTransformFeedback ( void )

  def glPauseTransformFeedback (): Unit

  // C function void glResumeTransformFeedback ( void )

  def glResumeTransformFeedback (): Unit

// // C function void glGetProgramBinary ( GLuint program, GLsizei bufSize, GLsizei *length, GLenum *binaryFormat, GLvoid *binary
// )
//
// def glGetProgramBinary: Unit
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
// def glGetProgramBinary: Unit
// program:Int,
// bufSize:Int,
// length:java.nio.IntBuffer,
// binaryFormat:java.nio.IntBuffer,
// binary:java.nio.Buffer
// );
//
// // C function void glProgramBinary ( GLuint program, GLenum binaryFormat, const GLvoid *binary, GLsizei length )
//
// def glProgramBinary: Unit
// program:Int,
// binaryFormat:Int,
// binary:java.nio.Buffer,
// length:Int
// );

  // C function void glProgramParameteri ( GLuint program, GLenum pname, GLint value )

  def glProgramParameteri (program:Int, pname:Int, value:Int): Unit

// // C function void glInvalidateFramebuffer ( GLenum target, GLsizei numAttachments, const GLenum *attachments )
//
// def glInvalidateFramebuffer: Unit
// target:Int,
// numAttachments:Int,
// attachments:Array[Int],
// offset:Int
// );

  // C function void glInvalidateFramebuffer ( GLenum target, GLsizei numAttachments, const GLenum *attachments )

  def glInvalidateFramebuffer (target:Int, numAttachments:Int, attachments:java.nio.IntBuffer): Unit

// // C function void glInvalidateSubFramebuffer ( GLenum target, GLsizei numAttachments, const GLenum *attachments, GLint x,
// GLint y, GLsizei width, GLsizei height )
//
// def glInvalidateSubFramebuffer: Unit
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

  def glInvalidateSubFramebuffer (target:Int, numAttachments:Int, attachments:java.nio.IntBuffer, x:Int, y:Int, width:Int, height:Int): Unit

// // C function void glTexStorage2D ( GLenum target, GLsizei levels, GLenum internalformat, GLsizei width, GLsizei height )
//
// def glTexStorage2D: Unit
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
// def glTexStorage3D: Unit
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
// def glGetInternalformativ: Unit
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
// def glGetInternalformativ: Unit
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