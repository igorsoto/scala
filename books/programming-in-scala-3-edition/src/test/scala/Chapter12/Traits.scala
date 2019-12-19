package Chapter12

import org.scalatest.{FlatSpec, Matchers}

class Point(val x: Int, val y: Int)

trait Rectangular {
  def topLeft: Point
  def bottomRight: Point

  def left: Int = topLeft.x
  def right: Int = bottomRight.x
  def width: Int = right - left
}

abstract class Component extends Rectangular
class Rectangle(val topLeft: Point, val bottomRight: Point) extends Rectangular
class GraphicalWidget2D(val topLeft: Point, val bottomRight: Point) extends Component


abstract class IntQueue {
  def get(): Int
  def put(x: Int): Unit
}


import scala.collection.mutable.ArrayBuffer
class BasicIntQueue extends IntQueue {
  private val buf = new ArrayBuffer[Int]

  override def get(): Int = buf.remove(0)
  override def put(x: Int): Unit = { buf += x }
}

trait Doubling extends IntQueue {
  abstract override def put(x: Int): Unit = { super.put(2 * x) }
}

trait Filtering extends IntQueue {
  abstract override def put(x: Int): Unit = {
    if (x >= 0) super.put(x)
  }
}

class Traits extends FlatSpec with Matchers {
  "When behavior might be reused in multiple, unrelated classes" should "use a trait" in {
    val rectangle = new Rectangle(new Point(1, 1), new Point(10, 10))
    val graphicalWidget2D = new GraphicalWidget2D(new Point(2, 2), new Point(20, 20))

    assert(rectangle.width == 9)
    assert(graphicalWidget2D.width == 18)
  }

  "When you need stackable modifications" should "mix in traits" in {
    val queue = new BasicIntQueue with Filtering with Doubling

    queue.put(-1)
    queue.put(0)
    queue.put(1)

    assert(queue.get == 0)
    assert(queue.get == 2)
  }
}
