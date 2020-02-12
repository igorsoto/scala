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

  "Why don't we need a lower bound for the replaceFirst method in Section 18.3, \"Bounds for Type Variables,\" on page 266"  should "replace the first component of a Pair[Person] with a Student." in {
    class Pair[T](val first: T, val second: T) {
      def replaceFirst(newFirst: T) = new Pair(newFirst, second)
    }

    class Person(val name: String)
    class Student(val fullName: String, val clazz: String) extends Person(name = fullName)

    val pair1 = new Pair(new Person(name = "Igor"), new Person(name = "Marcelo"))
    val student = new Student(fullName = "Maria", clazz = "15C")
    val pair2 = pair1.replaceFirst(student)

    pair2.first.name shouldEqual "Maria"
    student shouldBe a[Person]
    // We don't need a lower bound because we are replacing a supertype with a derived type
    // Lower type bounds declare a type to be a supertype of another type. (https://docs.scala-lang.org/tour/lower-type-bounds.html)
  }
}
