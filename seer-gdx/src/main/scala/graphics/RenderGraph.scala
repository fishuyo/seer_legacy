
package com.fishuyo.seer
package graphics
import spatial._

import scala.collection.mutable.ListBuffer
//import javax.media.opengl._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
// import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.graphics.{Texture => GdxTexture}



object RenderGraph {
  val roots = ListBuffer[RenderNode]()
  // var root:RenderNode = new BasicNode
  // root.scene = Scene
  // root.camera = Camera
  // roots += root

  def addNode(n:RenderNode){
    roots += n
    n.renderer.scene.init()
  }
  def prependNode(n:RenderNode){
    roots.prepend(n)
    n.renderer.scene.init()
  }

  def removeNode(n:RenderNode){
    //TODO cleanup inputs outputs
    roots -= n
  }

  def animate(dt:Float){
    roots.foreach( (n) => animateChildren(n,dt) )
  }
  def animateChildren(node:RenderNode, dt:Float){
   node.animate(dt)
   node.outputs.foreach( (n) => if(n != node) animateChildren(n,dt) )
  }

  def resize(vp:Viewport){
    roots.foreach( (n) => resizeChildren(n,vp))
  }
  def resizeChildren(node:RenderNode, vp:Viewport){
   node.resize(vp)
   node.outputs.foreach( (n) => if(n != node) resizeChildren(n,vp) )
  }

  def render(){
    roots.foreach( (n) => renderChildren(n) )
  }
  def renderChildren(node:RenderNode){
   node.render()
   node.outputs.foreach( (n) => if( n != node) renderChildren(n) )
  }

  // def leaves() = {
  // }
}


/**
  * RenderNode is a node in the RenderGraph, which uses framebuffer targets
  * to send to outputs in the graph, and also binds them as textures for inputs
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

object ScreenNode extends RenderNode {
  // clear = false
  renderer.scene.push(Plane())
  renderer.shader = Shader.load(DefaultShaders.texture)
}

class TextureNode(var texture:GdxTexture) extends RenderNode {
  override def bindBuffer(i:Int) = texture.bind(i)  
  override def render(){}
}

class CompositeNode(var blend0:Float=0.5, var blend1:Float=0.5) extends RenderNode {
  renderer.scene.push(Plane())
  renderer.shader = Shader.load(DefaultShaders.composite)
  override def render(){
    renderer.shader.uniforms("u_blend0") = blend0
    renderer.shader.uniforms("u_blend1") = blend1
    super.render()
  }
}

class FeedbackNode(b0:Float=0.8f, b1:Float=0.2f) extends CompositeNode(b0,b1) {
  renderer.clear = false
  this.outputTo(this)
}

// class OutlineNode extends RenderNode {
//   val quad = Primitive2D.quad
//   override def render(){
//     // inputs.foreach( _.buffer.get.getColorBufferTexture().bind(0) )

//     SceneGraph.root.buffer.get.getColorBufferTexture().bind(0)
//     Shader("secondPass").begin()
//     Shader().setUniformi("u_texture0", 0);
//     Shader().setUniformMatrix("u_projectionViewMatrix", new Matrix4())
//     //Shader().setUniformMatrix("u_modelViewMatrix", new Matrix4())
//     // Shader().setUniformMatrix("u_normalMatrix", modelViewMatrix.toNormalMatrix())
//     // scene.draw2()
//     // if( day.x == 1f) Shader("secondPass").setUniformf("u_depth", 0f)
//     // else Shader("secondPass").setUniformf("u_depth", 1f)

//     quad.render(Shader(), GL20.GL_TRIANGLES)
    
//     Shader().end();
//   }
// }

