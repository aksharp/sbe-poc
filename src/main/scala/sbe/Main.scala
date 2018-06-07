package sbe

import java.io.File

import sbe.codec.PlainCarEncoder
import sbe.json.{CirceJsonPrinter, IrToJson}

object Main extends App {

  val fileName = "my_car.ir"
  val path = new File(fileName).getAbsolutePath
  val schemaPath = "src/main/resources/schema.sbeir"
  PlainCarEncoder.encodeToFile(path)

  // reads my_car.ir and prints Json via SBE Tool
  // less accurate (precision loss), some data loss (enums, bools), faster time (80 millis) when ir file doesn't exist and it runs first
  IrToJson.printJson(schemaPath)(path)

  // reads my_car.ir, decodes with DomainCarDecoder, converts to Json with Circe, prints Json
  // more accurate, no data loss, takes more time (360 millis) when ir file doesn't exist and it runs first
  CirceJsonPrinter.decodeAndPrint(path)








}
