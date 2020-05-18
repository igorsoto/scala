package Chapter27

import org.scalatest.{FlatSpec, Matchers}
import scala.reflect.runtime.universe
import java.lang.annotation.{RetentionPolicy, Retention}
import annotation.meta.field

class AttribField() extends scala.annotation.StaticAnnotation
object MyTypes { type Attrib = AttribField @field }
import MyTypes._
case class TestClass(@Retention(RetentionPolicy.RUNTIME)
                     @Attrib version: Option[String])

class Annotations extends FlatSpec with Matchers {
  "User defined annotation" should "get annotation property value" in {
    def annotationsOf[T: universe.TypeTag](obj: T) = {
      universe.typeOf[T].members.foldLeft(Nil: List[universe.type#Annotation]) {
        (xs, x) => x.annotations ::: xs
      }
    }

    val user = TestClass(Some("IGOR"))
    println(annotationsOf(user))

    assert(true)
  }
}
