
package org.lwjgl.opengl


object SetDisplayStereo {
	def apply(){
		try{
			val d = Display.getDrawable.asInstanceOf[DrawableGL]
			val pf = d.getPixelFormat.asInstanceOf[PixelFormat]
			val pfs = pf.withStereo(true)
			// val attrs = new ContextAttribs(3, 2).withForwardCompatible(false).withProfileCore(true);
			d.setPixelFormat(pfs, null ) //attrs)
		} catch { case e:Exception => println("SetDisplayStereo hack failed.") }
	}
}