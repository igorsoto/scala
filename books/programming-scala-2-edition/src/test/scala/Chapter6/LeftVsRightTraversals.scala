package Chapter6

import org.scalatest.{FlatSpec, Matchers}

class LeftVsRightTraversals extends FlatSpec with Matchers {
  "reduceLeft" should "be tail recursive" in {

    def reduceLeft[A, B](s: Seq[A])(f: A => B): Seq[B] = {
      @annotation.tailrec
      def rl(accum: Seq[B], s2: Seq[A]): Seq[B] = s2 match {
        case head +: tail => rl(f(head) +: accum, tail)
        case _ => accum
      }

      rl(Seq.empty[B], s)
    }

    def double(i: Int): Int = i * 2

    val list = List(1, 2, 3)
    val list2 = reduceLeft(list)(double)

    assert(list2(0) == 6)
    assert(list2(1) == 4)
    assert(list2(2) == 2)
  }
}
