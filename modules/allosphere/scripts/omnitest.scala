
import com.fishuyo.seer._

import com.fishuyo.seer.allosphere._

import graphics._
import dynamic._
import spatial._
import io._
import com.fishuyo.seer.particle._
import util._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

import de.sciss.osc.Message

import collection.mutable.ArrayBuffer

// import com.fishuyo.seer.allosphere.OmniTest

// Scene.alpha = .3
// SceneGraph.root.depth = false

Mouse.clear
Mouse.use

implicit val ctx = rx.Ctx.Owner.Unsafe


implicit def f2i(f:Float) = f.toInt

object Script extends SeerScript {
	

  // OmniTest.mode = "omni"

  val c = Cube()
  val nc = 1
  val cubes = for(z <- -nc to nc; y <- -nc to nc; x <- -nc to nc) yield {
    val c = Cube().translate(Vec3(x,y,z)*3f)
    c.material = Material.specular
    c.material.color = RGB(1,0,1)
    c
  }

 	val n = 40
  val mesh = Plane.generateMesh(10,10,n,n,Quat.up)
  mesh.primitive = Lines
  val model = Model(mesh)
  model.material = Material.specular
  model.material.color = RGB(0,0.5,0.7)
  // mesh.vertices.foreach{ case v => v.set(v.x,v.y+Random.float(-1,1).apply()*0.05*(v.x).abs,v.z) }
  mesh.vertices.foreach{ case v => v.set(v.x,v.y+math.sin(v.x*v.z)*0.1,v.z) }
  val fabricVertices0 = mesh.vertices.clone

  val fabric = new SpringMesh(mesh,1f)
  fabric.pins += AbsoluteConstraint(fabric.particles(0), fabric.particles(0).position)
  fabric.pins += AbsoluteConstraint(fabric.particles(n), fabric.particles(n).position)
  // fabric.pins += AbsoluteConstraint(fabric.particles(0), fabric.particles(0).position)
  fabric.pins += AbsoluteConstraint(fabric.particles.last, fabric.particles.last.position)
  Gravity.set(0,0,0)

  mesh.primitive = Triangles

  var t = 0f
  var scale = 1f

	val cursor = Sphere().scale(0.05)
	var lpos = Vec2()
	var vel = Vec2()

  val renderer = OmniTest.renderer//Renderer().asInstanceOf[OmniStereoRenderer] //RenderGraph.roots(0).asInstanceOf[OmniStereoRenderNode]
  renderer.environment.alpha = 0.1f
  renderer.environment.blend = true
  renderer.environment.depth = false

	// override def preUnload(){
		// recv.clear()
    // recv.disconnect()
	// }

	var inited = false
	override def init(){
    // renderer.shader = Shader.load(OmniShader.glsl + S.basic._1, S.basic._2 )
    // OmniTest.omni.configure("/Users/fishuyo/calib", "gr02")
    // OmniTest.omni.onCreate

    // renderer.omni.mStereo = 1
    // renderer.omni.mMode = StereoMode.ANAGLYPH
    // renderer.omni.mMode = StereoMode.ACTIVE
		// renderer.omni.mMode = StereoMode.SEQUENTIAL
		// OmniTest.omni.renderFace(0) = true
		// OmniTest.omni.renderFace(1) = true
		// OmniTest.omni.renderFace(2) = true
		// OmniTest.omni.renderFace(3) = true
		// OmniTest.omni.renderFace(4) = true
		// OmniTest.omni.renderFace(5) = true
		
		inited = true
	}

  override def draw(){
		// Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
    // Gdx.gl.glDisable( GL20.GL_DEPTH_TEST )
    FPS.print

  	model.draw
    cubes.foreach(_.draw)
  }

  override def animate(dt:Float){
  	if(!inited) init()

  	renderer.lens.eyeSep = Mouse.y.now * 0.5

  	if( Mouse.status.now == "drag"){
			vel = (Mouse.xy.now - lpos)/dt
			// println(vel)
			// s.applyForce( Vec3(vel.x,vel.y,0)*10f)
			val r = Camera.ray(Mouse.x.now*Window.width, (1f-Mouse.y.now * Window.height))
			fabric.particles.foreach( (p) => {
				val t = r.intersectSphere(p.position, 0.25f)
				if(t.isDefined){
					// val p = r(t.get)
					p.applyForce(Vec3(vel.x,vel.y,0)*150f)
					cursor.pose.pos.set(r(t.get))
				}
			})
		}
		lpos = Mouse.xy.now



  	fabric.animate(dt)
  	cubes.foreach(_.scale.set(scale))
  }


