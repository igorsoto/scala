package exercises

import org.scalatest._

class Chapter18 extends FlatSpec with Matchers {
  "1. Define an immutable class Pair[T, S] with a method swap" should "returns a new pair with the components swapped." in {
    class Pair[T, S](val first: T, val second: S) {
      def swap() = new Pair[S, T](second, first)
    }

    val pair = new Pair[Int, Int](1, 2);
    pair.first shouldEqual 1
    pair.second shouldEqual 2

    val newPair = pair.swap
    newPair.first shouldEqual 2
    newPair.second shouldEqual 1
    pair.first shouldEqual 1
    pair.second shouldEqual 2
  }

  "2. Define a mutable class Pair[T] with a method swap" should "swaps the components of the pair." in {
    class Pair[T](var first: T, var second: T) {
      def swap() = {
        val oldFirst = first
        first = second
        second = oldFirst
      }
    }

    val pair = new Pair[Int](1, 2)
    pair.first shouldEqual 1
    pair.second shouldEqual 2

    pair.swap
    pair.first shouldEqual 2
    pair.second shouldEqual 1
  }

  "3. Given a class Pair[T, S], write a generic method swap that takes a pair as its argument" should "returns a new pair with the components swapped." in {
    class Pair[T, S](val first: T, val second: S) {
      def swap[S, T](pair: Pair[T, S]) = new Pair[S, T](pair.second, pair.first)
    }

    val pair: Pair[Int, Double] = new Pair[Int, Double](1, 2)
    pair.first shouldEqual 1
    pair.second shouldEqual 2.0

    val newPair: Pair[Double, Int] = pair.swap(pair)
    newPair.first shouldEqual 2.0
    newPair.second shouldEqual 1
  }
}
