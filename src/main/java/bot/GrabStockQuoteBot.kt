package bot

import model.StockQuote
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import service.StockQuoteService
import service.impl.StockQuoteServiceImpl

class GrabStockQuoteBot : TelegramLongPollingBot() {

	override fun onUpdateReceived(update: Update?) {

		val stockQuote: StockQuote;
		val stockQuoteService: StockQuoteService = StockQuoteServiceImpl();
		var replyMsg: String

		// We check if the update has a message and the message has text
		if (update!!.hasMessage() && update.getMessage().hasText()) {

			val responseMsg = update.message.getText()

			when (responseMsg) {
				"/start" -> replyMsg = "Welcome to GrabStockQuoteBot. Type in KLSE ticket symbol name to get the latest stock price"
				else -> {
					stockQuote = stockQuoteService.getStockQuote(update.getMessage().getText());
					replyMsg = (if (stockQuote.ticker != "") stockQuote.ticker + " : RM " + stockQuote.price else "No data")
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
		return "436951744:AAFfRktcl2OnCX6NvP4l3ibdEeVBt8LTaI8"
	}
}