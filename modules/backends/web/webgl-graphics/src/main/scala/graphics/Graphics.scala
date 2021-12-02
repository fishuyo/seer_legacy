package seer.graphics
package backend.webgl 

import org.scalajs.dom
import dom.raw.WebGLRenderingContext

class GraphicsImpl(webgl:WebGLRenderingContext) extends Graphics {

  val gl:GLES30 = new GLES30WebGLImpl(webgl) 

}