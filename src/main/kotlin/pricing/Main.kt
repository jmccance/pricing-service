package pricing

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider
import mu.KotlinLogging
import org.glassfish.jersey.netty.httpserver.NettyHttpContainerProvider
import org.glassfish.jersey.server.ResourceConfig
import pricing.web.RateResource
import java.net.URI

fun main(args: Array<String>) {
    // TODO Add in the try-catch logic from the Jersey examples
    // Ensures a clean shutdown

    val baseUri = URI.create("http://localhost:8080")
    val logger = KotlinLogging.logger("Main")

    // TODO To what extent can I extract this configuration out
    // Would prefer to have these things out in a config package.
    val resourceConfig =
        ResourceConfig(RateResource::class.java)
            .packages("pricing")
            .register(JacksonJsonProvider::class.java)
            .register(JacksonXMLProvider::class.java)

    NettyHttpContainerProvider.createHttp2Server(
        URI.create("http://localhost:8080/"),
        resourceConfig,
        null
    )

    logger.info { "Application listening on $baseUri" }

    Thread.currentThread().join()
}