package bot

import model.StockQuote
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import service.StockQuoteService
import service.impl.StockQuoteServiceImpl

class GrabStockQuoteBot : TelegramLongPollingBot() {
	
	fun getUpDownSymbol(price: String) : String {
		if(price.indexOf("-") > -1) {
			return "↓"
		}
		if(price.indexOf("+") > -1) {
			return "↑"
		}
		if(price.equals("0.00")) {
			return ""
		}
		if(price.equals("0.0000")) {
			return ""
		}
		return "↑"
	}

	override fun onUpdateReceived(update: Update?) {
		val stockQuote: StockQuote;
		val stockQuoteService: StockQuoteService = StockQuoteServiceImpl();
		var replyMsg: String
		val notFoundMsg: String = "Not found. Please type in valid KLSE stock symbol."

		// We check if the update has a message and the message has text
		if (update!!.hasMessage() && update.getMessage().hasText()) {

			val responseMsg = update.message.getText()

			when (responseMsg) {
				"/start" -> replyMsg = "Hello! Good day buddy :-)\n\nType in KLSE ticket symbol name to get the latest stock price.\n\nExample: digi"
				else -> {
					stockQuote = stockQuoteService.getStockQuote(update.getMessage().getText());
					if (stockQuote.ticker == ""
							|| stockQuote.lastPrice == ""
							|| stockQuote.previousClosePrice == ""
							|| stockQuote.change == ""
							|| stockQuote.changePercentage == "") {
						replyMsg = notFoundMsg;
						
					} else {
						replyMsg = "📌 " + stockQuote.exchange.toUpperCase() + ": " + stockQuote.ticker.toUpperCase()
						replyMsg += "\n\n🔸 Last trade ➔ MYR " + stockQuote.lastPrice
						replyMsg += "\n\n🔸 Prev close ➔ MYR " + stockQuote.previousClosePrice
						replyMsg += "\n\n🔸 Change ➔ MYR " + stockQuote.change + " " + getUpDownSymbol(stockQuote.change)
						replyMsg += "\n\n🔸 Percentage ➔ " + stockQuote.changePercentage + "% " + getUpDownSymbol(stockQuote.changePercentage) 
					}
				}
			}

			// Create a SendMessage object with mandatory fields
			val message = SendMessage()
					.setChatId(update.message.getChatId())
					.setText(replyMsg);

			// Call method to send the message
			sendMessage(message);
		}
	}

	override fun getBotUsername(): String? {
		return "GrabStockQuoteBot"
	}

	override fun getBotToken(): String? {
		return System.getenv("TELEGRAM_TOKEN")
	}
}