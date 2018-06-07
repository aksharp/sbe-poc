package sbe.domain

sealed trait Booster {
  val horsepower: Int
}
case class Turbo(horsepower: Int) extends Booster
case class Supercharger(horsepower: Int) extends Booster
case class Nitros(horsepower: Int) extends Booster
case class Kers(horsepower: Int) extends Booster