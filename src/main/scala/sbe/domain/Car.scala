package sbe.domain

case class Car(
                serialNumber: Int,
                modelYear: Int,
                available: Boolean,
                code: Option[Model],
                someNumbers: Seq[Long],
                vehicleCode: String,
                extras: Seq[OptionalExtras],
                discountedModel: Option[Model],
                engine: Engine,
                fuelFigures: Seq[FuelFigures],
                performanceFigures: Seq[PerformanceFigures],
                manufacturer: String,
                model: String,
                activationCode: String
              )