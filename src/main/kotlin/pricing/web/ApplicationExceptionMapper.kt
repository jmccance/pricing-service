package pricing.web

import mu.KLogging
import org.glassfish.jersey.server.ParamException
import pricing.web.filter.RequestId
import java.time.Instant
import java.time.format.DateTimeParseException
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class ApplicationExceptionMapper(
    @Context private val requestId: RequestId
): ExceptionMapper<Throwable> {
    companion object : KLogging()

    override fun toResponse(exception: Throwable?): Response = when (exception) {
        is ParamException.QueryParamException ->
            if (exception.cause is DateTimeParseException) {
                errorResponse(Status.BAD_REQUEST, exception.cause?.message)
            } else {
                unmappedExceptionResponse(exception)
            }

        is WebApplicationException -> {
            exception.response
        }

        else -> unmappedExceptionResponse(exception)
    }

    private fun unmappedExceptionResponse(e: Throwable?): Response {
        logger.error(e) { "Unmappable exception" }

        return errorResponse(
            status = Status.INTERNAL_SERVER_ERROR,
            message = e?.message
        )
    }

    private fun errorResponse(status: Status, message: String?): Response =
        Response
            .status(status)
            .entity(
                ErrorResponse(
                    requestId = requestId.value,
                    timestamp = Instant.now(),
                    statusCode = status.statusCode,
                    message = message
                )
            )
            .build()
}

