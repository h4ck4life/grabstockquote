package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class StockUser(

		@JsonProperty("id")
		val userId: Int = 0,

		@JsonProperty("userName")
		val userName: String = ""
)
