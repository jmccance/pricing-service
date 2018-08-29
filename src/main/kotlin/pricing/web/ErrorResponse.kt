package pricing.web

import java.time.Instant

data class ErrorResponse(
    val requestId: String?,
    val timestamp: Instant,
    val endpoint: String,
    val statusCode: Int,
    val message: String?
)