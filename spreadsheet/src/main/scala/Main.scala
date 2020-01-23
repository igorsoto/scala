import java.io.FileOutputStream
import org.apache.poi.hssf.usermodel.HSSFWorkbook

case class User(id: Int, name: String)
case class Order(id: Int, amount: Double)

object Main extends App {
  def createUsersSheet(workbook: HSSFWorkbook): Unit = {
    val users = Seq(
      User(1, "Igor Campos"),
      User(2, "Marcelo Campos"),
      User(3, "Leidiane Campos")
    )
    val headerColumns = Seq("ID", "NAME")

    val sheet = workbook.createSheet("Users")
    val headerRow = sheet.createRow(0)

    headerColumns.zipWithIndex.foreach {
      case (column, index) => {
        val cell = headerRow.createCell(index)
        cell.setCellValue(column)
      }
    }

    users.zipWithIndex.foreach {
      case (user, index) => {
        val row = sheet.createRow(index + 1)
        val idCell = row.createCell(0)
        val nameCell = row.createCell(1)
        idCell.setCellValue(user.id)
        nameCell.setCellValue(user.name)
      }
    }
  }

  def createOrdersSheet(workbook: HSSFWorkbook): Unit = {
    val orders = Seq(
      Order(1, 1.99),
      Order(2, 3.20),
      Order(3, 1999.86)
    )
    val headerColumns = Seq("ID", "AMOUNT")

    val sheet = workbook.createSheet("Orders")
    val headerRow = sheet.createRow(0)

    headerColumns.zipWithIndex.foreach {
      case (column, index) => {
        val cell = headerRow.createCell(index)
        cell.setCellValue(column)
      }
    }

    orders.zipWithIndex.foreach {
      case (order, index) => {
        val row = sheet.createRow(index + 1)
        val idCell = row.createCell(0)
        val amountCell = row.createCell(1)
        idCell.setCellValue(order.id)
        amountCell.setCellValue(order.amount)
      }
    }
  }

  val workbook = new HSSFWorkbook()

  createUsersSheet(workbook)
  createOrdersSheet(workbook)

  val fileOut = new FileOutputStream("poi-generated-file.xls")
  workbook.write(fileOut)
  fileOut.close()
  workbook.close()

  println("End...")
}
