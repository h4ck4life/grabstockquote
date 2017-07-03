package bot

import StockFeedback
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.provider
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.eq
import model.StockQuote
import model.StockUser
import org.ehcache.Cache
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.api.methods.AnswerInlineQuery
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery
import org.telegram.telegrambots.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResultArticle
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import service.StockQuoteService
import service.impl.StockQuoteServiceImpl

class GrabStockQuoteBot(val mongoDatabase: MongoDatabase, val cache: Cache<String, String>) : TelegramLongPollingBot() {

	val LOG: Logger = LoggerFactory.getLogger("com.filavents.grabstockquote")
	val mapper = jacksonObjectMapper()

	// Dependency injection
	val kodein = Kodein {
		bind<StockQuoteService>() with provider { StockQuoteServiceImpl() }
	}

	fun saveStockIntoDb(stockQuote: StockQuote) {
		try {
			mongoDatabase.getCollection("stockquote", StockQuote::class.java).insertOne(stockQuote)
		} catch (e: Exception) {
			LOG.error("Mongodb error for ${stockQuote.ticker}: " + e.message)
		}
	}

	fun validateUserAndSave(stockUser: StockUser) {
		try {
			val isUserExist = mongoDatabase.getCollection("stockuser", StockUser::class.java).find(eq("userId", stockUser.userId)).first()
			if (isUserExist == null) {
				mongoDatabase.getCollection("stockuser", StockUser::class.java).insertOne(stockUser)
				LOG.debug("Successfuly saved new user: ${stockUser.userName}")
			}
		} catch (e: Exception) {
			LOG.error("Mongodb error for ${stockUser.userName}: " + e.message)
		}
	}

