import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val host: String,
    val port: Int,
    val baseUrl: String,
    val contentDir: String,
    val isDebug: Boolean = false
)