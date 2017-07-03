package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Calendar
import java.util.TimeZone

@JsonIgnoreProperties(ignoreUnknown = true)
data class StockUser(

		@JsonProperty("id")
		val userId: Int = 0,

		@JsonProperty("userName")
		val userName: String = "",
		
		@JsonProperty("registeredDate")
		val registeredDate: String = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur")).getTime().toString()
)