	fun getAllUsers(): MutableList<StockUser> {
		var stockUserList: MutableList<StockUser> = mutableListOf()
		for (doc in mongoDatabase.getCollection("stockuser", StockUser::class.java).find()) {
			stockUserList.add(doc)
		}
		return stockUserList;
	}

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
		if (price.equals("0.000")) {
			return ""
		}
		if (price.equals("0.0000")) {
			return ""
		}
		return "↑"
	}

	fun getStockReply(ticker: String): String {
		var replyMsg: String
		val notFoundMsg: String = "'${ticker.toUpperCase()}' is invalid KLSE stock symbol."
		val stockQuote: StockQuote;
		val stockQuoteService = kodein.provider<StockQuoteService>().invoke()

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
			replyMsg += "\n\n🕘 " + stockQuote.lastTradeDateTimeLong

			saveStockIntoDb(stockQuote)
		}
		return replyMsg
	}

	override fun onUpdateReceived(update: Update?) {
		val inlineResponseMsg: InlineQuery
		val answerInlineQuery = AnswerInlineQuery()
		val stockQuoteService: StockQuoteService = kodein.provider<StockQuoteService>().invoke()
		var replyMsg: String

		if (update!!.hasInlineQuery()) {

			inlineResponseMsg = update.inlineQuery

			if (!inlineResponseMsg.query.equals("")) {

				var inlineQueryResult = InlineQueryResultArticle()
				val stockQuote = stockQuoteService.getStockQuote(inlineResponseMsg.query.toUpperCase());
				val inputMessageContent = InputTextMessageContent();

				if (stockQuote.lastPrice != "" || stockQuote.ticker != "") {

					inlineQueryResult.setTitle("KLSE: ${inlineResponseMsg.query.toUpperCase()}")
					inlineQueryResult.setDescription("Last price: MYR ${stockQuote.lastPrice} ➞ Tap here to view more.")
					inputMessageContent.setMessageText(if (inlineResponseMsg.query != "") getStockReply(inlineResponseMsg.query.toUpperCase()) else "")

					saveStockIntoDb(stockQuote)

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
		if (update.hasMessage() && update.getMessage().hasText()) {

			val responseMsg = update.message.getText()
			val queryCmd = responseMsg.trim().split(" ")

			if (queryCmd.size > 0) {
				val radioCommand = System.getenv("RADIO_MESSAGE");

				if (queryCmd[0] == radioCommand) {
					val broadcastMessage = responseMsg.trim().split("${radioCommand} ")[1]

					val stockUsersList = getAllUsers();
					if (stockUsersList.size > 0) {
						for (user in stockUsersList) {
							val message = SendMessage()
									.setChatId(user.userId.toLong())
									.setText(broadcastMessage);
							sendMessage(message)
						}
					}
				} else if (queryCmd[0] == "/feedback") {
					try {
						val feedbackMsg = responseMsg.trim().split("/feedback ")

						if (feedbackMsg[1].length > 0) {
							val stockFeedback = StockFeedback(update.message.from.id, if (update.message.from.userName == null) "" else update.message.from.userName, feedbackMsg[1])
							mongoDatabase.getCollection("stockfeedback", StockFeedback::class.java).insertOne(stockFeedback)

							val message = SendMessage()
									.setChatId(update.message.getChatId())
									.setText("Your feedback successfully submitted. Thanks for the support.");
							sendMessage(message)
						}

					} catch(e: Exception) {
						LOG.error("Error saving feedback: ${e.message}")
					}
				} else if (queryCmd[0] == "/summary") {
					val userCount = mongoDatabase.getCollection("stockuser", StockUser::class.java).count();
					val quoteCount = mongoDatabase.getCollection("stockquote", StockQuote::class.java).count();
					val feedbackCount = mongoDatabase.getCollection("stockfeedback", StockFeedback::class.java).count();

					val message = SendMessage()
							.setChatId(update.message.getChatId())
							.setText("⚡ Total\n\n📢 Users: ${userCount}\n📢 Quotes: ${quoteCount}\n📢 Feedbacks: ${feedbackCount}");
					sendMessage(message)

				} else {
					queryCmd.mapIndexed { idx, value ->
						if (idx > 2) {
							return;
						}
						val logReqObject = mapper.createObjectNode()
						logReqObject.put("chatId", update.updateId)
						logReqObject.put("inputMsg", value)
						LOG.debug(logReqObject.toString())

						when (value) {
							"/start" -> {
								val stockUser = StockUser(userId = update.message.from.id, userName = if (update.message.from.userName == null) "" else update.message.from.userName)
								validateUserAndSave(stockUser)
								replyMsg = """
					📈 Hello! Good day buddy :-)
 
👉 Type in KLSE symbol name to get the latest stock price information,
 
🔹 Example: digi
 
👉 Get multiple results (max 3),
 
🔹 Example: digi maxis astro
  
👉 Please feedback to us your experience with this bot,

🔹 /feedback type your feedback 
 					"""
							}
							"/top" -> {
								if (!cache.get("topList").isNullOrBlank()) {
									replyMsg = cache.get("topList");
									LOG.debug("Get toplist from cache")
								} else {
									replyMsg = "🔵 KLSE Top Gainers\n\n"
									val stockTopGainersList: List<StockQuote> = stockQuoteService.getTopGainersList()
									for ((index, element) in stockTopGainersList.withIndex()) {
										replyMsg += "${index + 1}. ${element.ticker} ➞ MYR ${element.lastPrice} ➞ ${element.change} ${getUpDownSymbol(element.change)}\n\n"
									}
									cache.put("topList", replyMsg)
								}
							}
							"/losers" -> {
								if (!cache.get("loserList").isNullOrBlank()) {
									replyMsg = cache.get("loserList");
									LOG.debug("Get loserlist from cache")
								} else {
									replyMsg = "🔴 KLSE Top Losers\n\n"
									val stockTopLosersList: List<StockQuote> = stockQuoteService.getTopLosersList()
									for ((index, element) in stockTopLosersList.withIndex()) {
										replyMsg += "${index + 1}. ${element.ticker} ➞ MYR ${element.lastPrice} ➞ ${element.change} ${getUpDownSymbol(element.change)}\n\n"
									}
									cache.put("loserList", replyMsg)
								}
							}
							else -> {
								replyMsg = getStockReply(value)
							}
						}

						val logRespObject = mapper.createObjectNode()
						logRespObject.put("chatId", update.updateId)
						logRespObject.put("replyMsg", replyMsg)
						LOG.debug(logRespObject.toString())

						// Create a SendMessage object with mandatory fields
						val message = SendMessage()
								.setChatId(update.message.getChatId())
								.setText(replyMsg);

						// Call method to send the message
						sendMessage(message)

						val stockUser = StockUser(userId = update.message.from.id, userName = if (update.message.from.userName == null) "" else update.message.from.userName)
						validateUserAndSave(stockUser)

					}
				}
			}
		}
	}

	override fun getBotUsername(): String? {
		return "GrabStockQuoteBot"
	}

	override fun getBotToken(): String? {
		return System.getenv("TELEGRAM_TOKEN")
	}
}