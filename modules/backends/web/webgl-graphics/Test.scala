// // import fiddle.Fiddle, Fiddle.println
// import scalajs.js
// import scala.scalajs.js.typedarray._
// import scala.scalajs.js.annotation._
// import org.scalajs.dom
// import dom.raw.WebGLRenderingContext._

// @JSExportTopLevel("Test")
// object Test {
//   @JSExport
//   def main(args: Array[String]): Unit = {
//     var can: dom.html.Canvas = dom.document.createElement("canvas").asInstanceOf[dom.html.Canvas]
//     dom.document.body.appendChild(can)
//     can.width = dom.window.innerWidth.toInt
//     can.height = dom.window.innerHeight.toInt - 60

//     // val can = Fiddle.canvas
//     var gl: dom.raw.WebGLRenderingContext = can.getContext("webgl").asInstanceOf[dom.raw.WebGLRenderingContext]     
//     gl.clearColor(0.4, 0.0, 0.5, 0.8)
//     gl.clear(COLOR_BUFFER_BIT)
    
//     var vShader = gl.createShader(VERTEX_SHADER)     
//     var vertText = "attribute vec2 position; void main() {gl_Position = vec4(position, 0, 1);}"
//     gl.shaderSource(vShader, vertText)     
//     gl.compileShader(vShader)     

//     var fShader = gl.createShader(FRAGMENT_SHADER)
//     var fragText = "precision highp float; uniform vec4 color; void main() {gl_FragColor = vec4(0, 1, 0, 1);}"
//     gl.shaderSource(fShader, fragText)
//     gl.compileShader(fShader)

//     var program = gl.createProgram()
//     gl.attachShader(program, vShader)
//     gl.attachShader(program, fShader)
//     gl.linkProgram(program)     

//     val vertices = new Float32Array(js.Array(
//     -0.8f,-0.3f,  0.3f,-0.3f,  0.0f,0.3f,  0.2f,0.2f,
//     0.6f,0.6f,  0.4f,-0.4f))

//     var buffer = gl.createBuffer()
//     gl.bindBuffer(ARRAY_BUFFER, buffer)
//     gl.bufferData(ARRAY_BUFFER, vertices, STATIC_DRAW)

//     gl.useProgram(program)
//     var progDyn = program.asInstanceOf[scalajs.js.Dynamic]
//     progDyn.color = gl.getUniformLocation(program, "color")
//     var temp2 = scalajs.js.Array[Double]()
//     temp2.push(0f, 1f, 0.5f, 1.0f)
//     gl.uniform4fv(progDyn.color.asInstanceOf[dom.raw.WebGLUniformLocation], temp2)

//     progDyn.position = gl.getAttribLocation(program, "position")
//     gl.enableVertexAttribArray(progDyn.position.asInstanceOf[Int])
//     gl.vertexAttribPointer(progDyn.position.asInstanceOf[Int], 2, FLOAT, false, 0, 0)
//     gl.drawArrays(TRIANGLES, 0, vertices.length / 2)     
//   }
// }