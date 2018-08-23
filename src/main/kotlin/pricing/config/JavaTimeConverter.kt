package pricing.config

import java.lang.reflect.Type
import java.time.Instant
import javax.ws.rs.ext.ParamConverter
import javax.ws.rs.ext.ParamConverterProvider
import javax.ws.rs.ext.Provider

@Provider
class JavaTimeConverter : ParamConverterProvider {
    // We actually _are_ checking the cast by checking that rawType is of type
    // Instant.
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> getConverter(
        rawType: Class<T>,
        genericType: Type?,
        annotations: Array<out Annotation>?
    ): ParamConverter<T>? = when {
        Instant::class.java.isAssignableFrom(rawType) ->
            instantParamConverter as ParamConverter<T>

        else -> null
    }

    private val instantParamConverter = object : ParamConverter<Instant> {
        override fun toString(value: Instant): String = value.toString()

        override fun fromString(value: String): Instant? =
            if (value.isBlank()) null
            else Instant.parse(value)
    }

    // Add additional java.time.* types as necessary.
}