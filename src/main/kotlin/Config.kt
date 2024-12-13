import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val host: String,
    val port: Int,
    val baseUrl: String,
    val contentDir: String,
    val game: String,
    val imageDir: String,
    val version: String,
    val isDebug: Boolean = false,
    val theme: String
)