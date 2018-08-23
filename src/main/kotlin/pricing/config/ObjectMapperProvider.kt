package pricing.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import com.fasterxml.jackson.module.kotlin.KotlinModule
import javax.annotation.Priority
import javax.ws.rs.Priorities
import javax.ws.rs.ext.Provider

@Provider
@Priority(Priorities.ENTITY_CODER)
class ObjectMapperProvider : JacksonJaxbJsonProvider(createObjectMapper(), null) {
    companion object {
        private fun createObjectMapper(): ObjectMapper =
            ObjectMapper()
                .registerModules(
                    JavaTimeModule(),
                    KotlinModule()
                )
                // Use ISO dates instead of epoch timestamps
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
}