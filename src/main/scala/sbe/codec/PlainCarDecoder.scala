package sbe.codec

import java.nio.file.{Files, Paths}

import io.github.aksharp.sbe.poc._
import org.agrona.concurrent.UnsafeBuffer

import scala.collection.JavaConverters._

object PlainCarDecoder {

  private val MESSAGE_HEADER_DECODER = new MessageHeaderDecoder
  private val CAR_DECODER = new CarDecoder

  def decodeFromFile[T](path: String)(f: StringBuilder => T): T = {
    val byteArray: Array[Byte] = Files.readAllBytes(Paths.get(path))
    val directBuffer = new UnsafeBuffer(byteArray)
    var bufferOffset = 0
    MESSAGE_HEADER_DECODER.wrap(directBuffer, bufferOffset)

    // Lookup the applicable flyweight to decode this type of message based on templateId and version.
    val templateId = MESSAGE_HEADER_DECODER.templateId
    if (templateId != CarEncoder.TEMPLATE_ID) throw new IllegalStateException("Template ids do not match")

    val actingBlockLength = MESSAGE_HEADER_DECODER.blockLength
    val actingVersion = MESSAGE_HEADER_DECODER.version

    bufferOffset += MESSAGE_HEADER_DECODER.encodedLength
    decode(CAR_DECODER, directBuffer, bufferOffset, actingBlockLength, actingVersion)(f)
  }

  @throws[Exception]
  private def decode[T](car: CarDecoder,
             directBuffer: UnsafeBuffer,
             bufferOffset: Int,
             actingBlockLength: Int,
             actingVersion: Int)(f: StringBuilder => T): T = {

    val buffer = new Array[Byte](128)
    val sb = new StringBuilder

    car.wrap(directBuffer, bufferOffset, actingBlockLength, actingVersion)

    sb.append("\ncar.serialNumber=").append(car.serialNumber)
    sb.append("\ncar.modelYear=").append(car.modelYear)
    sb.append("\ncar.available=").append(car.available)
    sb.append("\ncar.code=").append(car.code)

    sb.append("\ncar.someNumbers=")
    for (i <- 0 until CarEncoder.someNumbersLength) {
      sb.append(car.someNumbers(i)).append(", ")
    }

    sb.append("\ncar.vehicleCode=")
    for (i <- 0 until CarEncoder.vehicleCodeLength) {
      sb.append(car.vehicleCode(i).toChar)
    }

    val extras = car.extras
    sb.append("\ncar.extras.cruiseControl=").append(extras.cruiseControl)
    sb.append("\ncar.extras.sportsPack=").append(extras.sportsPack)
    sb.append("\ncar.extras.sunRoof=").append(extras.sunRoof)

    sb.append("\ncar.discountedModel=").append(car.discountedModel)

    val engine = car.engine
    sb.append("\ncar.engine.capacity=").append(engine.capacity)
    sb.append("\ncar.engine.numCylinders=").append(engine.numCylinders)
    sb.append("\ncar.engine.maxRpm=").append(engine.maxRpm)
    sb.append("\ncar.engine.manufacturerCode=")

    for (i <- 0 until EngineEncoder.manufacturerCodeLength) {
      sb.append(engine.manufacturerCode(i).toChar)
    }

    sb.append("\ncar.engine.efficiency=").append(engine.efficiency)
    sb.append("\ncar.engine.boosterEnabled=").append(engine.boosterEnabled)
    sb.append("\ncar.engine.booster.boostType=").append(engine.booster.boostType)
    sb.append("\ncar.engine.booster.horsePower=").append(engine.booster.horsePower)

    sb.append("\ncar.engine.fuel=").append(
      new String(buffer, 0, engine.getFuel(buffer, 0, buffer.length), "ASCII")
    )

    car.fuelFigures.iterator.asScala.foreach { fuelFigures =>
      sb.append("\ncar.fuelFigures.speed=").append(fuelFigures.speed)
      sb.append("\ncar.fuelFigures.mpg=").append(fuelFigures.mpg)
      sb.append("\ncar.fuelFigures.usageDescription=").append(fuelFigures.usageDescription)
    }

    car.performanceFigures.iterator.asScala.foreach { performanceFigures =>
      sb.append("\ncar.performanceFigures.octaneRating=").append(performanceFigures.octaneRating)

      performanceFigures.acceleration.iterator.asScala.foreach { acceleration =>
        sb.append("\ncar.performanceFigures.acceleration.mph=").append(acceleration.mph)
        sb.append("\ncar.performanceFigures.acceleration.seconds=").append(acceleration.seconds)
      }
    }

    sb.append("\ncar.manufacturer=").append(car.manufacturer)
    sb.append("\ncar.model=").append(
      new String(buffer, 0, car.getModel(buffer, 0, buffer.length), CarEncoder.modelCharacterEncoding)
    )

    val tempBuffer = new UnsafeBuffer(buffer)

    val tempBufferLength = car.getActivationCode(tempBuffer, 0, tempBuffer.capacity)
    sb.append("\ncar.activationCode=").append(
      new String(buffer, 0, tempBufferLength, CarEncoder.activationCodeCharacterEncoding)
    )
    sb.append("\ncar.encodedLength=").append(car.encodedLength)

    f(sb)
  }
}
