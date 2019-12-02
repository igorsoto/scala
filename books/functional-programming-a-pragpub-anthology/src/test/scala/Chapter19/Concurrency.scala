package Chapter19

import java.util.concurrent.atomic.AtomicInteger

import org.scalatest.{FlatSpec, Matchers}

object Time {
  def code(block: () => Unit): Double = {
    val start = System.nanoTime
    block()
    val end = System.nanoTime
    (end - start) / 1.0e9
  }
}

class Concurrency extends FlatSpec with Matchers {
  private def checkStatus(name: String): String = {
    val milliseconds = 1000
    Thread.sleep(milliseconds)
    String.format("%s's status", name)
  }

  private def isConnected(name: String) = {
    val milliseconds = 1000
    Thread.sleep(milliseconds)
    name.length < 6
  }

  private def addToConnected(connected: Int, name: String): Int =
    connected + (if (isConnected(name)) 1 else 0)

  "map in sequential collection" should "delay in proportion to the number of elements" in {
    val names = List("Jane", "Jacob", "Brenda", "Brad")
    val timeTaken = Time.code { () => names.map { checkStatus } }
    assert(timeTaken > 4 && timeTaken < 5)
  }

  "map in parallel collection" should "not delay in proportion to the number of elements" in {
    val names = List("Jane", "Jacob", "Brenda", "Brad")
    val timeTaken = Time.code { () => names.par.map { checkStatus } }
    assert(timeTaken > 1 && timeTaken < 2)
  }

  "forall in sequential collection" should "iterate until condition fails" in {
    val names = List("Jane", "Jacob", "Brenda", "Brad")
    var counterCalls = 0
    val timeTaken = Time.code { () => {
        val allConnected = names.forall { name =>
          counterCalls += 1
          isConnected(name)
        }
        assert(allConnected == false)
      }
    }
    assert(counterCalls == 3)
    assert(timeTaken > 3 && timeTaken < 4)
  }

  "forall in parallel collection" should "evaluate isConnected function for all elements" in {
    val names = List("Jane", "Jacob", "Brenda", "Brad")
    val initialValue = 0
    val counterCalls: AtomicInteger = new AtomicInteger(initialValue)
    val timeTaken = Time.code { () => {
        val allConnected = names.par.forall { name =>
          counterCalls.incrementAndGet
          isConnected(name)
        }
        assert(allConnected == false)
      }
    }
    assert(counterCalls.get == 4)
    assert(timeTaken > 1 && timeTaken < 2)
  }

  "foldLeft in sequential collection" should "delay in proportion to the number of elements" in {
    val names = List("Jane", "Jacob", "Brenda", "Brad")
    val timeTaken = Time.code { () => {
        val folksConnected = names.foldLeft(0) { addToConnected }
        assert(folksConnected == 3)
      }
    }
    assert(timeTaken > 4 && timeTaken < 5)
  }

  "foldLeft in parallel collection" should "delay in proportion to the number of elements" in {
    val names = List("Jane", "Jacob", "Brenda", "Brad")
    val timeTaken = Time.code { () => {
        val folksConnected = names.par.foldLeft(0) { addToConnected }
        assert(folksConnected == 3)
      }
    }
    assert(timeTaken > 4 && timeTaken < 5)
  }

  "find in sequential collection" should "evaluate isConnected function once" in {
    val names = List("Jane", "Jacob", "Brenda", "Brad")
    val timeTaken = Time.code { () => {
        var counterCalls = 0
        val found = names.find { name => counterCalls += 1; isConnected(name) }
        assert(found.get == "Jane")
        assert(counterCalls == 1)
      }
    }
    assert(timeTaken > 1 && timeTaken < 2)
  }

  "find in parallel collection" should "evaluate isConnected function for all elements" in {
    val names = List("Jane", "Jacob", "Brenda", "Brad")
    val timeTaken = Time.code { () => {
        val initialValue: Int = 0
        val counterCalls = new AtomicInteger(initialValue)
        val found = names.par.find { name => counterCalls.incrementAndGet(); isConnected(name) }
        assert(found.get == "Jane")
        assert(counterCalls.get == 4)
      }
    }
    assert(timeTaken > 1 && timeTaken < 2)
  }
}
