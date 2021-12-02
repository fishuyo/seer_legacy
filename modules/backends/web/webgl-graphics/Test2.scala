package seer 
import graphics._
import graphics.backend.webgl._
// import fiddle.Fiddle, Fiddle.println

import scalajs.js
import scala.scalajs.js.typedarray._
import scala.scalajs.js.annotation._
import org.scalajs.dom
import dom.raw.WebGLRenderingContext._

import java.nio.FloatBuffer

@JSExportTopLevel("Test2")
object Test2 {
  @JSExport
  def main(args: Array[String]): Unit = {
    var can: dom.html.Canvas = dom.document.createElement("canvas").asInstanceOf[dom.html.Canvas]
    dom.document.body.appendChild(can)
    can.width = dom.window.innerWidth.toInt
    can.height = dom.window.innerHeight.toInt - 60

    // val can = Fiddle.canvas
    val webgl = can.getContext("webgl2").asInstanceOf[dom.raw.WebGLRenderingContext]     
    Graphics() = new GraphicsImpl(webgl)

    val gl = Graphics().gl
    import gl._ 
    glClearColor(0.4f, 0.0f, 0.5f, 0.8f)
    glClear(COLOR_BUFFER_BIT)
    
    var vertText = "attribute vec2 position; void main() {gl_Position = vec4(position, 0, 1);}"
    var fragText = "precision highp float; uniform vec4 color; void main() {gl_FragColor = vec4(0, 1, 0, 1);}"
    val shader = new ShaderProgram().create(vertText, fragText)

    val vertices = new Float32Array(js.Array(
    -0.8f,-0.3f,  0.3f,-0.3f,  0.0f,0.3f,  0.2f,0.2f,
    0.6f,0.6f,  0.4f,-0.4f))

    var buffer = glGenBuffer()
    glBindBuffer(ARRAY_BUFFER, buffer)
    glBufferData(ARRAY_BUFFER, vertices.length, TypedArrayBuffer.wrap(vertices), STATIC_DRAW)

    shader.bind()
    val program = shader.id.get
    val color = glGetUniformLocation(program, "color")
    var temp2 = new Float32Array(js.Array[Float](0f, 1f, 0.5f, 1.0f))
    glUniform4fv(color, temp2.length, TypedArrayBuffer.wrap(temp2))

    println("here")

    val position = glGetAttribLocation(program, "position")
    println(s"position: $position")
    glEnableVertexAttribArray(position)
    glVertexAttribPointer(position, 2, FLOAT, false, 0, 0)
    glDrawArrays(TRIANGLES, 0, vertices.length / 2)   
    
    println("end")
    
  }
}