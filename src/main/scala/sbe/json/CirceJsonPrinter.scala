package sbe.json

import io.circe.generic.extras.Configuration
import io.circe.generic.extras.auto._
import io.circe.syntax._
import org.joda.time.DateTime
import sbe.codec.DomainCarDecoder
import sbe.domain.Car

object CirceJsonPrinter {

  implicit val discriminatorConfig: Configuration =
    Configuration.default.withDiscriminator("value")

  def printJson(car: Car) = {
    println(car.asJson.spaces2)
  }

  def decodeAndPrint(path: String)= {
    val start = DateTime.now.getMillis
    DomainCarDecoder.decodeFromFile(path)(printJson)
    val duration = DateTime.now.getMillis - start

    println(s"Circe: Duration = $duration millis")
  }

}
