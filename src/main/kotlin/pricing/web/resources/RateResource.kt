package pricing.web.resources

import mu.KLogging
import pricing.services.RateService
import pricing.web.MissingParameterException
import java.time.ZonedDateTime
import javax.annotation.ManagedBean
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam

@Path("rates")
@Produces("application/json", "application/xml")
@ManagedBean
class RateResource @Inject constructor(private val rateService: RateService) {
    companion object : KLogging()

    @GET
    fun getPrice(
        @QueryParam("start") start: ZonedDateTime?,
        @QueryParam("end") end: ZonedDateTime?
    ): RateResponse {
        val missingParams =
            mapOf("start" to start, "end" to end)
                .filterValues { it == null }
                .keys

        return if (missingParams.isEmpty()) {
            RateResponse(
                rateService.priceFor(
                    start!!.toLocalDateTime(),
                    end!!.toLocalDateTime()
                )
            )
        } else {
            throw MissingParameterException(missingParams)
        }
    }
}

