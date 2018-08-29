package pricing.web.filters

import mu.KLogging
import org.slf4j.MDC
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.container.PreMatching
import javax.ws.rs.ext.Provider

@Provider
@PreMatching
class RequestIdFilter : ContainerRequestFilter, ContainerResponseFilter {
    companion object : KLogging()

    override fun filter(requestContext: ContainerRequestContext) {
        requestContext.requestId = RequestId.randomId()

        // Note that this only works if we're using a thread-per-request model.
        MDC.put("requestId", requestContext.requestId?.value)
    }

    override fun filter(
        requestContext: ContainerRequestContext,
        responseContext: ContainerResponseContext
    ) {
        val requestId = requestContext.requestId
        if (requestId != null) {
            responseContext.headers.putSingle("Request-Id", requestId.value)
        }
    }
}

data class RequestId(val value: String) {
    companion object {
        fun randomId(): RequestId =
            RequestId(java.util.UUID.randomUUID().toString().replace("-", ""))
    }
}

private const val REQUEST_ID_PROPERTY = "Request-Id"

var ContainerRequestContext.requestId: RequestId?
    get(): RequestId? = this.getProperty(REQUEST_ID_PROPERTY) as RequestId
    set(value) {
        this.setProperty(REQUEST_ID_PROPERTY, value)
    }