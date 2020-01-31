package com.github.igorsoto

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{complete, get, parameter, pathPrefix}
import akka.http.scaladsl.server.Route
import com.github.igorsoto.MessagingActor.SendMessage

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import scala.util.Failure
import scala.util.Success

object State {
  private var requestsCounter: Int = 0
  private var messagesHandledCounter: Int = 0
  def getRequestsCounter() = requestsCounter
  def incrementRequestsCounter() = requestsCounter += 1
  def getMessagesHandledCounter() = messagesHandledCounter
  def incrementMessagesHandledCounter() = messagesHandledCounter += 1
  def reset() = {
    requestsCounter = 0
    messagesHandledCounter = 0
  }
}

object MessagingActor {

  final case class SendMessage(message: String)

  def apply(): Behavior[SendMessage] =
    Behaviors.setup { _ =>
      Behaviors.receiveMessage { message =>
        // println("Message handled: " + message)
        State.incrementMessagesHandledCounter
        Behaviors.same
      }
    }
}

//#main-class
object Main {
  //#start-http-server
  private def startHttpServer(routes: Route, system: ActorSystem[_]): Unit = {
    // Akka HTTP still needs a classic ActorSystem to start
    implicit val classicSystem: akka.actor.ActorSystem = system.toClassic
    import system.executionContext

    val futureBinding = Http().bindAndHandle(routes, "localhost", 8080)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }
  //#start-http-server
  def main(args: Array[String]): Unit = {
    val messagingActor: ActorSystem[MessagingActor.SendMessage] = ActorSystem(MessagingActor(), "MessagingActor")
    messagingActor.scheduler.scheduleWithFixedDelay(1 second, 1 second){
      () => {
        println(s"\nRequests: ${State.getRequestsCounter()}")
        println(s"Messages: ${State.getMessagesHandledCounter()}")
        State.reset
      }
    }

    //#server-bootstrapping
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      val route: Route =
        pathPrefix("messages") {
          parameter('message) { message =>
            get {
              State.incrementRequestsCounter
              messagingActor ! SendMessage(message)
              complete(s"message $message")
            }
          }
        }
      startHttpServer(route, context.system)
      Behaviors.empty
    }
    ActorSystem[Nothing](rootBehavior, "HelloAkkaHttpServer")
  }
}
//#main-class
