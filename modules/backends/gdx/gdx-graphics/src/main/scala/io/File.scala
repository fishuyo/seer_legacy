
package seer
package io

import com.badlogic.gdx.Gdx

object File {

	def apply(path:String) = Gdx.files.internal(path)

}
