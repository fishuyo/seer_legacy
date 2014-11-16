
package com.fishuyo.seer
package graphics

import spatial.Vec3

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
// import com.badlogic.gdx.math.Matrix4

/**
  * Compainion object holding current renderer
  */
object Renderer {
  var renderer = new Renderer
  def apply() = renderer 
  def update(r:Renderer) = renderer = r
}

/**
  * Renderer encapsulates a camera, a scene, an environment, and a shader
  */
class Renderer {

  var active = true
  var depth = true

  var camera:NavCamera = new OrthographicCamera(2,2)
  var scene = new Scene 
  var environment = new Environment
  var shader = new Shader

  var defaultMaterial:BasicMaterial = new BasicMaterial
  var visible = true
  var wireframe = false
  var linewidth = 1
  var blend = false
  var lightingMix = 1f
  var lightPosition = Vec3(1,1,1)
  var lightAmbient = RGBA(.2f,.2f,.2f,1)
  var lightDiffuse = RGBA(.6f,.6f,.6f,1)
  var lightSpecular = RGBA(.4f,.4f,.4f,1)
  var shininess = 1f
  var textureMix = 0f
  var alpha = 1f
  var fade = 0f

  def render(){

    try{
      shader.begin() 

      MatrixStack.clear()
      setMatrices()

      Renderer() = this

      if(active){
        // Shader.alpha = scene.alpha
        // Shader.fade = scene.fade

        if( scene.alpha < 1f ){ //TODO depth ordering, conflicts with depth flag
          // Shader.blend = true
          Gdx.gl.glEnable(GL20.GL_BLEND);
          Gdx.gl.glDisable( GL20.GL_DEPTH_TEST )
        }else {
          // Shader.blend = false
          Gdx.gl.glEnable( GL20.GL_DEPTH_TEST )
          Gdx.gl.glDisable( GL20.GL_BLEND )
        }

        if(depth) Gdx.gl.glEnable( GL20.GL_DEPTH_TEST )
        else Gdx.gl.glDisable( GL20.GL_DEPTH_TEST )

        scene.draw()
      }
      
      shader.end()

    } catch{ case e:Exception => println(e)
      println ("\n" + e.printStackTrace + "\n")
    }

  }


  def setLightUniforms(){
    shader.uniforms("u_lightingMix") = lightingMix
    shader.uniforms("u_textureMix") = textureMix
    shader.uniforms("u_lightPosition") = lightPosition
    shader.uniforms("u_lightAmbient") = lightAmbient
    shader.uniforms("u_lightDiffuse") = lightDiffuse
    shader.uniforms("u_lightSpecular") = lightSpecular
    shader.uniforms("u_shininess") = shininess
  }

  def setMatrices(){
    try{
      MatrixStack(camera)

      shader.uniforms("u_projectionViewMatrix") = MatrixStack.projectionModelViewMatrix() 
      shader.uniforms("u_modelViewMatrix") = MatrixStack.modelViewMatrix() 
      shader.uniforms("u_viewMatrix") = MatrixStack.viewMatrix() 
      shader.uniforms("u_modelMatrix") = MatrixStack.modelMatrix() 
      shader.uniforms("u_normalMatrix") = MatrixStack.normalMatrix()
      shader.uniforms("u_cameraPosition") = camera.nav.pos
      // shader.uniforms("u_color") = color
      shader.uniforms("u_alpha") = alpha
      shader.uniforms("u_fade") = fade
      setLightUniforms()
    } catch { case e:Exception => ()} //println(e)}
    shader.setUniforms() 
  }
  
  def setColor(c:RGBA){
    // if( shader.isEmpty ) return
    // color = c
    shader.uniforms("u_color") = c //olor
    // this().setUniformf("u_color", color.r, color.g, color.b, color.a)
  }
  def setColor(v:Vec3, a:Float){ setColor( RGBA(v,a) ) }

  // def setAlpha(f:Float) = {
    // alpha = f
    // this().setUniformf("u_alpha", alpha)
  // }

  def setMaterial(material:Material){
    
    material match {
      case m:ShaderMaterial => setBasicMaterial(m);
      case m:SpecularMaterial => setBasicMaterial(m); //lightingMix=1f; shininess = m.shininess
      case m:DiffuseMaterial => setBasicMaterial(m); //lightingMix=1f; shininess = 0f
      case m:NoMaterial => setMaterial(defaultMaterial)
      case m:BasicMaterial => setBasicMaterial(m)
      case _ => () //setMaterial(defaultMaterial)
    }
  }

  def setBasicMaterial(material:BasicMaterial){
    setColor(material.color)
    visible = material.visible
    blend = material.transparent

    wireframe = material.wireframe
    linewidth = material.linewidth

    val s = shader
    material.texture.foreach( (t) => {t.bind(0); s.uniforms("u_texture0")=0 } )
    textureMix = material.textureMix

    // var normalMap = None:Option[Texture]
    // var specularMap = None:Option[Texture]

    lightingMix = material.lightingMix
    shininess = material.shininess
  }

}