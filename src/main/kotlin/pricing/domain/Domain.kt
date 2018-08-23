package pricing.domain

import java.time.DayOfWeek
import java.time.LocalTime

typealias Price = Int

data class Rate(
    val days: Set<DayOfWeek>,
    val start: LocalTime,
    val end: LocalTime,
    val price: Int
)