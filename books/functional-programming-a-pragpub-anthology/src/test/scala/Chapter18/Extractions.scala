package Chapter18

import org.scalatest._

object Task {
  def unapply(taskInfo: String): Option[(String, String)] = {
    val parts = taskInfo.split("---")
    if (parts.size != 2) None
    else Some(parts(0), parts(1))
  }
}

class Extractions extends FlatSpec with Matchers {
  "Unapply" should "extract values" in {

    val input = "Monday --- integrate with the payment system"
    val output = input match {
      case Task(day, task) => s"Task for $day:$task"
      case _ => "Invalid task"
    }

    output shouldBe "Task for Monday : integrate with the payment system"
  }
}