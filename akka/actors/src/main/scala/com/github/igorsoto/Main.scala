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
import scala.concurrent.blocking
import scala.language.postfixOps
import scala.util.Failure
import scala.util.Success
import java.util.concurrent.atomic.AtomicInteger

object State {
  private val requestsCounter: AtomicInteger = new AtomicInteger(0)
  private val messagesHandledCounter: AtomicInteger = new AtomicInteger(0)
  def getRequestsCounter() = requestsCounter.get
  def incrementRequestsCounter() = requestsCounter.incrementAndGet
  def getMessagesHandledCounter() = messagesHandledCounter.get
  def incrementMessagesHandledCounter() = messagesHandledCounter.incrementAndGet
  def reset() = {
    requestsCounter.set(0)
    messagesHandledCounter.set(0)
  }
}

object MessagingActor {

  final case class SendMessage(message: String)

  def apply(): Behavior[SendMessage] = Behaviors.receive { (context, message) =>
    blocking {
      State.incrementMessagesHandledCounter
    }
    Behaviors.same
  }

}

object Main {
  private def startHttpServer(routes: Route, system: ActorSystem[_]): Unit = {
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
  def main(args: Array[String]): Unit = {
    val messagingActor: ActorSystem[MessagingActor.SendMessage] = ActorSystem(MessagingActor(), "MessagingActor")
    messagingActor.scheduler.scheduleWithFixedDelay(1 second, 1 second){
      () => {
        println(s"\nRequests: ${State.getMessagesHandledCounter}")
        println(s"Messages: ${State.getMessagesHandledCounter}")
        State.reset
      }
    }

    val rootBehavior = Behaviors.setup[Nothing] { context =>
      val route: Route =
        pathPrefix("messages") {
          parameter('message) { message =>
            get {
              blocking {
                State.incrementRequestsCounter
              }
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
