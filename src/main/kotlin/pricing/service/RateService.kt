package pricing.service

import org.jvnet.hk2.annotations.Contract
import org.jvnet.hk2.annotations.Service
import pricing.domain.Price
import pricing.domain.Rate
import java.time.DayOfWeek
import java.time.DayOfWeek.*
import java.time.LocalDateTime
import java.time.LocalTime

@Contract
interface RateService {
    fun priceFor(start: LocalDateTime, end: LocalDateTime): Price?
}

@Service
class RateServiceImpl : RateService {

    // TODO Load from configuration
    private val rates = setOf(
        Rate(
            setOf(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY),
            start = LocalTime.parse("06:00"),
            end = LocalTime.parse("18:00"),
            price = 1500
        ),
        Rate(
            setOf(SATURDAY, SUNDAY),
            start = LocalTime.parse("06:00"),
            end = LocalTime.parse("20:00"),
            price = 2000
        )
    )

    override fun priceFor(
        start: LocalDateTime,
        end: LocalDateTime
    ): Price? {
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