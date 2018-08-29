package pricing.web.filters

import mu.KLogging
import mu.withLoggingContext
import org.glassfish.jersey.server.ExtendedUriInfo
import java.time.Duration
import java.time.temporal.ChronoUnit
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.core.Context
import javax.ws.rs.ext.Provider

@Provider
class RequestHandledLoggingFilter(
    @Context private val uriInfo: ExtendedUriInfo
) : ContainerRequestFilter, ContainerResponseFilter {
    companion object : KLogging()

    private var ContainerRequestContext.startTimeNs: Long?
        get() = this.getProperty("responseLoggingFilter.startTimeNs") as Long?
        set(value) = this.setProperty("responseLoggingFilter.startTimeNs", value)

    override fun filter(requestContext: ContainerRequestContext) {
        requestContext.startTimeNs = System.nanoTime()
    }

    override fun filter(
        requestContext: ContainerRequestContext,
        responseContext: ContainerResponseContext
    ) {
        val elapsedTime = requestContext.startTimeNs?.let {
            Duration.of(
                System.nanoTime() - requestContext.startTimeNs!!,
                ChronoUnit.NANOS
            )
        }

        // We know this cast is safe because we're declaring the map to be from
        // String to String? and then we're filtering out all the nulls.
        //
        // Also suppressing the explicit-type-argument warning, since we
        // actually need that there to help guarantee that the result is
        // Map<String, String> and not Map<String, Any>.
        @Suppress("UNCHECKED_CAST", "RemoveExplicitTypeArguments")
        val extraContext = mapOf<String, String?>(
            "exception" to getQualifiedName(uriInfo.mappedThrowable),
            "exception.message" to uriInfo.mappedThrowable?.message,
            "durationMs" to elapsedTime?.toMillis()?.toString()
        ).filterValues { it != null } as Map<String, String>

        withLoggingContext(
            mapOf(
                "endpoint" to "${requestContext.method} ${getPath(uriInfo)}",
                "statusCode" to responseContext.status.toString()
            ) + extraContext
        ) {
            if (uriInfo.mappedThrowable != null) {
                logger
            }
            logger.info { "RequestHandled" }
        }
    }

    /**
     * Return the qualified name of the class of the Throwable, or null if the
     * Throwable is null.
     *
     * A workaround since `t?::class` is reserved syntax.
     */
    private fun getQualifiedName(t: Throwable?): String? =
        if (t == null) null else t::class.qualifiedName

    private fun getPath(uriInfo: ExtendedUriInfo): String =
            uriInfo.matchedTemplates.firstOrNull()?.toString() ?: "/${uriInfo.path}"

}