package pricing.services

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import pricing.domain.Rate
import java.time.DayOfWeek.*
import java.time.LocalDateTime
import java.time.LocalTime

object RateServiceImplSpec : Spek({
    given("A RateServiceImpl") {
        val service = createRateService()

        on("calculating price for a time period") {
            it("returns null if the period spans more than one day") {
                val result = service.priceFor(
                    LocalDateTime.parse("2018-01-02T06:00:00"),
                    LocalDateTime.parse("2018-01-01T07:00:00")
                )

                assert(result == null) { "$result != null" }
            }

            it("returns a value for a valid period with a corresponding rate") {
                val result = service.priceFor(
                    LocalDateTime.parse("2018-01-01T07:00:00"),
                    LocalDateTime.parse("2018-01-01T08:00:00")
                )

                assert(result == 1500) { "$result != 1500" }
            }

            it("returns null for a valid period without a rate") {
                val result = service.priceFor(
                    LocalDateTime.parse("2018-01-01T00:00:00"),
                    LocalDateTime.parse("2018-01-01T01:00:00")
                )

                assert(result == null) { "$result != null" }
            }
        }

    }
})

private fun createRateService(): RateServiceImpl =
    RateServiceImpl(
        setOf(
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
    )
