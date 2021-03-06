package service

import model.StockQuote

interface StockQuoteService {

	fun getStockQuote(stockSymbol: String): StockQuote

	fun getTopGainersList(): MutableList<StockQuote>

	fun getTopLosersList(): MutableList<StockQuote>

}