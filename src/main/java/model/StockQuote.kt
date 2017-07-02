package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**

Valid URL source = https://www.google.com/finance/info?q=KLSE:DIGI

JSON Object Definition Ref:

t		Ticker
e		Exchange
l		Last Price
ltt		Last Trade Time
l		Price
lt		Last Trade Time Formatted
lt_dts	Last Trade Date/Time
c		Change
cp		Change Percentage
el		After Hours Last Price
elt		After Hours Last Trade Time Formatted
div		Dividend
yld		Dividend Yield

 */

@JsonIgnoreProperties(ignoreUnknown = true)
data class StockQuote(
		
		/*@JsonProperty("id")
		val id: String = "",*/

		@JsonProperty("t")
		val ticker: String = "",

		@JsonProperty("e")
		val exchange: String = "",

		@JsonProperty("l")
		val lastPrice: String = "",

		@JsonProperty("l_fix")
		val lastFixPrice: String = "",

		@JsonProperty("l_cur")
		val lastTradeWithCurrency: String = "",

		@JsonProperty("s")
		val lastTradeSize: String = "",

		@JsonProperty("ltt")
		val lastTradeTime: String = "",

		@JsonProperty("lt")
		val lastTradeDateTimeLong: String = "",

		@JsonProperty("lt_dts")
		val lastTradeDateTime: String = "",

		@JsonProperty("c")
		val change: String = "",

		@JsonProperty("c_fix")
		val c_fix: String = "",

		@JsonProperty("cp")
		val changePercentage: String = "",

		@JsonProperty("cp_fix")
		val changePercentageFix: String = "",

		@JsonProperty("ccol")
		val ccol: String = "",

		@JsonProperty("pcls_fix")
		val previousClosePrice: String = "",

		val requestedDate: String =  Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur")).getTime().toString()
);
