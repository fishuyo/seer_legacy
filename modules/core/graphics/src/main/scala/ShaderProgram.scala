package seer
package graphics



class ShaderProgram {
  implicit val gl = Graphics().gl

  var id:Option[Int] = None
  var vid:Option[Int] = None
  var fid:Option[Int] = None
  
  def init() = {
    id = Some(gl.glCreateProgram())
    vid = Some(gl.glCreateShader(gl.GL_VERTEX_SHADER))
    fid = Some(gl.glCreateShader(gl.GL_FRAGMENT_SHADER))
  }

  def loadSource(vert:String, frag:String) = {
    vid.foreach(gl.glShaderSource(_, vert))
    fid.foreach(gl.glShaderSource(_, frag))
  }

  def compile() = {
    vid.foreach(gl.glCompileShader(_))
    fid.foreach(gl.glCompileShader(_))
  }

  def link() = {
    id.foreach { case i =>
      gl.glAttachShader(i, vid.get)
      gl.glAttachShader(i, fid.get)
      gl.glLinkProgram(i)
    }
  }

  def create(v:String,f:String) = {
    init()
    loadSource(v,f)
    compile()
    link()
    this
  }

  def bind() = id.foreach(gl.glUseProgram(_))
  def unbind() = gl.glUseProgram(0)



}