import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class StockFeedback(

		@JsonProperty("id")
		val userId: Int = 0,

		@JsonProperty("userName")
		val userName: String = "",

		@JsonProperty("feedback")
		val feedback: String = ""
)