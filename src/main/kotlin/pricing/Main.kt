package pricing

import mu.KotlinLogging
import org.glassfish.jersey.netty.httpserver.NettyHttpContainerProvider
import pricing.config.Application
import java.net.URI

fun main(args: Array<String>) {
    // TODO Make port and bind host configurable

    val baseUri = URI.create("http://localhost:8080/")
    val logger = KotlinLogging.logger("main")

    val application = Application()

    val server = NettyHttpContainerProvider.createHttp2Server(
        baseUri,
        application,
        null
    )

    // Ensure we close the resources allocated by Netty on our way out.
    Runtime.getRuntime().addShutdownHook(Thread(Runnable { server.close() }))

    logger.info { "Application listening on $baseUri" }

    Thread.currentThread().join()
}