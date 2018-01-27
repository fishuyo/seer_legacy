
package com.fishuyo.seer
package graphics


object Graphics {
  var interface:Option[Graphics] = None
  def apply() = interface.get //OrElse({ interface = Some(new NullAudioInterface()); interface.get })
}


trait Graphics extends GL30 {





}