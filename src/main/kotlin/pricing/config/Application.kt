package pricing.config

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.server.ResourceConfig
import pricing.service.RateService
import pricing.service.RateServiceImpl

class Application : ResourceConfig() {
    init {
        packages("pricing")
        register(ApplicationBinder())
        register(JacksonJsonProvider::class.java)
        register(JacksonXMLProvider::class.java)
    }
}

class ApplicationBinder : AbstractBinder() {

    override fun configure() {
        bind(RateServiceImpl::class.java).to(RateService::class.java)
    }

}

