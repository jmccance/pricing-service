package pricing.web.resources

import io.mockk.every
import io.mockk.mockk
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.Assertions.assertThrows
import pricing.services.RateService
import pricing.web.MissingParameterException
import java.time.ZonedDateTime

class RateResourceSpec : Spek({
    given("A RateResource") {
        val service = mockk<RateService>()
        val resource = RateResource(service)

        on("getPrice") {
            val someStart = ZonedDateTime.parse("2018-05-06T00:01:02Z")
            val someEnd = ZonedDateTime.parse("2018-05-06T03:04:05Z")

            it("should return a RateResponse when given a valid request") {
                every { service.priceFor(any(), any()) } returns 0
                val res = resource.getPrice(someStart, someEnd)

                assert(res == RateResponse(0))
            }

            it("should throw MissingParameterException if start and/or end is missing") {
                for (start in listOf(someStart, null)) {
                    for (end in listOf(someEnd, null)) {
                        if (start == null || end == null) {
                            assertThrows(MissingParameterException::class.java) {
                                resource.getPrice(start, end)
                            }
                        }
                    }
                }
            }
        }
    }
})