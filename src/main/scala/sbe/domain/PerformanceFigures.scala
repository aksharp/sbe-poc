package sbe.domain

case class PerformanceFigures(
                               octaneRating: Int, // 90 - 110
                               accelerations: Seq[Acceleration]
                             )
