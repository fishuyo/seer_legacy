


package com.fishuyo.seer
package examples.particle

import graphics._
import spatial._
import util._
import io._


import particle._

object FloorFabric extends SeerApp { 

  var t = 0f

  val (nx,ny) = (50,50)

  val mesh = Plane.generateMesh(4,4,nx,ny,Quat.up)
  mesh.primitive = Lines

  val spring = new SpringMesh(mesh,1f)

  // audio.Audio().sampleRate = nx
  // val s = new audio.Sine(1, 0.1)
  for(p <- spring.particles.takeRight(nx)){ //.sliding(1,nx/12).flatten){ 
    // spring.pins += AbsoluteConstraint(p,p.position * Vec3(1,1,1) + Vec3(0,4,Random.float()*0.01f))
    spring.pins += AbsoluteConstraint(p,p.position)
  //   spring.pins += AbsoluteConstraint(p,p.position * Vec3(0.9,1,1) + Vec3(0,4 - math.abs(s()),Random.float()*0.01f))
  }
  for(p <- spring.particles.take(nx)){
    spring.pins += AbsoluteConstraint(p,p.position)
  }
  spring.updateNormals = true
  
  val model = Model(spring)

  // modify material
  // model.material = new SpecularMaterial
  // model.material.color = RGBA(0f,.6f,.6f,1f)

  var lpos = Vec2()
  var vel = Vec2()

  var initd = false

  Gravity.set(0,0,0)

  Keyboard.bindCamera()
  
  Keyboard.bind("g", ()=>{ 
    if(Gravity.y == 0f) Gravity.set(0,-5,0)
    else Gravity.zero()
  })

  override def draw(){
    model.draw()
  }

  override def animate(dt:Float){

    t += dt 

    implicit def f2i(f:Float) = f.toInt

    if( Mouse.status.now == "drag"){
      vel = (Mouse.xy.now - lpos)/dt
      // println(vel)
      // s.applyForce( Vec3(vel.x,vel.y,0)*10.0f)
      val r = Camera.ray(Mouse.x.now*Window.width, (1f-Mouse.y.now) * Window.height)
      spring.particles.foreach( (p) => {
        val t = r.intersectSphere(p.position, 0.25f)
        if(t.isDefined){
          // val p = r(t.get)
          p.applyForce(Vec3(vel.x,vel.y,0)*150f)
          // cursor.pose.pos.set(r(t.get))
        }
      })
    }
    lpos = Mouse.xy.now
    // simulate the spring mesh
    // this automatically recalculates normals, and updates the mesh
    if(t > 1){
      if(!initd){
        // model.material.loadTexture("CassowaryJack.jpg")
        initd = true
      }

      spring.animate(dt)
    }

    // // every second, snap a random vertex along its normal
    // if(t > 1f){
    //   t = 0f
    //   val i = util.Random.int(0, model.mesh.vertices.length)()
    //   model.mesh.vertices(i) += model.mesh.normals(i) * (util.Random.float()*0.1f)
    // }
  }
}