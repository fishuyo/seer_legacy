
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


object RootNode extends RenderNode {
  renderer.scene = Scene
  renderer.camera = Camera
  renderer.shader = Shader.load(DefaultShaders.basic)

  def reset(){
    // reset root node state
    // renderer.shader = Shader.load(DefaultShaders.basic)
    renderer.environment.default()
    renderer.scene.clear
    inputs.clear
    outputs.clear
  }
}

object RenderGraph {
  val roots = ListBuffer[RenderNode]()
  // var root:RenderNode = new BasicNode
  // root.scene = Scene
  // root.camera = Camera
  // roots += root

  def clear() = roots.clear

  def reset() = {
    roots.clear
    RootNode.inputs.clear
    RootNode.outputs.clear
    roots += RootNode
  }

  def addNode(n:RenderNode){
    roots += n
    // n.renderer.scene.init()
  }
  def prependNode(n:RenderNode){
    roots.prepend(n)
    // n.renderer.scene.init()
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
    // println("node: " + node.getClass.getName)
    node.render()
    node.outputs.foreach( (n) => if( n != node) renderChildren(n) )
  }

  // def leaves() = {
  // }
}