	// val recv = new OSCRecv
 //  recv.listen(12001)
 //  recv.bindp {
 //    case Message("/mx", x:Float) => Camera.nav.vel.x = -x
 //    case Message("/my", x:Float) => Camera.nav.vel.y = x
 //    case Message("/mz", x:Float) => Camera.nav.vel.z = x
 //    case Message("/tx", x:Float) => Camera.nav.angVel.x = x * -.02
 //    case Message("/ty", x:Float) => Camera.nav.angVel.y = x * .02
 //    case Message("/tz", x:Float) => Camera.nav.angVel.z = x * -.02
 //    case Message("/home") => Camera.nav.moveToOrigin
 //    case Message("/halt") => Camera.nav.stop

 //    case _ => ()
 //  }

}

object S {
 val basic = (
    // Vertex Shader
    """
      attribute vec3 a_position;
      attribute vec3 a_normal;
      attribute vec4 a_color;
      attribute vec2 a_texCoord0;

      uniform int u_hasColor;
      uniform vec4 u_color;
      uniform mat4 u_projectionViewMatrix;
      uniform mat4 u_modelViewMatrix;
      uniform mat4 u_viewMatrix;
      uniform mat4 u_modelMatrix;
      uniform mat4 u_normalMatrix;
      uniform vec4 u_cameraPosition;

      uniform vec3 u_lightPosition;

      varying vec4 v_color;
      varying vec3 v_normal, v_pos, v_lightDir, v_eyeVec;
      varying vec2 v_texCoord;
      varying float v_fog;

      void main(){
        // if( u_hasColor == 0){
        if( a_color.xyz == vec3(0,0,0)){
          v_color = u_color;
        } else {
          v_color = a_color;
        }

        vec4 pos = u_modelViewMatrix * vec4(a_position,1);
        v_pos = vec3(pos) / pos.w;

        v_normal = vec3(u_normalMatrix * vec4(a_normal,0));
        
        v_eyeVec = normalize(-pos.xyz);

        v_lightDir = vec3(u_viewMatrix * vec4(u_lightPosition,0));

        v_texCoord = a_texCoord0;
        gl_Position = omni_render(u_modelViewMatrix * vec4(a_position,1));
        // gl_Position = u_projectionViewMatrix * vec4(a_position,1); 
      }
    """,
    // Fragment Shader
    """
      #ifdef GL_ES
       precision mediump float;
      #endif

      uniform sampler2D u_texture0;

      uniform float u_alpha;
      uniform float u_fade;
      uniform float u_textureMix;
      uniform float u_lightingMix;
      uniform vec4 u_lightAmbient;
      uniform vec4 u_lightDiffuse;
      uniform vec4 u_lightSpecular;
      uniform float u_shininess;

      varying vec2 v_texCoord;
      varying vec3 v_normal;
      varying vec3 v_eyeVec;
      varying vec3 v_lightDir;
      varying vec4 v_color;
      varying vec3 v_pos;

      void main() {
        
        vec4 colorMixed;
        if( u_textureMix > 0.0){
          vec4 textureColor = texture2D(u_texture0, v_texCoord);
          colorMixed = mix(v_color, textureColor, u_textureMix);
        }else{
          colorMixed = v_color;
        }

        vec4 final_color = colorMixed * u_lightAmbient;

        vec3 N = normalize(v_normal);
        vec3 L = normalize(v_lightDir);

        float lambertTerm = dot(N,L);
        final_color += u_lightDiffuse * colorMixed * max(lambertTerm,0.0);

        float specularTerm = 0.0;

        //phong
        vec3 R = reflect(-L, N);
        vec3 E = normalize(v_eyeVec); //normalize(-v_pos);
        //float specAngle = max(dot(R,E), 0.0);
        //specularTerm = pow(specAngle, 8.0);

        //blinn
        float halfDotView = max(0.0, dot(N, normalize(L + E)));
        specularTerm = pow(halfDotView, 20.0);
        specularTerm = specularTerm * smoothstep(0.0,0.2,lambertTerm);

        final_color += u_lightSpecular * specularTerm;
        gl_FragColor = mix(colorMixed, final_color, u_lightingMix);
        gl_FragColor *= (1.0 - u_fade);
        gl_FragColor.a *= u_alpha;

        // gl_FragColor = vec4(1,0,1,1); //(1.0 - u_fade);

      }
    """
  )
 }


Script

