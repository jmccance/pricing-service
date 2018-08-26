package pricing

import mu.KotlinLogging
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import pricing.config.Application
import java.net.URI

fun main(args: Array<String>) {
    // TODO Make port and bind host configurable

    val baseUri = URI.create("http://localhost:8080/")
    val logger = KotlinLogging.logger("main")

    val application = Application()

    val server = GrizzlyHttpServerFactory.createHttpServer(
        baseUri,
        application
    )

    // Ensure we close the resources allocated by the server on our way out.
    Runtime.getRuntime().addShutdownHook(
        Thread(Runnable {
            server.shutdownNow()
        })
    )

    logger.info { "Application listening on $baseUri" }

    Thread.currentThread().join()
}