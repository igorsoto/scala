package Chapter18

import org.scalatest._

object Task {
  def unapply(taskInfo: String): Option[(String, String)] = {
    val parts = taskInfo.split("---")
    if (parts.size != 2) None
    else Some(parts(0), parts(1))
  }
}

object DayOfWeek {
  def unapply(day: String): Boolean =
    List("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday") contains day.trim
}

class Extractions extends FlatSpec with Matchers {
  def processTask(taskInfo: String): String = taskInfo match {
    case Task(day, task) => s"Task for $day:$task"
    case _ => "Invalid task"
  }

  def processTaskImproved(taskInfo: String): String = taskInfo match {
    case Task(day @ DayOfWeek(), task) => s"Task for $day:$task"
    case _ => "Invalid task"
  }

  "processTask" should "extract values when format is valid" in {
    val input = "Monday --- integrate with the payment system"
    val output = processTask(input)
    output shouldBe "Task for Monday : integrate with the payment system"
  }

  "processTask" should "not extract values when format is invalid" in {
    val input = "Wednesday -- hack code with no tests"
    val output = processTask(input)
    output shouldBe "Invalid task"
  }

  "processTask" should "not extract values when day is invalid" in { // Should fail
    val input = "Frday --- discuss discount rates"
    val output = processTask(input)
    output shouldBe "Invalid task"
  }

  "processTaskImproved" should "not extract values when day is invalid" in {
    val input = "Frday --- discuss discount rates"
    val output = processTaskImproved(input)
    output shouldBe "Invalid task"
  }

  "processTaskImproved" should "extract values when format is valid" in {
    val input = "Monday --- integrate with the payment system"
    val output = processTaskImproved(input)
    output shouldBe "Task for Monday : integrate with the payment system"
  }

  "processTaskImproved" should "not extract values when format is invalid" in {
    val input = "Wednesday -- hack code with no tests"
    val output = processTaskImproved(input)
    output shouldBe "Invalid task"
  }
}