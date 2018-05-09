
package com.fishuyo.seer
package dynamic 

// package ammonite.integration
object TestMain {
  def main(args: Array[String]): Unit = {
    val hello = "Hello"
    // Break into debug REPL with
    ammonite.Main(
      // predefCode = "println(\"Starting Debugging!\")"
    ).run()
      // "hello" -> hello,
      // "fooValue" -> foo()
    // )
    // while(true){Thread.sleep(1000)}
  }

  def foo() = 1
}