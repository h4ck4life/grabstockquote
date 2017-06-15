package app

import model.StockQuote
import service.StockQuoteService
import service.impl.StockQuoteServiceImpl

fun main(args: Array<String>) {

	val stockQuote: StockQuote;
	val stockQuoteService: StockQuoteService = StockQuoteServiceImpl();

	stockQuote = stockQuoteService.getStockQuote("DIGI");

	println(if (stockQuote.ticker != "") stockQuote.ticker else "No data")

}


