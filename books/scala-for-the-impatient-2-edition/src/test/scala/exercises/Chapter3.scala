package exercises

import org.scalatest._

class Chapter3 extends FlatSpec with Matchers {
  "2. Write a loop that swaps adjacent elements of an array of integers. For example, Array(1, 2, 3, 4, 5) becomes Array(2, 1, 4, 3, 5)" should "" in {
    val input = Array(1, 2, 3, 4, 5)
    for (index <- 0 until input.length - 1 by 2) {
      val current = input(index)
      input(index) = input(index + 1)
      input(index + 1) = current
    }
    input shouldEqual Array(2, 1, 4, 3, 5)
  }

  "3. Repeat the preceding assignment, but produce a new array with the swapped values. Use for/yield." should "" in {
    val input = Array(1, 2, 3, 4, 5)
    val output = for {
      i <- input.indices by 2
      j <- (i + 1 to i by -1) if j < input.size
    } yield input(j)

    output shouldEqual Array(2, 1, 4, 3, 5)
  }

  "foo" should "bar" in {

  }
}
