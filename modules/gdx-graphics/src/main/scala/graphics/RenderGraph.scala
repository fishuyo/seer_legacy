
package com.fishuyo.seer
package graphics

import spatial._
import util._
import scala.collection.mutable.ListBuffer
//import javax.media.opengl._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
// import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.graphics.{Texture => GdxTexture}

import scala.concurrent.duration._

object RootNode extends RenderNode {
  renderer.scene = Scene
  renderer.camera = Camera
  renderer.shader = Shader.load("basic",DefaultShaders.basic)

  override def reset(){
    // reset root node state
    // renderer.shader = Shader.load(DefaultShaders.basic)
    renderer.environment.default()
    renderer.scene.clear
    super.reset()
  }
}

object Become {
  def apply(name:String) = {
    val scene = Scene(name)
    val node = RenderGraph.roots.filter( _.renderer.scene == scene)
    node.toList match {
      case n :: ns => new Become(n)
      case _ => println(s"Error no renderer node with scene $name in graph."); new Become(null)
    }
  }
}
class Become(node:RenderNode) {

  def setup(){
    if(node == null) return
    Run.animate {
      val c =  RenderGraph.compositor
      c.inputs(0) = c.inputs(1)
      c.inputs(1) = node
      node.renderer.active = true
    }
  }
  def now = {
    setup()
  }
  def over(dur:FiniteDuration){
    if(node == null) return
    setup()
    Schedule.over(dur){ case t => 
      RenderGraph.compositor.xfade(t)
      if(t == 1f) RenderGraph.compositor.inputs(0).renderer.active = false
    }    
  }
}

object RenderGraph {
  val roots = ListBuffer[RenderNode]()

  val compositor = new CompositeNode(0f,1f)
  RootNode >> compositor
  RootNode >> compositor
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

  def +=(n:RenderNode){ addNode(n) }
  def addNode(n:RenderNode){
    roots += n
    // n.renderer.scene.init()
  }
  def prependNode(n:RenderNode){
    roots.prepend(n)
    // n.renderer.scene.init()
  }

  def -=(n:RenderNode){ removeNode(n) }
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

