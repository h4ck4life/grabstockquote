package app

import model.StockQuote
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import service.StockQuoteService
import service.impl.StockQuoteServiceImpl
import bot.GrabStockQuoteBot

fun main(args: Array<String>) {
	ApiContextInitializer.init();
	val botsApi: TelegramBotsApi = TelegramBotsApi();
	botsApi.registerBot(GrabStockQuoteBot());
	println("GrabStockQuoteBot now started...")
}


