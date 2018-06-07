package sbe.codec

import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.{CREATE, READ, WRITE}
import io.github.aksharp.sbe.poc._
import org.agrona.concurrent.UnsafeBuffer
import sbe.util.AutoCloseable._

object PlainCarEncoder {

  private val VEHICLE_CODE = "abcdef".getBytes(CarEncoder.vehicleCodeCharacterEncoding)
  private val MANUFACTURER_CODE = "123".getBytes(EngineEncoder.manufacturerCodeCharacterEncoding)
  private val MANUFACTURER = "Honda".getBytes(CarEncoder.manufacturerCharacterEncoding)
  private val MODEL = "Civic VTi".getBytes(CarEncoder.modelCharacterEncoding)
  private val ACTIVATION_CODE = new UnsafeBuffer("abcdef".getBytes(CarEncoder.activationCodeCharacterEncoding))
  private val MESSAGE_HEADER_ENCODER = new MessageHeaderEncoder
  private val CAR_ENCODER = new CarEncoder


  def encodeToFile(path: String) = {
    val byteBuffer = ByteBuffer.allocateDirect(4096)
    val directBuffer = new UnsafeBuffer(byteBuffer)
    val encodingLengthPlusHeader = encode(CAR_ENCODER, directBuffer, MESSAGE_HEADER_ENCODER)

    // Optionally write the encoded buffer to a file for decoding by the On-The-Fly decoder

    withResources(FileChannel.open(Paths.get(path), READ, WRITE, CREATE)) { channel =>
      byteBuffer.limit(encodingLengthPlusHeader)
      channel.write(byteBuffer)
    }
  }

  @throws[Exception]
  private def encode(
              car: CarEncoder,
              directBuffer: UnsafeBuffer,
              messageHeaderEncoder: MessageHeaderEncoder): Int = {

    car.wrapAndApplyHeader(directBuffer, 0, messageHeaderEncoder)
      .serialNumber(1234)
      .modelYear(2013)
      .available(BooleanType.T)
      .code(Model.A)
      .putVehicleCode(VEHICLE_CODE, 0)

    for (i <- 0 until CarEncoder.someNumbersLength) {
      car.someNumbers(i, i)
    }

    car.extras
      .clear
      .cruiseControl(true)
      .sportsPack(true)
      .sunRoof(false)

    car.engine
      .capacity(2000)
      .numCylinders(4.toShort)
      .putManufacturerCode(MANUFACTURER_CODE, 0)
      .efficiency(35.toByte)
      .boosterEnabled(BooleanType.T)
      .booster.boostType(BoostType.NITROUS).horsePower(200.toShort)

    car.fuelFiguresCount(3)
      .next.speed(30).mpg(35.9f).usageDescription("Urban Cycle")
      .next.speed(55).mpg(49.0f).usageDescription("Combined Cycle")
      .next.speed(75).mpg(40.0f).usageDescription("Highway Cycle")

    val figures = car.performanceFiguresCount(2)
    figures.next
      .octaneRating(95.toShort)
      .accelerationCount(3)
      .next.mph(30).seconds(4.0f)
      .next.mph(60).seconds(7.5f)
      .next.mph(100).seconds(12.2f)

    figures.next
      .octaneRating(99.toShort)
      .accelerationCount(3)
      .next.mph(30).seconds(3.8f)
      .next.mph(60).seconds(7.1f)
      .next.mph(100).seconds(11.8f)

    // An exception will be raised if the string length is larger than can be encoded in the varDataEncoding field
    // Please use a suitable schema type for varDataEncoding.length: uint8 <= 254, uint16 <= 65534
    car.manufacturer(new String(MANUFACTURER, StandardCharsets.UTF_8))
      .putModel(MODEL, 0, MODEL.length)
      .putActivationCode(ACTIVATION_CODE, 0, ACTIVATION_CODE.capacity)

    MessageHeaderEncoder.ENCODED_LENGTH + car.encodedLength
  }

}
