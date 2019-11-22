package com.github.igorsoto

import akka.util.ByteString

object DatabaseService {
  def fetchDataFromDB (page: Int): ByteString = {
    val batchSize = 1000
    ByteString.fromString(
      (1 to batchSize).map {
        index => s"outer index $page, inner index $index, column1, column2, column3, column4, column5\n"
      }.mkString
    )
  }
}