package pricing.config

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider
import org.glassfish.hk2.api.TypeLiteral
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.hk2.utilities.binding.ScopedBindingBuilder
import org.glassfish.hk2.utilities.binding.ServiceBindingBuilder
import org.glassfish.jersey.server.ResourceConfig
import pricing.domain.Rate
import pricing.service.RateService
import pricing.service.RateServiceImpl
import java.time.DayOfWeek.*
import java.time.LocalTime

class Application : ResourceConfig() {
    init {
        packages("pricing")
        register(ApplicationBinder())
        register(JacksonJsonProvider::class.java)
        register(JacksonXMLProvider::class.java)
    }
}

class ApplicationBinder : AbstractBinder() {

    // TODO Load from configuration
    private val rates = setOf(
        Rate(
            setOf(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY),
            start = LocalTime.parse("06:00"),
            end = LocalTime.parse("18:00"),
            price = 1500
        ),
        Rate(
            setOf(SATURDAY, SUNDAY),
            start = LocalTime.parse("06:00"),
            end = LocalTime.parse("20:00"),
            price = 2000
        )
    )

    override fun configure() {
        bind<RateServiceImpl>().to(RateService::class.java)
        bind(rates).to<Set<Rate>>()
    }

    private inline
    fun <reified T> AbstractBinder.bind(): ServiceBindingBuilder<T> =
        bind(T::class.java)

    private fun <T> ScopedBindingBuilder<T>.to(): ScopedBindingBuilder<T> =
        this.to(object : TypeLiteral<T>() {})

}
