
package com.fishuyo.seer
package graphics

import scala.collection.mutable.ListBuffer

/**
  * RenderNode is a node in the RenderGraph
  * uses framebuffer targets, a nodes output framebuffer
  * is bound to corresponding input texture of next node
  */
class RenderNode(val renderer:Renderer = new Renderer()) {
  
  val inputs = new ListBuffer[RenderNode]
  val outputs = new ListBuffer[RenderNode]
  // val nodes = new ListBuffer[RenderNode]

  var buffer:Option[FrameBuffer] = None

  def createBuffer(){
    if(buffer.isEmpty) buffer = Some(FrameBuffer(renderer.viewport.w.toInt, renderer.viewport.h.toInt))
  }

  def bindBuffer(i:Int) = buffer.get.getColorBufferTexture().bind(i)

  def bindTarget() = if( buffer.isDefined ) buffer.get.begin()
  def unbindTarget() = if( buffer.isDefined ) buffer.get.end()

  def resize(vp:Viewport){
    renderer.resize(vp)

    if(buffer.isDefined){
      buffer.get.dispose
      buffer = Some(FrameBuffer(vp.w.toInt,vp.h.toInt))
    }
  }

  def addInput(node:RenderNode){
    inputs += node
  }

  def outputTo(node:RenderNode){
    createBuffer()
    outputs += node
    node.addInput(this)
  }

  def animate(dt:Float){
    renderer.animate(dt)
  }

  def render(){
    bindTarget()

    inputs.zipWithIndex.foreach { case(input,idx) => 
      input.bindBuffer(idx) 
      renderer.shader.uniforms("u_texture"+idx) = idx
    }

    renderer.render()

    unbindTarget()
  }
}

// class BasicNode extends RenderNode

/**
  * ScreenNode renders to screen
  */
object ScreenNode extends ScreenNode
class ScreenNode extends RenderNode {
  // clear = false
  renderer.scene.push(Plane())
  renderer.shader = Shader.load(DefaultShaders.texture)
}

/**
  * TextureNode just a texture to send to next node
  */
// class TextureNode(var texture:GdxTexture) extends RenderNode {
//   override def bindBuffer(i:Int) = texture.bind(i)  
//   override def render(){}
// }

/**
  * CompositeNode blends two nodes output simply
  */
class CompositeNode(var blend0:Float=0.5f, var blend1:Float=0.5f) extends RenderNode {
  var mode = 0
  renderer.scene.push(Plane())
  renderer.shader = Shader.load(DefaultShaders.composite)
  override def render(){
    renderer.shader.uniforms("u_blend0") = blend0
    renderer.shader.uniforms("u_blend1") = blend1
    renderer.shader.uniforms("u_mode") = mode
    super.render()
  }
}

class Composite3Node(var blend0:Float=0.33f, var blend1:Float=0.33f, var blend2:Float=0.33f) extends RenderNode {
  var mode = 0

  renderer.scene.push(Plane())
  renderer.shader = Shader.load(DefaultShaders.composite._1,
    """
      #ifdef GL_ES
       precision mediump float;
      #endif

      uniform sampler2D u_texture0;
      uniform sampler2D u_texture1;
      uniform sampler2D u_texture2;

      uniform float u_blend0;
      uniform float u_blend1;
      uniform float u_blend2;

      uniform int mode;

      varying vec2 v_texCoords;

      void main(){

        // pull everything we want from the textures
        vec4 color0 = texture2D(u_texture0, v_texCoords) * u_blend0;
        vec4 color1 = texture2D(u_texture1, v_texCoords) * u_blend1;
        vec4 color2 = texture2D(u_texture2, v_texCoords) * u_blend2;

        if( mode == 0){
          gl_FragColor = color0 + color1 + color2;
        }else {
          gl_FragColor = color0 * color1 * color2;
        }
      }
    """
  )
  override def render(){
    renderer.shader.uniforms("u_blend0") = blend0
    renderer.shader.uniforms("u_blend1") = blend1
    renderer.shader.uniforms("u_blend2") = blend2
    renderer.shader.uniforms("mode") = mode
    super.render()
  }
}

/**
  * FeedbackNode uses the CompositeNode but feeds into itself creating a motion blur effect
  */
