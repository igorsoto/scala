package com.github.igorsoto

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import com.github.dreamhead.moco._

class RoutesSpec extends WordSpec with Matchers with ScalatestRouteTest {
  "When route is called" should {
    "call dependent service" in {
      val port = 5000
      val stubServer = Moco.httpServer(port)
      val stubServerRunner = Runner.runner(stubServer)
      stubServerRunner.start

      val responseContentTypeKey = "Content-Type"
      val responseContentTypeValue = "application/json"
      val responsePayload = """{"foo":"bar"}"""
      val requestResource = "/resource"
      val requestQueryParam = "filter"
      val requestQueryParamFoo = "foo"
      val requestQueryParamBar = "bar"

      try {
        val responseHeader = Moco.header(responseContentTypeKey, responseContentTypeValue)
        stubServer.get(Moco.and(
          Moco.by(Moco.uri(requestResource)),
          Moco.eq(Moco.query(requestQueryParam), requestQueryParamFoo)
        )).response(Moco.`with`(responsePayload), responseHeader)
        stubServer.get(Moco.and(
          Moco.by(Moco.uri(requestResource)),
          Moco.eq(Moco.query(requestQueryParam),  requestQueryParamBar)
        )).response(Moco.`with`(responsePayload), responseHeader)

        Get("/users") ~> Routes.routes ~> check {
          responseAs[String] shouldEqual "OK"
        }
      } finally {
        stubServerRunner.stop
      }
    }
  }
}
