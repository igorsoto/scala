object Main extends App {
  val userService = ComponentRegistry.userService
  val user = userService.authenticate("igorsoto", "123")
}