class FeedbackNode(b0:Float=0.8f, b1:Float=0.2f) extends CompositeNode(b0,b1) {
  renderer.clear = false
  this.outputTo(this)
}



/**
  * BackBufferNode keeps previous render frame for reuse
  * Also uses FloatFrameBuffer
  */
class BackBufferNode extends RenderNode {

  var isTarget2 = true
  var buffer1:Option[FloatFrameBuffer] = None
  var buffer2:Option[FloatFrameBuffer] = None

  outputTo(this)

  renderer.scene.push(Plane())

  override def createBuffer(){
    if(buffer1.isEmpty){ 
      buffer1 = Some(new FloatFrameBuffer(renderer.viewport.w.toInt, renderer.viewport.h.toInt))
      buffer2 = Some(new FloatFrameBuffer(renderer.viewport.w.toInt, renderer.viewport.h.toInt))
    }
  }
  override def bindBuffer(i:Int) = {
    if( isTarget2 ) buffer1.get.getColorBufferTexture().bind(i)
    else buffer2.get.getColorBufferTexture().bind(i)
  }
  override def resize(vp:Viewport){
    renderer.resize(vp)
  
    if(buffer1.isDefined){
      buffer1.get.dispose
      buffer1 = Some(new FloatFrameBuffer(vp.w.toInt,vp.h.toInt))
      buffer2.get.dispose
      buffer2 = Some(new FloatFrameBuffer(vp.w.toInt,vp.h.toInt))
    }
  }

  override def bindTarget(){
    if( buffer1.isDefined ){
      
      if(isTarget2) buffer2.get.begin()
      else buffer1.get.begin()

      // if( clear ) Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
      // else Gdx.gl.glClear( GL20.GL_DEPTH_BUFFER_BIT)

      // nodes.foreach(_.render()) //hacky
    }
  }
  override def unbindTarget(){
    if(isTarget2) buffer2.get.end()
    else buffer1.get.end()
    isTarget2 = !isTarget2
  }

}


class ColorizeNode extends RenderNode {
  var color1 = RGBA(0,0,0,0)
  var color2 = RGBA(1,0,0,0)
  var color3 = RGBA(0,1,0,0)
  var color4 = RGBA(0,0,1,0)
  var color5 = RGBA(1,1,1,1)

  renderer.scene.push(Plane())

  renderer.shader = Shader.load(
    """
    attribute vec4 a_position;
    attribute vec4 a_normal;
    attribute vec2 a_texCoord0;
    attribute vec4 a_color;

    uniform mat4 u_projectionViewMatrix;

    varying vec2 v_uv;
    varying vec3 v_normal;

    void main() {
      gl_Position = u_projectionViewMatrix * a_position;
      v_normal = a_normal.xyz;
      v_uv = a_texCoord0;
      // v_color = a_color;
      // v_pos = a_position.xyz;
    }
    """,

    """
    #ifdef GL_ES
        precision mediump float;
    #endif

    varying vec2 v_uv;

    uniform sampler2D u_texture0;

    uniform vec4 color1;
    uniform vec4 color2;
    uniform vec4 color3;
    uniform vec4 color4;
    uniform vec4 color5;

    void main(){
      float value = texture2D(u_texture0, v_uv).g;
      //int step = int(floor(value));
      //float a = fract(value);
      float a;
      vec3 col;
      
      if(value <= color1.a)
          col = color1.rgb;
      if(value > color1.a && value <= color2.a){
          a = (value - color1.a)/(color2.a - color1.a);
          col = mix(color1.rgb, color2.rgb, a);
      }
      if(value > color2.a && value <= color3.a){
          a = (value - color2.a)/(color3.a - color2.a);
          col = mix(color2.rgb, color3.rgb, a);
      }
      if(value > color3.a && value <= color4.a){
          a = (value - color3.a)/(color4.a - color3.a);
          col = mix(color3.rgb, color4.rgb, a);
      }
      if(value > color4.a && value <= color5.a){
          a = (value - color4.a)/(color5.a - color4.a);
          col = mix(color4.rgb, color5.rgb, a);
      }
      if(value > color5.a)
          col = color5.rgb;
      
      gl_FragColor = vec4(col.r, col.g, col.b, 1.0);
    }
    """
  )
  
