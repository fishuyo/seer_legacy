
package com.fishuyo.seer

import graphics._
import spatial._
import io._

import com.badlogic.gdx.vr._
import com.badlogic.gdx.vr.VRContext.Eye;
// import com.badlogic.gdx.vr.VRContext.Space;
// import com.badlogic.gdx.vr.VRContext.VRControllerButtons;
// import com.badlogic.gdx.vr.VRContext.VRDevice;
// import com.badlogic.gdx.vr.VRContext.VRDeviceListener;
// import com.badlogic.gdx.vr.VRContext.VRDeviceType;


// example SeerApp usage
//
object GdxVRTest extends SeerApp {
  Window.w0 = 1280 //960 //780
  Window.h0 = 720  //540 //438
  val models = for(i <- -2 to 2; j <- -2 to 2; k <- -2 to 2) yield {
    val m = Cube().translate(i,j,k).scale(0.1)
    m.material.specular()
    m
  }

  override def init(){
    // com.badlogic.gdx.Gdx.graphics.setVSync(true)
    // DesktopApp.app.resize(780,438)
    // println(Rift.hmd)

    // DesktopApp.toggleFullscreen

    // RenderGraph.clear
    RenderGraph += new GdxVRNode
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


// object GdxVRNode extends GdxVRNode
class GdxVRNode extends RenderNode {

  val context = new VRContext()

  // renderer.clear = false
  renderer.camera = new ManualCamera
  renderer.scene = Scene

  // DesktopApp.toggleFullscreen


  // val nav0 = Nav()
  // val navMove = Nav()
  // Keyboard.bindNav(nav0)
  // Keyboard.bindNav(navMove)

 
  override def animate(dt:Float){

    // navMove.step(dt)
    // nav0.step(dt)
    renderer.animate(dt)
  }

  override def render(){
   
  	context.pollEvents()
  	context.begin()

	context.beginEye(Eye.Left)
	var cam = context.getEyeData(Eye.Left).camera
	renderer.camera.combined.set(cam.combined)
	renderer.camera.view.set(cam.view)
	renderer.render()
	context.endEye();

	context.beginEye(Eye.Right);
	cam = context.getEyeData(Eye.Right).camera
	renderer.camera.combined.set(cam.combined)
	renderer.camera.view.set(cam.view)
	renderer.render()
	context.endEye();

	context.end(); 




      // val eyePose = vrProvider.vrState.getEyePose(i);
      // val matView = new Matrix4f(eyePose).invert();
      // val eyeProjection = vrProvider.vrState.getEyeProjectionMatrix(i);
      // val matMVP = eyeProjection.mul(matView);

      // val mat4 = new Array[Float](16)      
      // eyeProjection.get(mat4)

      // val pos = new Vector3f
      // eyePose.getTranslation(pos)

      // val q = new Quaternionf
      // eyePose.getNormalizedRotation(q)

      // shader.setUniformMatrix("MVP", false, matMVP);
      
      // val matPos = Vec3(-pos.x, -pos.y, -pos.z)
      // val quat = Quat(q.w, q.x, q.y, q.z)// RH

      // navMove.quat.set(nav0.quat*quat)
      // renderer.camera.nav.quat.set(nav0.quat*quat)
      // renderer.camera.nav.pos.set(matPos) //navMove.pos)
      // renderer.camera.asInstanceOf[ManualCamera].projection.set(mat4) //.tra()
      // renderer.camera.update
      
      // framebuffers(eye).begin()
      // renderer.render()
      // glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0)
      // framebuffers(eye).end()
    // }
    
    // context.end()
    // numFrames += 1

    // unbindTarget()
  }

  override def dispose(){
    // context.dispose
  }


}
