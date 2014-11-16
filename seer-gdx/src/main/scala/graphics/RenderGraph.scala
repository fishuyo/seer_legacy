
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
  var roots = ListBuffer[RenderNode]()
  var root:RenderNode = new BasicNode
  root.scene = Scene
  root.camera = Camera
  roots += root

  def addNode(n:RenderNode){
    n.scene.init()
    roots += n
  }
  def prependNode(n:RenderNode){
    n.scene.init()
    roots.prepend(n)
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

  def leaves() = {

  }
}

/**
  * RenderNode is a node in the RenderGraph, which uses framebuffer targets
  * to send to outputs in the graph, and also binds them as textures for inputs
  */

class RenderNode extends Renderer {
  var clear = true
  val inputs = new ListBuffer[RenderNode]
  val outputs = new ListBuffer[RenderNode]
  val nodes = new ListBuffer[RenderNode]

  var viewport = new Viewport(0,0,800,800)
  // var scene = new Scene
  // var camera:NavCamera = new OrthographicCamera(2,2)

  var buffer:Option[FrameBuffer] = None
  // var shader = "basic"

  def createBuffer(){
    if(buffer.isEmpty) buffer = Some(FrameBuffer(viewport.w.toInt, viewport.h.toInt))
  }

  def bindBuffer(i:Int) = buffer.get.getColorBufferTexture().bind(i)

  def bindTarget(){
    if( buffer.isDefined ){
      buffer.get.begin()

      if( clear ) Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
      else Gdx.gl.glClear( GL20.GL_DEPTH_BUFFER_BIT)

      nodes.foreach(_.render()) //hacky
    }
  }
  
  def unbindTarget() = if( buffer.isDefined ) buffer.get.end()

  def resize(vp:Viewport){
    viewport = vp
    if(camera.viewportHeight == 1f){
      camera.viewportWidth = vp.aspect
    }else{
      // camera.viewportWidth = vp.w
      // camera.viewportHeight = vp.h
    }

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
    scene.animate(dt)
    camera.step(dt)
  }

  override def render(){
    bindTarget()

    try{
      shader.begin() 

      inputs.zipWithIndex.foreach( (i) => {
        // i._1.buffer.get.getColorBufferTexture().bind(i._2) 
        i._1.bindBuffer(i._2) 
        shader.uniforms("u_texture"+i._2) = i._2
      })

      MatrixStack.clear()
      setMatrices()

      Renderer() = this

      if(active){
        Shader.alpha = scene.alpha
        Shader.fade = scene.fade

        if( scene.alpha < 1f ){ //TODO depth ordering, conflicts with depth flag
          Shader.blend = true
          Gdx.gl.glEnable(GL20.GL_BLEND);
          Gdx.gl.glDisable( GL20.GL_DEPTH_TEST )
        }else {
          Shader.blend = false
          Gdx.gl.glEnable( GL20.GL_DEPTH_TEST )
          Gdx.gl.glDisable( GL20.GL_BLEND )
        }

        if(depth) Gdx.gl.glEnable( GL20.GL_DEPTH_TEST )
        else Gdx.gl.glDisable( GL20.GL_DEPTH_TEST )

        scene.draw()
      }
      
      shader.end()
    } catch{ case e:Exception => println(e)
      println ("\n" + e.printStackTrace + "\n")
    }

    unbindTarget()
  }
}

class BasicNode extends RenderNode

// object ScreenNode extends RenderNode {
//   // clear = false
//   scene.push(Plane.generateMesh())
//   shader = "texture"
// }

// class TextureNode(var texture:GdxTexture) extends RenderNode{
//   override def bindBuffer(i:Int) = texture.bind(i)  
//   override def render(){}
// }

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

