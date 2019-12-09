package com.example

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.actor.typed.ActorSystem
import spray.json.DefaultJsonProtocol

trait PayloadBase {
  val companyId: Int
}

class UserRoutes()(implicit val system: ActorSystem[_]) {

  private val random = scala.util.Random

  private def checkAuth(): Boolean = {
    Thread.sleep(1000)
    random.nextInt % 2 == 0
  }

  val legacyUsersRouteBase = path("company" / IntNumber / "users")
  val usersRouteBase = path("users")

  val getUsersRouteBase = legacyUsersRouteBase & get & authorize(checkAuth)
  val postUsersRouteBase = usersRouteBase & post & entity(as[PayloadBase]) & authorize(checkAuth)

  val getUserRoute = getUsersRouteBase { companyId =>
    // TODO: Implement
    complete(s"GET OK $companyId")
  }

  val postUserRoute = postUsersRouteBase { requestEntity =>
    // TODO: Implement
    complete(s"POST OK ${requestEntity.companyId}")
  }

  val userRoutes: Route = getUserRoute ~ postUserRoute
}
