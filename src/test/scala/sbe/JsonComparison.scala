package sbe

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.{Files, Paths}

import io.circe.generic.extras.Configuration
import io.circe.generic.extras.auto._
import io.circe.parser.parse
import io.circe.syntax._
import io.circe.{Decoder, HCursor, Json}
import org.agrona.concurrent.UnsafeBuffer
import org.scalatest.{Matchers, WordSpec}
import sbe.codec.{DomainCarDecoder, PlainCarEncoder}
import sbe.util.AutoCloseable.withResources
import uk.co.real_logic.sbe.ir.IrDecoder
import uk.co.real_logic.sbe.json.JsonPrinter

import scala.io.Source

class JsonComparison extends WordSpec with Matchers {

  implicit val discriminatorConfig: Configuration =
    Configuration.default.withDiscriminator("value")

  "SBE and Circe Json comparison" in {
    val fileName = "my_car.ir"
    val path = new File(fileName).getAbsolutePath
    val schemaPath = "src/main/resources/schema.sbeir"
    PlainCarEncoder.encodeToFile(path)

    val sbeJson: Json = getJson(schemaPath)(path)
    val circeJson: Json = DomainCarDecoder.decodeFromFile(path)(_.asJson)

    val cursorCirce: HCursor = circeJson.hcursor
    val cursorSbe: HCursor = sbeJson.hcursor

    val serialNumberCirce: Decoder.Result[Int] = cursorCirce.downField("serialNumber").as[Int]
    println(s"serialNumberCirce $serialNumberCirce")

    val serialNumberSBE: Decoder.Result[Int] = cursorSbe.downField("serialNumber").as[Int]
    println(s"serialNumberSbe $serialNumberSBE")

    println(sbeJson.noSpaces)
    println(circeJson.noSpaces)

    sbeJson should be (circeJson)
  }

  private def getJson(schemaPath: String)(irPath: String): Json = {
    withResources(new IrDecoder(schemaPath)) { irDecoder =>
      val ir = irDecoder.decode()
      val jsonPrinter = new JsonPrinter(ir)
      val output = new java.lang.StringBuilder()
      val bytes: Array[Byte] = Files.readAllBytes(Paths.get(irPath))
      jsonPrinter.print(output, new UnsafeBuffer(bytes), 0)

      // saving generated json to file, so you can visualize it
      val file = new File("json_by_json_printer.json")
      withResources(new BufferedWriter(new FileWriter(file))) { bw =>
        bw.write(output.toString)
      }

      val str = Source.fromFile(file).getLines().toList.mkString
        // YIKES! need to do this to fix bad encoding by JsonPrinter
        .replace("Petrol", "\"Petrol\"")

      parse(str) match {
        case Right(json) => json
        case Left(err) => Json.fromString(s"""{ "error": "${err.getMessage()}" }""")
      }

    }
  }
}
