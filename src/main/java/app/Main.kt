package app

import model.StockQuote
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import service.StockQuoteService
import service.impl.StockQuoteServiceImpl
import bot.GrabStockQuoteBot

fun main(args: Array<String>) {

	val stockQuote: StockQuote;
	val stockQuoteService: StockQuoteService = StockQuoteServiceImpl();

	stockQuote = stockQuoteService.getStockQuote("DIGI");

	println(if (stockQuote.ticker != "") stockQuote.ticker else "No data")

	ApiContextInitializer.init();
	val botsApi: TelegramBotsApi = TelegramBotsApi();
	botsApi.registerBot(GrabStockQuoteBot());

}


