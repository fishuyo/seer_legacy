
class FlowTest extends SeerActor {

  val joy = Device("Joy-Con (L)", 0)
  joy.L >> Print

}

classOf[FlowTest]