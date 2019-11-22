package com.github.igorsoto

import akka.util.ByteString

object CsvLinesIterable extends Iterable[ByteString] {
  val pages = 1 to 1000

  override def iterator: Iterator[ByteString] = new Iterator[ByteString] {
    var pageIndex = 0

    override def hasNext: Boolean = pageIndex < pages.length

    override def next(): ByteString = {
      val currentPage = pages(pageIndex)
      pageIndex += 1
      DatabaseService.fetchDataFromDB(currentPage)
    }
  }
}