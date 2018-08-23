package pricing.web

import java.time.Instant

// TODO Add request id to response
data class ErrorResponse(
    val timestamp: Instant,
    val statusCode: Int,
    val message: String?
)