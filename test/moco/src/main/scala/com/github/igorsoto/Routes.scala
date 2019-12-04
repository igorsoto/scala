package com.github.igorsoto

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, StatusCodes, Uri}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import scala.util.{Failure, Success}
import scala.concurrent.Future
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.StatusCodes.OK

import scala.concurrent.ExecutionContext.Implicits.global

case class CompanyData(foo: String)

object Routes {
  def getDependentResource(filter: String): Future[String] = {
    implicit val system = ActorSystem()

    val request = HttpRequest(GET, uri = Uri(s"http://localhost:5000/resource?filter=$filter"))

    Http().singleRequest(request) flatMap { res =>
      res.status match {
        case OK => Future.successful("OK")
        case _ => Future.failed(new NoSuchElementException)
      }
    }
  }

  val routes: Route =
    pathPrefix("users") {
      get {
        val future = for {
          _ <- getDependentResource("foo")
          _ <- getDependentResource("bar")
        } yield "OK"

        onComplete(future) {
          case Success(_) => complete(StatusCodes.OK, "OK")
          case Failure(_) => complete(StatusCodes.InternalServerError, "Error")
        }
      }
    }
}
