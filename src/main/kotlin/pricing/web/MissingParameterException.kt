package pricing.web

data class MissingParameterException(
    val parameters: Set<String>
) : RuntimeException() {
    override val message: String? = "Missing required parameter(s): $parameters"
}