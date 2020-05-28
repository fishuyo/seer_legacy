
package com.fishuyo.seer
package graphics

import spatial._
import io._
import util._

// import org.lwjgl.opengl.GL11
// import org.lwjgl.opengl.GL12
// import org.lwjgl.opengl.GL43

object Terrain {
  def apply(tx:Int=65, ty:Int=65, scale:Float=67f) = new Terrain(tx,ty,scale)

  def fractalize(m:Mesh, nx:Int, ny:Int, roughness:Float=0.15f) = {

    divide(nx-1)

    def divide(size:Int){
      val half = size/2
      var scale = roughness * size
      if(half < 1) return
      for(y <- half until (ny) by size; x <- half until (nx) by size)
        square(x,y,half, Random.float() * scale * 2 - scale)

      for(y <- 0 until (ny) by half; x <- ((y+half)%size) until (nx) by size)
        diamond(x,y,half, Random.float() * scale * 2 - scale)
      
      divide(size/2)
    }

    def indx(i:Int,j:Int ) = {
      var x = i; var y = j;
      while( x < 0 ) x += nx; while( x >= nx ) x -= nx;
      while( y < 0 ) y += ny; while( y >= ny ) y -= ny;
      nx*y + x
    }

    def square(x:Int, y:Int, size:Int, offset:Float){
      val avg = Vec3()
      avg += m.vertices(indx(x-size, y-size))
      avg += m.vertices(indx(x+size, y-size))
      avg += m.vertices(indx(x+size, y+size))
      avg += m.vertices(indx(x-size, y+size))
      avg /= 4f

      m.vertices(y*nx+x).y = avg.y + offset
      // m.vertices(y*nx+x) = avg * offset
    }

    def diamond(x:Int, y:Int, size:Int, offset:Float){
      val avg = Vec3()
      avg += m.vertices(indx(x, y-size))
      avg += m.vertices(indx(x+size, y))
      avg += m.vertices(indx(x, y+size))
      avg += m.vertices(indx(x-size, y))
      avg /= 4f

      m.vertices(y*nx+x).y = avg.y + offset
      // m.vertices(y*nx+x) = avg * offset

    }
  }
}

class Terrain(tx:Int, ty:Int, scale:Float) extends Animatable {

  val terrain = Plane.generateMesh(scale,scale,tx,ty,Quat.up)
  Terrain.fractalize(terrain,tx,ty)

  var dirty = true

  val tmodels = new Array[Model](9)
  val lmodels = new Array[Model](9)
  var idx = 0;
  for( i<-(-1 to 1); j<-(-1 to 1)){
    tmodels(idx) = Model(terrain).translate(scale*i,0,scale*j)
    if(i != 0) tmodels(idx).scale(-1,1,1)
    if(j != 0) tmodels(idx).scale(1,1,-1)

    tmodels(idx).material = Material.specular
    // tmodel.material.color = RGBA(0.01,0.01,0.01,0.1)
    // tmodel.material.color = RGBA(0.5,0.5,0.5,0.1)
    tmodels(idx).material.color = RGBA(1,1,1,1)

    lmodels(idx) = Model(terrain).translate(scale*i,0.01f,scale*j)
    if(i != 0) lmodels(idx).scale(-1,1,1)
    if(j != 0) lmodels(idx).scale(1,1,-1)
    // lmodels(idx).translate(0,0.01,0)
    lmodels(idx).material = Material.basic
    lmodels(idx).material.color = RGBA(0,0,0,1)
    // lmodels(idx).material.color = RGBA(1,1,1,1)

    idx += 1
  }

  override def animate(dt:Float){ Camera.nav.pos.wrap(Vec3(-scale),Vec3(scale)) }
 
  override def draw(){

    if(dirty){
      dirty = false
      terrain.recalculateNormals
      terrain.update
    }

    // GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST);
    // GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
    // GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
    // GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
    // GL11.glEnable(GL11.GL_LINE_SMOOTH);
    // GL11.glEnable(GL11.GL_POINT_SMOOTH);
    // GL11.glEnable(GL11.GL_NORMALIZE);
    com.badlogic.gdx.Gdx.gl.glEnable(0x0BA1)

    // terrain.primitive = Triangles
    // GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)
    tmodels.foreach(_.draw)

    // GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)
    // lmodels.foreach(_.draw)

    // GL11.glDisable(GL11.GL_NORMALIZE);
    // GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)
  }

  // io.VRPN.clear
  // io.VRPN.bind("thing", (p:Pose) => {
  //   println(p.pos)
  //   Camera.nav.quat.slerpTo(p.quat,0.2)
  // })
}

// object Tile{
//   val (tx,ty) = (65,65)
//   val sx = 67f
// }
// class Tile(val xy:Vec2){
//   import Tile._

//   val seed = Random.long()

//   // val (tx,ty) = (65,65)
//   // val sx = 67f

//   val mesh:Mesh = _

//   def generateMesh(){
//     mesh = Plane.generateMesh(sx,sx,tx,ty,Quat.up)
//     Random.seed(id)
//     Script.fractalize(terrain,tx,ty)
//     mesh.recalculateNormals
//     mesh.update
//   }

//   def dispose(){
//     mesh.dispose
//     mesh = null
//   }

// }

// class Terrain {
//   import Tile._
// //   val tiles

//   val map = Array.ofDim[Tile](3,3)
//   val models = Array.ofDim[Model](3,3)

//   for(i <- (0 until 3); j<-(0 until 3)){
//     map(i)(j) = new Tile(Vec2(i,j))
//     val m = Model()
//     m.translate(sx*(i-1),0,sx*(j-1))
//     m.material = Material.specular
//     model(i)(j) = m
//   }


//   override def draw() = models.foreach(_.draw())
//   override def animate() = {
//     for(i <- (0 until 3); j<-(0 until 3)){
//       val tile = map(i)(j)
//       if(tile.mesh == null){ 
//         tile.generateMesh
//         models(i)(j) = Model(tile.mesh)
//       }
//     }
//   }



// }



