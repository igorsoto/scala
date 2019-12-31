package exercises

import org.scalatest._

import scala.annotation.tailrec

class Chapter2 extends FlatSpec with Matchers {
  "1. The signum of a number is 1 if the number is positive, -1 if it is negative, and 0 if is zero." should "Write a function that computes this value." in {
    object Positive {
      def unapply(x: Int): Option[Int] = if (x > 0) Some(x) else None
    }
    object Negative {
      def unapply(x: Int): Option[Int] = if (x < 0) Some(x) else None
    }

    def signum(x: Int): Int = x match {
      case Positive(_) => 1
      case Negative(_) => -1
      case _ => 0
    }

    signum(10) shouldBe (1)
    signum(-30) shouldBe (-1)
    signum(0) shouldBe (0)
  }

  "2. What is the value of an empty block expression {}?" should "What is its type?" in {
    {} shouldBe a[Unit]
  }

  "3. Come up with one situation where the assignment x = y = 1 is valid in Scala." should "(Hint: Pick a suitable type for x.)" in {
    // TODO
    true shouldBe true
  }

  "4. Write a Scala equivalent for the Java loop" should "for (int i = 10; i >= 0; i--) System.out.println(i)" in {
    def loop(f: Int => Unit): Unit = for (i <- 10 to 0 by -1) f(i)

    var seq = Seq[Int]()
    def f(x: Int): Unit = {
      seq = seq :+ x
      ()
    }

    loop(f)

    val matches = seq match {
      case Seq(10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0) => true
      case _ => false
    }

    matches shouldBe true
  }

  "5. Write a procedure countdown(n: Int)" should "prints the numbers from n to 0" in {
    var seq = Seq[Int]()
    def countdown(n: Int): Unit = seq = n to 0 by -1

    countdown(5)

    seq shouldEqual Seq(5, 4, 3, 2, 1, 0)
  }

  "6. Write a for loop for" should "computing the product of the Unicode codes of all letters in a string." in {
    val str: String = "Hello"
    var product: Long = 1L
    for (c <- str) product *= c.toLong
    product shouldEqual 9415087488L
  }

  "7. Solve the preceding exercise" should "without writing a loop." in {
    val str: String = "Hello"
    val product: Long = str.foldLeft(1L)(_*_.toLong)
    product shouldEqual 9415087488L
  }

  "8. Write a function product(s: String) that" should "computes the product, as described in the preceding exercises." in {
    def product(s: String) = s.foldLeft(1L)(_*_.toLong)
    val str: String = "Hello"
    product(str) shouldEqual 9415087488L
  }

  "9. Make the function of the preceding exercise a recursive function" should "be a recursive function" in {
    @tailrec
    def product(s: String, p: Long = 1L): Long = {
      s.headOption match {
        case Some(head) => product(s.tail, p * head.toLong)
        case _ => p
      }
    }
    val str: String = "Hello"
    product(str) shouldEqual 9415087488L
  }
}

