package pricing.config

import com.typesafe.config.Config
import java.nio.file.Path
import java.nio.file.Paths

data class ApplicationConfig(
    val host: String,
    val port: Int,
    val rateConfig: Path
) {
    companion object {
        fun from(config: Config): ApplicationConfig =
            with(config) {
                ApplicationConfig(
                    getString("service.host"),
                    getInt("service.port"),
                    Paths.get(getString("service.rate-config"))
                )
            }
    }
}