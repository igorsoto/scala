package exercises

import org.scalatest._

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
}

