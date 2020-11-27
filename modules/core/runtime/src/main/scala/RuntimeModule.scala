
package seer
package runtime


trait RuntimeModule {
  def init()
  def cleanup()
}


trait ModuleSync extends RuntimeModule {
  def step()
}


trait ModuleAsync extends RuntimeModule {

}