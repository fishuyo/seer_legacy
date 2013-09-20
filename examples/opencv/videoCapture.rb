require 'java'

module M
  include_package "com.fishuyo.io"
  include_package "com.fishuyo.maths"
  include_package "com.fishuyo.graphics"
  include_package "com.fishuyo.examples.opencv.cap"
end

class Object
  class << self
    alias :const_missing_old :const_missing
    def const_missing c
      M.const_get c
    end
  end
end


def step dt
  # Main.bgsub.updateBackgroundNextFrame()
  Main.bgsub.setThreshold(50.0)
end