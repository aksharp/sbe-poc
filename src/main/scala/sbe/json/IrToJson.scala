package sbe.json

import java.nio.file.{Files, Paths}

import org.agrona.concurrent.UnsafeBuffer
import org.joda.time.DateTime
import uk.co.real_logic.sbe.ir.IrDecoder
import uk.co.real_logic.sbe.json.JsonPrinter
import sbe.util.AutoCloseable._

object IrToJson {

  def printJson(schemaPath: String)(irPath: String) = {
    val start = DateTime.now.getMillis
    withResources(new IrDecoder(schemaPath)) { irDecoder =>
      val ir = irDecoder.decode()
      val jsonPrinter = new JsonPrinter(ir)
      val output = new java.lang.StringBuilder()
      val bytes: Array[Byte] = Files.readAllBytes(Paths.get(irPath))
      jsonPrinter.print(output, new UnsafeBuffer(bytes), 0)
      println(output)
    }
    val duration = DateTime.now.getMillis - start

    println(s"SBE Tool: Duration = $duration millis")
  }

}
