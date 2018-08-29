package pricing.services

import pricing.domain.Price
import pricing.domain.Rate
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

interface RateService {
    fun priceFor(start: LocalDateTime, end: LocalDateTime): Price?
}

class RateServiceImpl @Inject() constructor(
    private val rates: Set<Rate>
) : RateService {

    override fun priceFor(
        start: LocalDateTime,
        end: LocalDateTime
    ): Price? {
        validateDateRange(start, end)

        val dayOfWeek = getCommonDayOfWeek(start, end)

        return if (dayOfWeek == null) {
            null
        } else {
            val ratesForDay = getRatesForDayOfWeek(rates, dayOfWeek)
            val rate = getRateForTime(
                ratesForDay,
                start.toLocalTime(),
                end.toLocalTime()
            )

            rate?.price
        }
    }

    private fun validateDateRange(start: LocalDateTime, end: LocalDateTime) {
        if (end.isBefore(start)) {
            throw InvalidDateRangeException("'start' must precede 'end'")
        }
    }

    private fun getCommonDayOfWeek(
        start: LocalDateTime,
        end: LocalDateTime
    ): DayOfWeek? =
        if (start.dayOfWeek != end.dayOfWeek) {
            null
        } else {
            start.dayOfWeek
        }

    private fun getRatesForDayOfWeek(
        rates: Set<Rate>,
        dayOfWeek: DayOfWeek
    ): Set<Rate> = rates.filter { dayOfWeek in it.days }.toSet()

    private fun getRateForTime(
        rates: Set<Rate>,
        reqStart: LocalTime,
        reqEnd: LocalTime
    ): Rate? = rates.find { rate ->
        rate.start.isBefore(reqStart) && rate.end.isAfter(reqEnd)
    }
}

data class InvalidDateRangeException(override val message: String) : RuntimeException()