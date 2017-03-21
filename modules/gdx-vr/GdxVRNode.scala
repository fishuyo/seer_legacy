
package com.fishuyo.seer

import graphics._
import spatial._
import io._

import com.badlogic.gdx.vr._
import com.badlogic.gdx.vr.VRContext._ //Eye;
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
    RenderGraph += GdxVRNode
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
object GdxVRNode extends RenderNode with VRDeviceListener {

  val context = new VRContext()

  // renderer.clear = false
  renderer.camera = new ManualCamera
  renderer.scene = Scene

  // DesktopApp.toggleFullscreen


  // val nav0 = Nav()
  // val navMove = Nav()
  // Keyboard.bindNav(nav0)
  // Keyboard.bindNav(navMove)
 
  context.addListener(this)


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


  // VRDeviceListener methods
  override def connected(device:VRContext#VRDevice) {
    println(device + " connected");
    // if (device.getType() == VRDeviceType.Controller && device.getModelInstance() != null)
    //   modelInstances.add(device.getModelInstance());
  }


  override def disconnected(device:VRContext#VRDevice) {
    println(device + " disconnected");
    // if (device.getType() == VRDeviceType.Controller && device.getModelInstance() != null)
    //   modelInstances.removeValue(device.getModelInstance(), true);
  }


  override def buttonPressed(device:VRContext#VRDevice, button:Int) {
    println(device + " button pressed: " + button);

    // If the trigger button on the first controller was
    // pressed, setup teleporting
    // mode.
    if (device == context.getDeviceByType(VRDeviceType.Controller)) {
      // if (button == VRControllerButtons.SteamVR_Trigger)
      //   isTeleporting = true;
    }
  }


  override def buttonReleased(device:VRContext#VRDevice, button:Int) {
    println(device + " button released: " + button);

    // If the trigger button the first controller was released,
    // teleport the player.
    // if (device == context.getDeviceByType(VRDeviceType.Controller)) {
    //   if (button == VRControllerButtons.SteamVR_Trigger) {
    //     if (intersectControllerXZPlane(context.getDeviceByType(VRDeviceType.Controller), tmp)) {
    //       // Teleportation
    //       // - Tracker space origin in world space is initially at [0,0,0]
    //       // - When teleporting, we want to set the tracker space origin in world space to the
    //       //   teleportation point
    //       // - Then we need to offset the tracker space
    //       //   origin in world space by the camera
    //       //   x/z position so the camera is at the
    //       //   teleportation point in world space
    //       tmp2.set(context.getDeviceByType(VRDeviceType.HeadMountedDisplay).getPosition(Space.Tracker));
    //       tmp2.y = 0;
    //       tmp.sub(tmp2);

    //       context.getTrackerSpaceOriginToWorldSpaceTranslationOffset().set(tmp);
    //     }
    //     isTeleporting = false;
    //   }
    // }
  }

}
