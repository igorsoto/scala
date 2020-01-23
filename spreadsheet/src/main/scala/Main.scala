import java.io.FileOutputStream

import org.apache.poi.xssf.streaming.SXSSFWorkbook

object Main extends App {
  val totalSheets =  args(args.indexWhere(_ == "--sheets") + 1).toInt
  val totalColumns = args(args.indexWhere(_ == "--columns") + 1).toInt
  val totalRows = args(args.indexWhere(_ == "--rows") + 1).toInt

  def uuid() = java.util.UUID.randomUUID.toString

  def createSheet(workbook: SXSSFWorkbook, name: String): Unit = {
    val sheet = workbook.createSheet(name)
    val headerRow = sheet.createRow(0)

    (1 to totalColumns).foreach { index =>
      val cell = headerRow.createCell(index - 1)
      cell.setCellValue(s"COLUMN_$index")
    }

    (1 to totalRows).foreach { index =>
      val row = sheet.createRow(index)
      (0 to totalColumns - 1).foreach { columnIndex =>
        val cell = row.createCell(columnIndex)
        cell.setCellValue(columnIndex)
      }
    }
  }

  val start = System.nanoTime

  val workbook = new SXSSFWorkbook()
  workbook.setCompressTempFiles(true)

  (1 to totalSheets).foreach { index =>
    createSheet(workbook, s"SHEET$index")
  }

  val fileOut = new FileOutputStream(s"${uuid()}.xlsx")
  workbook.write(fileOut)
  fileOut.close()
  workbook.close()
  workbook.dispose()

  val end = System.nanoTime
  val elapsedTimeInSeconds = (end - start) / 1.0e9

  println(s"Elapsed time (seconds): $elapsedTimeInSeconds")
}
