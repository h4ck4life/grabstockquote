package service.impl

import service.StockQuoteService
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import model.StockQuote
import com.fasterxml.jackson.module.kotlin.*

class StockQuoteServiceImpl : StockQuoteService {

	override fun getStockQuote(stockSymbol: String): StockQuote {

		val jsonResponse: HttpResponse<String> = Unirest.get("https://www.google.com/finance/info")
				.header("accept", "application/json")
				.queryString("q", "KLSE:${stockSymbol.toUpperCase()}")
				.asString();

		if (jsonResponse.status == 400) {
			return StockQuote();
		}

		val mapper = jacksonObjectMapper()
		val state: List<StockQuote> = mapper.readValue(jsonResponse.body.substring(4))

		return state.get(0)
	}
}