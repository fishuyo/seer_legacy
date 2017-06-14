
package com.fishuyo.seer
package graphics

import spatial._

abstract class Light(var color:RGB = RGB.white)

class DirectionalLight(var pos:Vec3 = Vec3(1)) extends Light

