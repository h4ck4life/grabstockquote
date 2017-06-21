package service.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import model.StockQuote
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import service.StockQuoteService
import java.util.ArrayList
import org.jsoup.select.Elements

class StockQuoteServiceImpl : StockQuoteService {

	override fun getTopLosersList(): MutableList<StockQuote> {
		var stockQuoteList: MutableList<StockQuote> = mutableListOf()

		val doc: Document = Jsoup.connect("http://www.malaysiastock.biz/Market-Watch.aspx")
				.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.86 Safari/537.36")
				.get()
		val el: Elements = doc.select("#MainContent_tbTopLosers > tbody > tr:nth-child(n+2)")

		// Get ticket
		el.map {
			var stockQuote: StockQuote = StockQuote(
					ticker = it.select("td:nth-child(1)").text(),
					lastPrice = it.select("td:nth-child(2)").text(),
					change = it.select("td:nth-child(3)").text()
			)
			stockQuoteList.add(stockQuote)
		}
		return stockQuoteList;
	}

	override fun getTopGainersList(): MutableList<StockQuote> {
		var stockQuoteList: MutableList<StockQuote> = mutableListOf()

		val doc: Document = Jsoup.connect("http://www.malaysiastock.biz/Market-Watch.aspx")
				.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.86 Safari/537.36")
				.get()
		val el: Elements = doc.select("#MainContent_tbTopGainers > tbody > tr:nth-child(n+2)")

		// Get ticket
		el.map {
			var stockQuote: StockQuote = StockQuote(
					ticker = it.select("td:nth-child(1)").text(),
					lastPrice = it.select("td:nth-child(2)").text(),
					change = it.select("td:nth-child(3)").text()
			)

			stockQuoteList.add(stockQuote)
		}

		return stockQuoteList;
	}

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