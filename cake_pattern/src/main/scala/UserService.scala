trait UserServiceComponent {
  this: UserRepositoryComponent =>

  val userService: UserService

  class UserService {
    def authenticate(username: String, password: String): User =
      userRepository.authenticate(User(username, password))

    def create(username: String, password: String) =
      userRepository.create(User(username, password))

    def delete(user: User) =
      userRepository.delete(user)
  }
}