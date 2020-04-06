

// package com.fishuyo.seer
// package objects

// import spatial._
// import graphics._
// import particle._

// import scala.collection.mutable.ListBuffer

// import com.badlogic.gdx._
// import com.badlogic.gdx.graphics.GL20
// import com.badlogic.gdx.graphics._
// import com.badlogic.gdx.graphics.{Mesh => GdxMesh}
// import com.badlogic.gdx.graphics.glutils.VertexBufferObject

// object Fabric {
//   def apply( p: Vec3, w: Float, h: Float, d: Float, m:String="xy") = new Fabric(p,w,h,d,m)
// }

// class Fabric( var pos:Vec3=Vec3(0), var width:Float=1f, var height:Float=1f, var dist:Float=.05f, mode:String="xy") extends Animatable {

//   var stiff = 1.1f
//   var particles = ListBuffer[Particle]()
//   var links = ListBuffer[LinearSpringConstraint]()
//   var pins = ListBuffer[AbsoluteConstraint]()
//   var field:VecField3D = null

//   val nx = (width / dist).toInt
//   val ny = (height / dist).toInt

//   val numLinks = 2*nx*ny - nx - ny

//   for( j <- ( 0 until ny ); i <- ( 0 until nx)){

//     var x=0f; var y=0f;
//     var p:Particle = null

//     mode match {
//       case "xy" | "yx" => x = pos.x - width/2 + i*dist; y = pos.y + height/2 - j*dist; p = Particle( Vec3(x,y,pos.z) )
//       case "zy" | "yz" => x = pos.z - width/2 + i*dist; y = pos.y + height/2 - j*dist; p = Particle( Vec3(pos.x,y,x) )
//       case "xz" | "zx" => x = pos.x - width/2 + i*dist; y = pos.z + height/2 - j*dist; p = Particle( Vec3(x,pos.y,y) )
//       case _ => x = pos.x - width/2 + i*dist; y = pos.y + height/2 - j*dist; p = Particle( Vec3(x,y,pos.z) )
//     }

//     p.mass = .1f

//     if( i != 0 ) links += LinearSpringConstraint(p, particles(particles.length-1), dist, stiff)
//     if( j != 0 ) links += LinearSpringConstraint(p, particles( (j-1) * nx + i), dist, stiff)
//     if( (mode == "xz" || mode == "zx") && j > ny/4 && j < 3*ny/4 && i < 3*nx/4 && i>nx/4) pins += AbsoluteConstraint(p, p.position )
//     else if( (mode != "xz" && mode != "zx") && j == 0 ) pins += AbsoluteConstraint(p, p.position )
//     particles += p
//   }
  
//   var xt=0.0

//   var vertices = new Array[Float](3*2*numLinks)
//   var mesh:GdxMesh = null

//   override def animate( dt: Float ) = {

//     // if( Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)){
//     //   Fabric.gv.x = -Gdx.input.getAccelerometerX
//     //   Fabric.gv.y = -Gdx.input.getAccelerometerY
//     //   //Fabric.gv.z = -Gdx.input.getAccelerometerZ
//     // }

//     val ts = .015f
//     Integrators.setTimeStep(ts)

//     val steps = ( (dt+xt) / ts ).toInt
//     xt += dt - steps * ts

//     for( t <- (0 until steps)){
//       for( s <- (0 until 3) ){ 
//         links.foreach( _.solve() )
//         pins.foreach( _.solve() )
//       }

//       particles.foreach( (p) => {
//       	if( field != null ) p.applyForce( field(p.position) ) 
//         p.applyGravity()
//         p.applyDamping(20f)
//         p.step() // ts 
//       })

//     }

//   }

//   override def draw() {
//     //if( vbo == null ) vbo = new VertexBufferObject( false, 2*links, VertexAttribute.Position )
//     if( mesh == null) mesh = new GdxMesh(false,2*numLinks,0,VertexAttribute.Position)
//     var i = 0;
//     for( i<-(0 until links.size)){
//       val p = links(i).p.position
//       val q = links(i).q.position
//       vertices(6*i) = p.x
//       vertices(6*i+1) = p.y
//       vertices(6*i+2) = p.z
//       vertices(6*i+3) = q.x
//       vertices(6*i+4) = q.y
//       vertices(6*i+5) = q.z
//     }
//     mesh.setVertices(vertices)
//     mesh.render( Shader(), GL20.GL_LINES)    
//   }

//   def applyForce( f: Vec3 ) = particles.foreach( _.applyForce(f) )

//   def addField( f: VecField3D ) = field = f
// }

