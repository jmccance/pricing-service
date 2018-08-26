package pricing

import com.typesafe.config.ConfigFactory
import mu.KotlinLogging
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import pricing.config.Application
import pricing.config.ApplicationConfig
import java.net.URI

fun main(args: Array<String>) {
    // TODO Make port and bind host configurable

    val logger = KotlinLogging.logger("main")

    val config = ApplicationConfig.from(ConfigFactory.load())

    val baseUri = URI("http", null, config.host, config.port, "/", null, null)

    val application = Application(config)

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