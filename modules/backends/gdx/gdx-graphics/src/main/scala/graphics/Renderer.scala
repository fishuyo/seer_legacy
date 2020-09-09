
package seer
package graphics

import spatial.Vec3

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
// import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.utils.viewport._


/**
  * Compainion object holding current renderer
  */
object Renderer {
  var renderer = RootNode.renderer //new Renderer
  def apply() = renderer 
  def update(r:Renderer) = renderer = r
}

/**
  * Renderer encapsulates a camera, a scene, an environment, and a shader
  */
class Renderer {

  var viewport = Viewport(0,0,Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight())
  var camera:NavCamera = new OrthographicCamera(2,2)
  var scene = new Scene 
  var environment = new Environment
  var shader = Shader("basic") //Shader.load("basic") DefaultShaders.basic) //new Shader

  var active = true
  var clear = true
  var depth = true
  var resize = true

  var vp:Option[ScreenViewport] = None
  var viewportf = (v:ScreenViewport) => {}
  var pre = () => {}
  var post = () => {}
  
  var material:BasicMaterial = new BasicMaterial
  
  def render(){
    if(!active) return

    try{
      Renderer() = this
      
      pre()

      if(clear) Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
      else Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT)

      vp.foreach(viewportf(_))
      
      shader.begin() 

      MatrixStack.clear()
      setMatrixUniforms()
      setEnvironmentUniforms()
      environment.setGLState()
      setMaterialUniforms(material)
      
      shader.setUniforms() // set buffered uniforms in shader program

      scene.draw()
      
      shader.end()

      post()

    } catch{ case e:Exception => println(s"Renderer: $e")
      // println ("\n" + e.printStackTrace + "\n")
    }

  }

  def resize(vp:Viewport){
    if(!resize) return
    viewport = vp
    if(camera.viewportHeight == 1f){
      camera.viewportWidth = vp.aspect
    }else{
      camera match {
        case ortho:OrthographicCamera =>
          // camera.viewportWidth = vp.aspect * camera.viewportHeight //vp.w
          // camera.viewportHeight = vp.h
        case cam:ManualCamera => ()
        case _ => camera.viewportWidth = vp.w
                  camera.viewportHeight = vp.h
      }
    }
  }

  def animate(dt:Float){
    if(!active) return
    scene.animate(dt)
    camera.step(dt)
  }

  def setMatrixUniforms(){
    MatrixStack(camera)
    shader.uniforms("u_projectionViewMatrix") = MatrixStack.projectionModelViewMatrix() 
    shader.uniforms("u_modelViewMatrix") = MatrixStack.modelViewMatrix() 
    shader.uniforms("u_viewMatrix") = MatrixStack.viewMatrix() 
    shader.uniforms("u_modelMatrix") = MatrixStack.modelMatrix() 
    shader.uniforms("u_normalMatrix") = MatrixStack.normalMatrix()
    shader.uniforms("u_cameraPosition") = camera.nav.pos
  }

  def setEnvironmentUniforms(){
    val e = environment
    shader.uniforms("u_lightPosition") = e.lightPosition
    shader.uniforms("u_lightAmbient") = e.lightAmbient
    shader.uniforms("u_lightDiffuse") = e.lightDiffuse
    shader.uniforms("u_lightSpecular") = e.lightSpecular
    shader.uniforms("u_alpha") = e.alpha
    // shader.uniforms("u_fog") = e.fog
  }
  
  def setMaterialUniforms(mat:Material){
    
    mat match {
      // case m:ShaderMaterial => setBasicMaterial(m);
      // case m:SpecularMaterial => setBasicMaterial(m); //lightingMix=1f; shininess = m.shininess
      // case m:DiffuseMaterial => setBasicMaterial(m); //lightingMix=1f; shininess = 0f
      case m:NoMaterial => setMaterialUniforms(material)
      case m:BasicMaterial =>
        shader.uniforms("u_color") = m.color
        shader.uniforms("u_lightingMix") = m.lightingMix
        shader.uniforms("u_textureMix") = m.textureMix
        shader.uniforms("u_shininess") = m.shininess
        m.texture.foreach( (t) => {t.bind(0); shader.uniforms("u_texture0")=0 } )
        if(m.transparent){
          Gdx.gl.glEnable(GL20.GL_BLEND)
          Gdx.gl.glDisable( GL20.GL_DEPTH_TEST )
        } else {
          Gdx.gl.glDisable(GL20.GL_BLEND)
          Gdx.gl.glEnable( GL20.GL_DEPTH_TEST )          
        }
        Gdx.gl.glLineWidth(m.lineWidth)
      case _ => () //setMaterial(defaultMaterial)
    }
  }


}