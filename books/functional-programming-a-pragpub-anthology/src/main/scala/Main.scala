case class StockPrice(ticker: String, price: Double) {
  def print: Unit = println(s"Top stock is $ticker at price $price")
}

object Main extends App {
  def getPrice(ticker: String): StockPrice = {
    val url = s"http://download.finance.yahoo.com/d/quotes.csv?s=${ticker}&f=snbaopl1"
    val data = io.Source.fromURL(url).mkString
    val price = data.split(",")(4).toDouble
    StockPrice(ticker, price)
  }

  val tickers = List("AAPL", "AMD", "CSCO", "GOOG", "HPQ", "INTC",
    "MSFT", "ORCL", "QCOM", "XRX")

  tickers.foreach {
    ticker => {
      getPrice(ticker).print
    }
  }
}
