package Chapter32

import org.scalatest.{FlatSpec, Matchers}
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Time {
  def code(block: () => Unit): Double = {
    val start = System.nanoTime
    block()
    val end = System.nanoTime
    val timeTaken = (end - start) / 1.0e9
    println(s"\n\n*** Time taken (seconds): $timeTaken ***\n\n")
    timeTaken
  }
}

object Test {
  def test(name: String): Unit = {
    Thread.sleep(1000)
    println(name)
  }
}

class Futures extends FlatSpec with Matchers {
  "When creating futures before the for expression" should "run in parallel" in {
    val fut1 = Future { Thread.sleep(1000); 21 + 21 }
    val fut2 = Future { Thread.sleep(1000); 23 + 23 }
    val fut3 = for {
      x <- fut1
      y <- fut2
    } yield x + y

    val timeTaken = Time.code(() => {
      val result = Await.result(fut3, 10.seconds)
      assert(result == 88)
    })

    assert(timeTaken > 1 && timeTaken < 2)
  }

  "When creating futures inside the for expression" should "serialize (will not run in parallel)" in {
    val fut = for {
      x <- Future { Thread.sleep(1000); 21 + 21 }
      y <- Future { Thread.sleep(1000); 23 + 23 }
    } yield x + y

    val timeTaken = Time.code(() => {
      val result = Await.result(fut, 10.seconds)
      assert(result == 88)
    })

    assert(timeTaken > 2 && timeTaken < 3)
  }

  "Foo" should "deu" in {
    val fut1 = Future { Test.test("fut1") }
    val fut2 = Future { Test.test("fut2") }
    val fut3 = for {
      _ <- fut1
      _ <- fut2
    } yield true

    val timeTaken = Time.code(() => {
      Await.result(fut3, 10.seconds)
    })

    println(s"Time taken... ${timeTaken}")

    assert(true)
  }
}
