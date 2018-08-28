package pricing.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider
import javax.inject.Inject
import javax.ws.rs.ext.Provider

// Initially, I tried to simply bind an ObjectMapper, register() the
// JacksonJsonProvider in the ResourceConfig, and then let HK injection
// figure stuff out for me. Unfortunately, HK doesn't seem to consider the
// injected ObjectMapper and instead just lets the JacksonJsonProvider create
// its own un-configured one. So we subtype the providers here to force them
// to use the mappers we want.

@Provider
class CustomJsonProvider @Inject() constructor(mapper: ObjectMapper)
    : JacksonJsonProvider(mapper, null)

@Provider
class CustomXMLProvider @Inject() constructor(mapper: XmlMapper)
    : JacksonXMLProvider(mapper, null)
