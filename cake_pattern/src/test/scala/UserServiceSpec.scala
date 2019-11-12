import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

trait TestEnvironment extends
  UserServiceComponent with
  UserRepositoryComponent with
  MockFactory {

  val userRepositoryMock = mock[UserRepository]
  val userServiceMock = mock[UserService]

  val userRepository = userRepositoryMock
  val userService = userServiceMock
}

class UserServiceSpec
  extends FlatSpec
    with Matchers
    with TestEnvironment {

  "authenticate" should "call repository once" in {
    (userRepositoryMock.authenticate _)
      .expects(User("test", "test"))
      .returning(User("test", "test"))
      .once

    val service = new UserService
    val user = service.authenticate("test", "test")
    user.username shouldBe "test"
  }
}