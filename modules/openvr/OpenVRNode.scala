
package com.fishuyo.seer

import graphics._
import spatial._
import io._

import openvrprovider._
import org.joml._ //Matrix4f
// import org.joml.Vector3f
// import com.sun.jna.Structure

// import org.lwjgl.opengl.GL11
// import org.lwjgl.opengl.GL11._

import org.lwjgl.glfw.Callbacks._
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.EXTFramebufferObject._ //GL_FRAMEBUFFER_EXT
import org.lwjgl.opengl.GL11._
import org.lwjgl.system.MemoryUtil._

// example SeerApp usage
//
object OpenVRTest extends SeerApp {
  Window.w0 = 1280 //960 //780
  Window.h0 = 720  //540 //438
  val models = for(i <- -3 to 3; j <- -3 to 3; k <- -3 to 3) yield {
    val m = Cube().translate(i,j,k).scale(0.1)
    m.material.specular()
    m
  }

  val provider = new OpenVRProvider();

  override def init(){
    // com.badlogic.gdx.Gdx.graphics.setVSync(true)
    // DesktopApp.app.resize(780,438)
    // println(Rift.hmd)

    // DesktopApp.toggleFullscreen

    RenderGraph.clear
    RenderGraph += new OpenVRNode(provider)
  }

  override def draw(){
    FPS.print
    models.foreach(_.draw)
  }
  override def animate(dt:Float){
    // Camera.step(dt)
    // RenderGraph.roots(0).renderer.camera.nav.set( Camera.nav )
    models.foreach(_.rotate(0.01,0.01,.02))
  }

}


// object OpenVRNode extends OpenVRNode
class OpenVRNode(val vrProvider:OpenVRProvider) extends RenderNode {

  // val vrProvider = new OpenVRProvider();
  val vrRenderer = new OpenVRStereoRenderer(vrProvider,1280,720)

  // renderer.clear = false
  renderer.camera = new ManualCamera
  renderer.scene = Scene

  // DesktopApp.toggleFullscreen


  val nav0 = Nav()
  val navMove = Nav()
  Keyboard.bindNav(nav0)
  Keyboard.bindNav(navMove)

 
  override def animate(dt:Float){

    navMove.step(dt)
    nav0.step(dt)
    renderer.animate(dt)
  }

  override def render(){
   
    vrProvider.updateState()

    //for each eye
    for (i <- 0 until 2) {

      glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, vrRenderer.getTextureHandleForEyeFramebuffer(i))
      
      val eyePose = vrProvider.vrState.getEyePose(i);
      val matView = new Matrix4f(eyePose).invert();
      val eyeProjection = vrProvider.vrState.getEyeProjectionMatrix(i);
      val matMVP = eyeProjection.mul(matView);

      val mat4 = new Array[Float](16)      
      eyeProjection.get(mat4)

      val pos = new Vector3f
      eyePose.getTranslation(pos)

      val q = new Quaternionf
      eyePose.getNormalizedRotation(q)

      // shader.setUniformMatrix("MVP", false, matMVP);

      // val eye = hmd.EyeRenderOrder(i)
      // val P = projections(eye)
      // val pose = headPosesToUse(eye)
      
      val matPos = Vec3(-pos.x, -pos.y, -pos.z)
      val quat = Quat(q.w, q.x, q.y, q.z)// RH

      navMove.quat.set(nav0.quat*quat)
      renderer.camera.nav.quat.set(nav0.quat*quat)
      renderer.camera.nav.pos.set(matPos) //navMove.pos)
      renderer.camera.asInstanceOf[ManualCamera].projection.set(mat4) //.tra()
      renderer.camera.update
      
      // framebuffers(eye).begin()
      renderer.render()
      // glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0)
      // framebuffers(eye).end()
    }
    
    vrProvider.submitFrame();

    // numFrames += 1

    // unbindTarget()
  }


}
