package pricing.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.server.ResourceConfig
import pricing.service.RateService
import pricing.service.RateServiceImpl

class Application(config: ApplicationConfig) : ResourceConfig() {
    init {
        packages("pricing")
        register(ApplicationBinder(config))
        register(JacksonJsonProvider::class.java)
        register(JacksonXMLProvider::class.java)
    }
}

class ApplicationBinder(private val config: ApplicationConfig) : AbstractBinder() {
    
    override fun configure() {
        // FIXME This is redundant to the mapper defined in ObjectMapperProvider.
        // However, it is not clear how to explain to HK2 that the value defined
        // in the provider should be injected into the RateConfigReader here.
        //
        // It seems as if Jersey is doing its own thing with respect to
        // injection, so trying to do your own, separate injection that shares
        // functionality is a PITA.
        val mapper = ObjectMapper().registerModule(KotlinModule())
        val rates = RateConfigReader(mapper).getRates(config.rateConfig)

        bind(RateServiceImpl(rates)).to(RateService::class.java)
    }

}
