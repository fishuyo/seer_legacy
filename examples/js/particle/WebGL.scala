package com.fishuyo.seer
package js 

import org.scalajs._
import dom._
// import pUtil._

object WebGLApp extends scalajs.js.JSApp {
  def main(): Unit = {
    var can: html.Canvas = document.createElement("canvas").asInstanceOf[html.Canvas]
    document.body.appendChild(can)
    can.width = window.innerWidth.toInt
    can.height = window.innerHeight.toInt - 60

    import raw.WebGLRenderingContext._
    var gl: raw.WebGLRenderingContext = can.getContext("webgl").asInstanceOf[raw.WebGLRenderingContext]     
    gl.clearColor(0.4, 0.0, 0.5, 0.8)
    gl.clear(COLOR_BUFFER_BIT)     

    var vShader = gl.createShader(VERTEX_SHADER)     
    var vertText = """
      attribute vec4 position;
 
      void main() {
        gl_Position = position;
      }
    """
    gl.shaderSource(vShader, vertText)     
    gl.compileShader(vShader)     

    var fShader = gl.createShader(FRAGMENT_SHADER)
    var fragText = """
      precision highp float;
      uniform vec4 color;
      void main(){
        gl_FragColor = vec4(0, 1, 0, 1);
      }
    """
    gl.shaderSource(fShader, fragText)
    gl.compileShader(fShader)

    var program = gl.createProgram()
    gl.attachShader(program, vShader)
    gl.attachShader(program, fShader)
    gl.linkProgram(program)     

    var tempVertices: scalajs.js.Array[Float] = scalajs.js.Array[Float]()
    tempVertices.push(-0.3f,-0.3f,   0.3f,-0.3f,  0.0f,0.3f,  0.2f,0.2f,   0.6f, 0.6f,   0.4f, -0.4f)     
    import scalajs.js.typedarray.Float32Array
    var vertices: Float32Array = new Float32Array(tempVertices)

    var buffer = gl.createBuffer()
    gl.bindBuffer(ARRAY_BUFFER, buffer)
    gl.bufferData(ARRAY_BUFFER, vertices, STATIC_DRAW)

    gl.useProgram(program)
    var progDyn = program.asInstanceOf[scalajs.js.Dynamic]
    progDyn.color = gl.getUniformLocation(program, "color")
    var temp2 = scalajs.js.Array[Double]()
    temp2.push(0f, 1f, 0.5f, 1.0f)
    gl.uniform4fv(progDyn.color.asInstanceOf[raw.WebGLUniformLocation], temp2)

    progDyn.position = gl.getAttribLocation(program, "position")
    gl.enableVertexAttribArray(progDyn.position.asInstanceOf[Int])
    gl.vertexAttribPointer(progDyn.position.asInstanceOf[Int], 2, FLOAT, false, 0, 0)
    gl.drawArrays(TRIANGLES, 0, vertices.length / 2)     
  } 
}