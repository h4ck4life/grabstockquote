package service

import model.StockQuote

interface StockQuoteService {

	fun getStockQuote(stockSymbol: String): StockQuote

}