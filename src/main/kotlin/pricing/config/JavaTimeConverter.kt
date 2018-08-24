package pricing.config

import java.lang.reflect.Type
import java.time.ZonedDateTime
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
        ZonedDateTime::class.java.isAssignableFrom(rawType) ->
            zonedDateTimeConverter as ParamConverter<T>

        else -> null
    }

    private val zonedDateTimeConverter = object : ParamConverter<ZonedDateTime> {
        override fun toString(value: ZonedDateTime): String = value.toString()

        override fun fromString(value: String): ZonedDateTime? =
            if (value.isBlank()) null
            else ZonedDateTime.parse(value)
    }

    // Add additional java.time.* types as necessary.
}