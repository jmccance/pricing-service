package pricing.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.jvnet.hk2.annotations.Service
import pricing.domain.Rate
import java.nio.file.Files
import java.nio.file.Path
import java.time.DayOfWeek
import java.time.DayOfWeek.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@Service
class RateConfigReader @Inject constructor(private val mapper: ObjectMapper) {

    fun getRates(path: Path): Set<Rate> {
        val bytes = Files.readAllBytes(path)
        val rateConfig = mapper.readValue(bytes, RateConfig::class.java)

        return rateConfig.rates.map(::toRate).toSet()
    }

    private data class RateConfig(val rates: List<RateNode>)
    private data class RateNode(
        val days: String,
        val times: String,
        val price: Int
    )

    private fun toRate(node: RateNode): Rate {
        val daysOfWeek = parseDays(node.days)
        val (start, end) = parseTimes(node.times)

        return Rate(daysOfWeek, start, end, node.price)
    }

    private fun parseDays(dayString: String): Set<DayOfWeek> =
        dayString.split(",").map {
            when (it) {
                "mon" -> MONDAY
                "tues" -> TUESDAY
                "wed" -> WEDNESDAY
                "thurs" -> THURSDAY
                "fri" -> FRIDAY
                "sat" -> SATURDAY
                "sun" -> SUNDAY
                else -> throw RuntimeException("") // FIXME
            }
        }.toSet()

    private fun parseTimes(timeString: String): Pair<LocalTime, LocalTime> {
        val timeFormat = DateTimeFormatter.ofPattern("HHMM")

        val times = timeString.split("-").map {
            LocalTime.parse(it, timeFormat)
        }

        return Pair(times[0], times[1])
    }

}