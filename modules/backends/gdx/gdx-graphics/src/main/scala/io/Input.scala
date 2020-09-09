
package seer
package io

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.input._

class Input extends InputAdapter
object Inputs extends InputMultiplexer {

  def contains(in:InputAdapter) = getProcessors().contains(in, true)
  def add(in:InputAdapter) = if(!contains(in)) addProcessor(in)
  def remove(in:InputAdapter) = removeProcessor(in)
  def +=(in:InputAdapter) = add(in)
  def -=(in:InputAdapter) = remove(in)
}
