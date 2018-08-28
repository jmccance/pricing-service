package pricing.web.filter

import javax.ws.rs.container.*
import javax.ws.rs.ext.Provider

@Provider
@PreMatching
class RequestIdFilter : ContainerRequestFilter, ContainerResponseFilter {
    override fun filter(requestContext: ContainerRequestContext) {
        requestContext.requestId = RequestId.randomId()
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