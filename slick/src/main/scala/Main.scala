import java.time.ZonedDateTime

import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Main extends App {
  val db = Database.forConfig("h2mem1")

  try {
    class Suppliers(tag: Tag) extends Table[(Int, String, String, String, String, String)](tag, "SUPPLIERS") {
      def id = column[Int]( "SUP_ID", O.PrimaryKey)
      def name = column[String]("SUP_NAME")
      def street = column[String]("STREET")
      def city = column[String]("CITY")
      def state = column[String]("STATE")
      def zip = column[String]("ZIP")
      def * = (id, name, street, city, state, zip)
    }

    val suppliers = TableQuery[Suppliers]

    class Coffees(tag: Tag) extends Table[(String, Int, Double, Int, Int)](tag, "COFFEES") {
      def name = column[String]("COF_NAME", O.PrimaryKey)
      def supID = column[Int]("SUP_ID")
      def price = column[Double]("PRICE")
      def sales = column[Int]("SALES")
      def total = column[Int]("TOTAL")
      def * = (name, supID, price, sales, total)
      def supplier = foreignKey("SUP_FK", supID, suppliers)(_.id)
    }

    val coffees = TableQuery[Coffees]

    val setup = DBIO.seq(
      (suppliers.schema ++ coffees.schema).create,

      suppliers += (101, "Acme, Inc.",      "99 Market Street", "Groundsville", "CA", "95199"),
      suppliers += ( 49, "Superior Coffee", "1 Party Place",    "Mendocino",    "CA", "95460"),
      suppliers += (150, "Superior Coffee", "100 Coffee Lane",  "Meadows",      "CA", "93966"),

      // Insert some coffees (using JDBC's batch insert feature, if supported by the DB)
      coffees ++= Seq(
        ("Colombian",         101, 7.99, 0, 0),
        ("French_Roast",       49, 8.99, 0, 0),
        ("Espresso",          150, 9.99, 0, 0),
        ("Colombian_Decaf",   101, 8.99, 0, 0),
        ("French_Roast_Decaf", 49, 9.99, 0, 0)
      ),
    )

    val setupFuture = db.run(setup)

    val f = setupFuture.flatMap { _ =>
      // 1 - Simple Query

      println("1 - Simple Query")
      println("Coffees")

      db.run(coffees.result).map(_.foreach {
        case (name, supID, price, sales, total) =>
          println(" " + name + "\t" + supID + "\t" + price + "\t" + sales + "\t" + total)
      })
    }.flatMap { _ =>
      // 2 - Adding a projection

      println("\n\n2 - Adding a projection")
      println("Concatenated coffees")

      val q1 = for (c <- coffees)
        yield LiteralColumn(" ") ++
          c.name ++ "\t" ++
          c.supID.asColumnOf[String] ++ "\t" ++
          c.price.asColumnOf[String] ++ "\t" ++
          c.total.asColumnOf[String]

      db.stream(q1.result).foreach(println)
    }.flatMap { _ =>
      // 3 - Joining and filtering tables

      println("\n\n3 - Joining and filtering tables")

      val q2 = for {
        c <- coffees if c.price < 9.0
        s <- suppliers if s.id === c.supID
      } yield (c.name, s.name)

      db.stream(q2.result).foreach(println)
    }.flatMap { _ =>
      // 4 - Joining using foreign key directly

      println("\n\n4 - Joining using foreign key directly")

      val q3 = for {
        c <- coffees if c.price < 9.0
        s <- c.supplier
      } yield (c.name, s.name)

      db.stream(q3.result).foreach(println)
    }.flatMap { _ =>
      // 5 - Simple Aggregation

      println("\n\n5 - Aggregations")

      val maxPriceQuery: Rep[Option[Double]] = coffees.map(_.price).max
      db.run(maxPriceQuery.result).map(_.foreach {
        case max => println(max + "\t")
      })
    }.flatMap { _ =>
      // 6 - Sorting / Order By

      println("\n\n6 - Sorting / Order By")

      val sortByPriceQuery: Query[Coffees, (String, Int, Double, Int, Int), Seq] =
        coffees.sortBy(_.price)

      db.run(sortByPriceQuery.result).map(println)
    }.flatMap { _ =>
      // 7 - Deleting

      println("\n\n7 - Deleting")

      val deleteQuery: Query[Coffees, (String, Int, Double, Int, Int), Seq] =
        coffees.filter(_.price < 8.0)

      val deleteAction = deleteQuery.delete

      db.run(deleteAction).map { numDeletedRows =>
        println(s"Deleted $numDeletedRows rows")
      }
    }

    Await.result(f, Duration.Inf)
  } finally db.close
}