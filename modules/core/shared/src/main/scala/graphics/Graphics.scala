
package com.fishuyo.seer
package graphics


object Graphics {
  var interface:Option[Graphics] = None
  def apply() = interface.get
  def update(g:Graphics) = interface = Some(g)
}


trait Graphics {

  def gl:GLES30




}