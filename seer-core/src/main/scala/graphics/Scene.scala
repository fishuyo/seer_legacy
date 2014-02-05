
package com.fishuyo.seer
package graphics
import maths._

import scala.collection.mutable.ListBuffer
//import javax.media.opengl._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL10
import com.badlogic.gdx.math.Matrix4


/**
* Singleton scene object to contain list of scene drawables
*/
object Scene extends Scene

class Scene {
  
  var active = true
  var alpha = 1.f
  var fade = 0.f
  def alpha(v:Float){ alpha = v }
  def fade(v:Float){ fade = v }

  val drawable = new ListBuffer[Drawable]
  val animatable = new ListBuffer[Animatable]
  //val pickable = new ListBuffer[GLPickable]
  //val lights = new ListBuffer[GLLight]

  def push( o: Drawable) = drawable += o
  def push( o: Animatable) = { animatable += o;  drawable += o }
  def remove( o: Animatable) = { animatable -= o;  drawable -= o }
  def clear() = { drawable.clear; animatable.clear }
  
  //def pushPickable( o: GLPickable) = pickable += o
  //def push( s: SoundSource) = { sounds += s; objects += s }

  def init() = drawable.foreach( _.init() )
  def animate( dt: Float ) = animatable.foreach( _.animate(dt) )
  //def onDraw( gl: GL2 ) = drawable.foreach( _.onDraw(gl) )
  def draw() = drawable.foreach( _.draw() )

  // def draw2() = drawable.foreach( _.draw2() )
  //def pick( r: Ray ) = pickable.foreach( _.pick(r) )
  
}


// object SceneManager {
//   val scenes = new ListBuffer[Scene]
//   val active = Scene :: List()

//   def apply(i:Int) = scenes(i)



//   // def init() = scenes.foreach( _.init() )
//   def step( dt: Float ) = scenes.filter( _.active == true).foreach( _.step(dt) )
//   def draw() = scenes.filter( _.active == true).foreach( _.draw() )

// }


object SceneGraph {
  var roots = ListBuffer[RenderNode]()
  var root = new BasicNode
  root.scene = Scene
  root.camera = Camera
  roots += root

  def addNode(n:RenderNode){
    n.scene.init()
    roots += n
  }

  def animate(dt:Float){
    roots.foreach( (n) => animateChildren(n,dt) )
  }
  def animateChildren(n:RenderNode, dt:Float){
   n.animate(dt)
   n.outputs.foreach( (n) => animateChildren(n,dt) )
  }

  def resize(vp:Viewport){
    roots.foreach( (n) => resizeChildren(n,vp))
  }
  def resizeChildren(n:RenderNode, vp:Viewport){
   n.resize(vp)
   n.outputs.foreach( (n) => resizeChildren(n,vp) )
  }

  def render(){
    roots.foreach( (n) => renderChildren(n) )
  }
  def renderChildren(n:RenderNode){
   n.render()
   n.outputs.foreach( (n) => renderChildren(n) )
  }

  def leaves() = {

  }
}


class RenderNode {
  var active = true
  val inputs = new ListBuffer[RenderNode]
  val outputs = new ListBuffer[RenderNode]

  var viewport = new Viewport(0,0,800,800)
  var scene = new Scene
  var camera:NavCamera = new OrthographicCamera(2,2)

  var buffer:Option[FrameBuffer] = None
  var shader = "basic"

  def createBuffer(){
    if(buffer.isEmpty) buffer = Some(FrameBuffer(viewport.w, viewport.h))
  }

  def resize(vp:Viewport){
    viewport = vp
    if(buffer.isDefined){
      buffer.get.dispose
      buffer = Some(FrameBuffer(vp.w,vp.h))
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

  def render(){
    if( buffer.isDefined ){
      buffer.get.begin()
      Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
    }

    inputs.foreach( _.buffer.get.getColorBufferTexture().bind(0) )

    Shader(shader).begin()
    MatrixStack.clear()
    Shader.setMatrices(camera)
    if(active){
      Shader.alpha = scene.alpha
      Shader.fade = scene.fade
      if( scene.alpha < 1.f ){ 
        Shader.blend = true
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glDisable( GL20.GL_DEPTH_TEST )
      }else {
        Shader.blend = false
        Gdx.gl.glEnable( GL20.GL_DEPTH_TEST )
        Gdx.gl.glDisable( GL20.GL_BLEND )
      }
      scene.draw()
    }
    
    Shader().end()

    if( buffer.isDefined ) buffer.get.end()
  }
}

class BasicNode extends RenderNode

class OutlineNode extends RenderNode {
  val quad = Primitive2D.quad
  override def render(){
    // inputs.foreach( _.buffer.get.getColorBufferTexture().bind(0) )

    SceneGraph.root.buffer.get.getColorBufferTexture().bind(0)
    Shader("secondPass").begin()
    Shader().setUniformi("u_texture0", 0);
    Shader().setUniformMatrix("u_projectionViewMatrix", new Matrix4())
    //Shader().setUniformMatrix("u_modelViewMatrix", new Matrix4())
    // Shader().setUniformMatrix("u_normalMatrix", modelViewMatrix.toNormalMatrix())
    // scene.draw2()
    // if( day.x == 1.f) Shader("secondPass").setUniformf("u_depth", 0.f)
    // else Shader("secondPass").setUniformf("u_depth", 1.f)

    quad.render(Shader(), GL10.GL_TRIANGLES)
    
    Shader().end();
  }
}

