package pricing.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.glassfish.hk2.api.TypeLiteral
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.server.ResourceConfig
import pricing.domain.Rate
import pricing.service.RateService
import pricing.service.RateServiceImpl

class Application(config: ApplicationConfig) : ResourceConfig() {
    init {
        packages("pricing.config", "pricing.web")

        register(object : AbstractBinder() {
            override fun configure() {
                val jsonMapper = configure(ObjectMapper())
                val xmlMapper = configure(XmlMapper())

                bind(jsonMapper).to(ObjectMapper::class.java)
                bind(xmlMapper).to(XmlMapper::class.java)

                // For reasons that elude me, this binding will fail to resolve
                // if we just bind to Set<Rate>, or if we bind explicitly to
                // kotlin.collections.Set<Rate>. But binding to java.util.Set
                // works fine.
                bind(RateConfigReader(jsonMapper).getRates(config.rateConfig))
                    .to(object : TypeLiteral<java.util.Set<Rate>>() {})

                bind(RateServiceImpl::class.java).to(RateService::class.java)
            }
        })
    }

    private inline fun <reified M : ObjectMapper> configure(mapper: M): M =
        mapper
            .registerModules(
                JavaTimeModule(),
                KotlinModule()
            )
            // Use ISO dates instead of epoch timestamps
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                as M
}

