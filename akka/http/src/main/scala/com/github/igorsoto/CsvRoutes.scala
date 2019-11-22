package com.github.igorsoto

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{ContentTypes, HttpResponse, ResponseEntity}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.HttpEntity.Chunked
import akka.stream.scaladsl.Source
import akka.util.ByteString

object CsvRoutes {
  val routes: Route =
    pathPrefix("csv") {
      get {
        val iterator = () => CsvLinesIterable.iterator
        val chunks: Source[ByteString,_] = Source.fromIterator(iterator)
        val entity: ResponseEntity = Chunked.fromData(ContentTypes.`text/csv(UTF-8)`, chunks)
        val httpResponse : HttpResponse = HttpResponse(entity=entity)
        complete(httpResponse)
      }
    }
}