  override def render(){
    renderer.shader.uniforms("color1") = color1
    renderer.shader.uniforms("color2") = color2
    renderer.shader.uniforms("color3") = color3
    renderer.shader.uniforms("color4") = color4
    renderer.shader.uniforms("color5") = color5
    super.render()
  }
}


class BlurNode extends RenderNode {

  var size = 1f/512f
  var intensity = 0.35f

  renderer.scene.push(Plane())

  renderer.shader = Shader.load(
    """
    attribute vec4 a_position;
    attribute vec4 a_normal;
    attribute vec2 a_texCoord0;
    attribute vec4 a_color;

    uniform mat4 u_projectionViewMatrix;

    varying vec2 v_uv;
    varying vec3 v_normal;

    void main() {
      gl_Position = u_projectionViewMatrix * a_position;
      v_normal = a_normal.xyz;
      v_uv = a_texCoord0;
      // v_color = a_color;
      // v_pos = a_position.xyz;
    }
    """,

    """
    #ifdef GL_ES
        precision mediump float;
    #endif

    varying vec2 v_uv;

    uniform sampler2D u_texture0;
    uniform float blurSize;
    uniform float intensity;

    void main(){
      // const float blurSize = 1.0/512.0;
      // const float intensity = 0.35;

      vec4 sum = vec4(0);
      vec2 texcoord = v_uv;

      // blur in x
      // take nine samples, with the distance blurSize between them
      sum += texture2D(u_texture0, vec2(texcoord.x - 4.0*blurSize, texcoord.y)) * 0.05;
      sum += texture2D(u_texture0, vec2(texcoord.x - 3.0*blurSize, texcoord.y)) * 0.09;
      sum += texture2D(u_texture0, vec2(texcoord.x - 2.0*blurSize, texcoord.y)) * 0.12;
      sum += texture2D(u_texture0, vec2(texcoord.x - blurSize, texcoord.y)) * 0.15;
      sum += texture2D(u_texture0, vec2(texcoord.x, texcoord.y)) * 0.16;
      sum += texture2D(u_texture0, vec2(texcoord.x + blurSize, texcoord.y)) * 0.15;
      sum += texture2D(u_texture0, vec2(texcoord.x + 2.0*blurSize, texcoord.y)) * 0.12;
      sum += texture2D(u_texture0, vec2(texcoord.x + 3.0*blurSize, texcoord.y)) * 0.09;
      sum += texture2D(u_texture0, vec2(texcoord.x + 4.0*blurSize, texcoord.y)) * 0.05;

      // blur in y (vertical)
      // take nine samples, with the distance blurSize between them
      sum += texture2D(u_texture0, vec2(texcoord.x, texcoord.y - 4.0*blurSize)) * 0.05;
      sum += texture2D(u_texture0, vec2(texcoord.x, texcoord.y - 3.0*blurSize)) * 0.09;
      sum += texture2D(u_texture0, vec2(texcoord.x, texcoord.y - 2.0*blurSize)) * 0.12;
      sum += texture2D(u_texture0, vec2(texcoord.x, texcoord.y - blurSize)) * 0.15;
      sum += texture2D(u_texture0, vec2(texcoord.x, texcoord.y)) * 0.16;
      sum += texture2D(u_texture0, vec2(texcoord.x, texcoord.y + blurSize)) * 0.15;
      sum += texture2D(u_texture0, vec2(texcoord.x, texcoord.y + 2.0*blurSize)) * 0.12;
      sum += texture2D(u_texture0, vec2(texcoord.x, texcoord.y + 3.0*blurSize)) * 0.09;
      sum += texture2D(u_texture0, vec2(texcoord.x, texcoord.y + 4.0*blurSize)) * 0.05;

      //increase blur with intensity!
      gl_FragColor = sum*intensity + texture2D(u_texture0, texcoord); 
      // if(sin(iGlobalTime) > 0.0)
        // gl_FragColor = sum * sin(iGlobalTime)+ texture2D(u_texture0, texcoord);
      // else
        // gl_FragColor = sum * -sin(iGlobalTime)+ texture2D(u_texture0, texcoord);

    }
    """
  )
  
  override def render(){
    renderer.shader.uniforms("blurSize") = size
    renderer.shader.uniforms("intensity") = intensity
    super.render()
  }
}
