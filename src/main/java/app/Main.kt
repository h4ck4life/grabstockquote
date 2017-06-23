package app

import bot.GrabStockQuoteBot
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import org.bson.codecs.configuration.CodecRegistry
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import model.StockQuote
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.bson.codecs.pojo.PojoCodecProvider

fun main(args: Array<String>) {

	val pojoCodecRegistry = fromRegistries(
			fromProviders(PojoCodecProvider.builder().register(StockQuote::class.java).build()),
	MongoClient.getDefaultCodecRegistry());

	val connectionString = MongoClientURI("mongodb://" + System.getenv("DB_CONNECTION"));
	val mongoClient = MongoClient(connectionString);
	val database = mongoClient.getDatabase("grabstockquote").withCodecRegistry(pojoCodecRegistry);

	ApiContextInitializer.init();
	val botsApi: TelegramBotsApi = TelegramBotsApi();
	botsApi.registerBot(GrabStockQuoteBot(database));
	println("GrabStockQuoteBot now started...")
}


