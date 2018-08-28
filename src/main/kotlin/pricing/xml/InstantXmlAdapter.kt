package pricing.xml

import java.time.Instant
import javax.xml.bind.annotation.adapters.XmlAdapter

class InstantXmlAdapter : XmlAdapter<String, Instant>() {
    override fun marshal(v: Instant?): String = v.toString()

    override fun unmarshal(v: String?): Instant = Instant.parse(v)
}