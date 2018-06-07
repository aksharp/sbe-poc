package sbe.codec

import java.nio.file.{Files, Paths}

import io.github.aksharp.sbe.poc._
import org.agrona.concurrent.UnsafeBuffer
import sbe.domain._

import scala.collection.JavaConverters._

object DomainCarDecoder {

  private val MESSAGE_HEADER_DECODER = new MessageHeaderDecoder
  private val CAR_DECODER = new CarDecoder

  def decodeFromFile[T](path: String)(f: Car => T): T = {
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
                actingVersion: Int)(f: Car => T): T = {

    val buffer = new Array[Byte](128)

    car.wrap(directBuffer, bufferOffset, actingBlockLength, actingVersion)

    val serialNumber: Int = car.serialNumber().toInt
    val modelYear: Int = car.modelYear()
    val available: Boolean = toBool(car.available)
    val code: Option[sbe.domain.Model] = toModel(car.code)

    val someNumbersArr = new Array[Long](CarEncoder.someNumbersLength)
    for (i <- 0 until CarEncoder.someNumbersLength) {
      someNumbersArr(i) = car.someNumbers(i)
    }
    val someNumbers = someNumbersArr.toSeq

    val vehicleCodeArr = new Array[Char](CarEncoder.vehicleCodeLength)
    for (i <- 0 until CarEncoder.vehicleCodeLength) {
      vehicleCodeArr(i) = car.vehicleCode(i).toChar
    }
    val vehicleCode: String = String.valueOf(vehicleCodeArr)

    val extras: Seq[OptionalExtras] = {
      val cruiseControl = if (car.extras.cruiseControl) Option(CruiseControl) else None
      val sportsPack = if (car.extras.sportsPack) Option(SportsPack) else None
      val sunRoof = if (car.extras.sunRoof) Option(SunRoof) else None
      (cruiseControl :: sportsPack :: sunRoof :: Nil).flatten
    }

    val discountedModel: Option[sbe.domain.Model] = toModel(car.discountedModel)

    val engine = car.engine
    val capacity: Int = engine.capacity
    val cylinders: Int = engine.numCylinders
    val maxRpm: Int = engine.maxRpm

    val engineManufacturerCodeArr = new Array[Char](EngineEncoder.manufacturerCodeLength)
    for (i <- 0 until EngineEncoder.manufacturerCodeLength) {
      engineManufacturerCodeArr(i) = engine.manufacturerCode(i).toChar
    }
    val manufacturerCode: String = String.valueOf(engineManufacturerCodeArr)

    val efficiency: Int = engine.efficiency

    val boosterEnabled: Boolean = toBool(engine.boosterEnabled)
    val booster: Option[Booster] = if (toBool(engine.boosterEnabled)) {
      val horsepower: Int = engine.booster.horsePower
      engine.booster.boostType match {
        case BoostType.TURBO => Option(Turbo(horsepower))
        case BoostType.SUPERCHARGER => Option(Supercharger(horsepower))
        case BoostType.NITROUS => Option(Nitros(horsepower))
        case BoostType.KERS => Option(Kers(horsepower))
        case BoostType.NULL_VAL => None
      }
    } else None

    val fuel: String = new String(buffer, 0, engine.getFuel(buffer, 0, buffer.length), "ASCII") // car.engine.fuel

    val engine2 = Engine(
      capacity,
      cylinders,
      maxRpm,
      manufacturerCode,
      fuel,
      efficiency,
      boosterEnabled,
      booster
    )

    val fuelFigures: Seq[FuelFigures] = car.fuelFigures.iterator.asScala.foldLeft(Seq.empty[FuelFigures]) {
      (acc, fuelFigures) =>
        acc :+ FuelFigures(
          speed = fuelFigures.speed,
          mpg = fuelFigures.mpg,
          usageDescription = fuelFigures.usageDescription
        )
    }

    val performanceFigures: Seq[PerformanceFigures] = car.performanceFigures.iterator.asScala.foldLeft(Seq.empty[PerformanceFigures]) {
      (pfAcc, performanceFigures) =>
        pfAcc :+ PerformanceFigures(
          octaneRating = performanceFigures.octaneRating,
          accelerations = performanceFigures.acceleration.iterator.asScala.foldLeft(Seq.empty[Acceleration]) {
            (aAcc, acceleration) =>
              aAcc :+ Acceleration(
                mph = acceleration.mph,
                seconds = acceleration.seconds
              )
          }
        )
    }

    val manufacturer: String = car.manufacturer()

    val model: String = new String(buffer, 0, car.getModel(buffer, 0, buffer.length), CarEncoder.modelCharacterEncoding)

    val tempBuffer = new UnsafeBuffer(buffer)

    val tempBufferLength = car.getActivationCode(tempBuffer, 0, tempBuffer.capacity)
    val activationCode: String = new String(buffer, 0, tempBufferLength, CarEncoder.activationCodeCharacterEncoding)

    val car2 = Car(
      serialNumber,
      modelYear,
      available,
      code,
      someNumbers,
      vehicleCode,
      extras,
      discountedModel,
      engine2,
      fuelFigures,
      performanceFigures,
      manufacturer,
      model,
      activationCode
    )

    f(car2)
  }

  private def toModel(m: io.github.aksharp.sbe.poc.Model): Option[sbe.domain.Model] = {
    m match {
      case io.github.aksharp.sbe.poc.Model.A => Option(sbe.domain.A)
      case io.github.aksharp.sbe.poc.Model.B => Option(sbe.domain.B)
      case io.github.aksharp.sbe.poc.Model.C => Option(sbe.domain.C)
      case io.github.aksharp.sbe.poc.Model.NULL_VAL => None
    }
  }

  private def toBool(b: BooleanType): Boolean = {
    b.value == 1
  }
}
