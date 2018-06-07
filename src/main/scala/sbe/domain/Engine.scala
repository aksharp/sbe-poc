package sbe.domain

case class Engine(
                   capacity: Int,
                   cylinders: Int,
                   maxRpm: Int,
                   manufacturerCode: String,
                   fuel: String,
                   efficiency: Int,
                   boosterEnabled: Boolean,
                   booster: Option[Booster]
                 )