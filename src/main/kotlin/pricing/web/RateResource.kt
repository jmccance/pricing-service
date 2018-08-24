package pricing.web

import mu.KLogging
import pricing.service.RateService
import java.time.Instant
import java.time.ZonedDateTime
import javax.annotation.ManagedBean
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response

@Path("rates")
@Produces("application/json", "application/xml")
@ManagedBean
class RateResource @Inject constructor(private val rateService: RateService) {
    companion object : KLogging()

    @GET
    fun getPrice(
        // TODO Could we take advantage of bean validation here?
        // Can we map these two values to a single bean and then use validation
        // to indicate that both fields are required? Then we can easily just
        // check the whole request that way.
        @QueryParam("start") start: ZonedDateTime?,
        @QueryParam("end") end: ZonedDateTime?
    ): Response =
    // TODO Handle each missing param separately for better error messaging.
        if (start != null && end != null) {
            logger.info { "getPrice" }
            Response.ok(
                RateResponse(
                    rateService.priceFor(
                        start.toLocalDateTime(),
                        end.toLocalDateTime()
                    )
                )
            ).build()
        } else {
            // TODO Clean up error handling
            // This doesn't scale to validating requests across many different
            // endpoints. Need to clean this up, probably into a single
            // exception we can throw that generates the right error message.
            //
            //     MissingRequiredParameterException(param1, param2, ...)
            //
            Response
                .status(Response.Status.BAD_REQUEST)
                .entity(
                    ErrorResponse(
                        timestamp = Instant.now(),
                        statusCode = Response.Status.BAD_REQUEST.statusCode,
                        message = "Missing required parameter [start, end]"
                    )
                )
                .build()
        }

    @GET
    @Path("boom")
    fun boom(): Response = throw RuntimeException("kah-BEWM!")
}