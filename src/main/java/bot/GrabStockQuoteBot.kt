package bot

import model.StockQuote
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import service.StockQuoteService
import service.impl.StockQuoteServiceImpl
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery
import org.telegram.telegrambots.api.methods.AnswerInlineQuery
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResultArticle
import org.telegram.telegrambots.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent

class GrabStockQuoteBot : TelegramLongPollingBot() {

	fun getUpDownSymbol(price: String): String {
		if (price.indexOf("-") > -1) {
			return "↓"
		}
		if (price.indexOf("+") > -1) {
			return "↑"
		}
		if (price.equals("0.00")) {
			return ""
		}
		if (price.equals("0.0000")) {
			return ""
		}
		return "↑"
	}

	fun getStockReply(ticker: String, update: Update?): String {

		var replyMsg: String
		val notFoundMsg: String = "Please type in valid KLSE stock symbol."
		val stockQuote: StockQuote;
		val stockQuoteService: StockQuoteService = StockQuoteServiceImpl();

		stockQuote = stockQuoteService.getStockQuote(ticker);
		if (stockQuote.ticker == ""
				|| stockQuote.lastPrice == ""
				|| stockQuote.previousClosePrice == ""
				|| stockQuote.change == ""
				|| stockQuote.changePercentage == "") {
			replyMsg = notFoundMsg;

		} else {
			replyMsg = "📌 " + stockQuote.exchange.toUpperCase() + ": " + stockQuote.ticker.toUpperCase()
			replyMsg += "\n\n🔸 Last trade ➞ MYR " + stockQuote.lastPrice
			replyMsg += "\n\n🔸 Prev close ➞ MYR " + stockQuote.previousClosePrice
			replyMsg += "\n\n🔸 Change ➞ MYR " + stockQuote.change + " " + getUpDownSymbol(stockQuote.change)
			replyMsg += "\n\n🔸 Percentage ➞ " + stockQuote.changePercentage + "% " + getUpDownSymbol(stockQuote.changePercentage)
		}

		return replyMsg
	}

	override fun onUpdateReceived(update: Update?) {

		val inlineResponseMsg: InlineQuery
		val answerInlineQuery = AnswerInlineQuery()
		val stockQuote: StockQuote;
		val stockQuoteService: StockQuoteService = StockQuoteServiceImpl();
		var replyMsg: String
		val notFoundMsg: String = "Please type in valid KLSE stock symbol."

		if (update!!.hasInlineQuery()) {

			inlineResponseMsg = update.inlineQuery
			
			if (!inlineResponseMsg.query.equals("")) {

				var inlineQueryResult = InlineQueryResultArticle()
				val stockQuote = stockQuoteService.getStockQuote(inlineResponseMsg.query.toUpperCase());
				val inputMessageContent = InputTextMessageContent();

				if (stockQuote.lastPrice != "" || stockQuote.ticker != "") {
					inlineQueryResult.setTitle("KLSE: ${inlineResponseMsg.query.toUpperCase()}")
					inlineQueryResult.setDescription("Last price: MYR ${stockQuote.lastPrice} ➞ Tap here to view more.")
					inputMessageContent.setMessageText(if (inlineResponseMsg.query != "") getStockReply(inlineResponseMsg.query.toUpperCase(), update) else "")
				} else {
					inlineQueryResult.setTitle("No result")
					inlineQueryResult.setDescription("Invalid KLSE stock symbol. Please retry.")
					inputMessageContent.setMessageText("No result for ${inlineResponseMsg.query.toUpperCase()}")
				}

				
				inlineQueryResult.setThumbUrl("http://rhbtradesmart.com/uploads/home/bursa-logo.png")
				inlineQueryResult.setId(inlineResponseMsg.id)
				inlineQueryResult.setInputMessageContent(inputMessageContent)

				answerInlineQuery.setResults(inlineQueryResult)
				answerInlineQuery.setInlineQueryId(inlineResponseMsg.id)
				answerInlineQuery(answerInlineQuery)

			}
		}

		// We check if the update has a message and the message has text
		if (update!!.hasMessage() && update.getMessage().hasText()) {

			val responseMsg = update.message.getText()

			when (responseMsg) {
				"/start" -> {
					replyMsg = "Hello! Good day buddy :-)\n\nType in KLSE ticket symbol name to get the latest stock price.\n\nExample: digi"
				}
				"/top" -> {
					replyMsg = "🔵 KLSE Top Gainers\n\n"
					val stockTopGainersList: List<StockQuote> = stockQuoteService.getTopGainersList()
					for ((index, element) in stockTopGainersList.withIndex()) {
						replyMsg += "${index + 1}. ${element.ticker} ➞ MYR ${element.lastPrice} ➞ ${element.change} ${getUpDownSymbol(element.change)}\n\n"
					}
				}
				"/losers" -> {
					replyMsg = "🔴 KLSE Top Losers\n\n"
					val stockTopLosersList: List<StockQuote> = stockQuoteService.getTopLosersList()
					for ((index, element) in stockTopLosersList.withIndex()) {
						replyMsg += "${index + 1}. ${element.ticker} ➞ MYR ${element.lastPrice} ➞ ${element.change} ${getUpDownSymbol(element.change)}\n\n"
					}

				}
				else -> {
					replyMsg = getStockReply(update.getMessage().getText(), update)
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