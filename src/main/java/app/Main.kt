package app

import StockFeedback
import bot.GrabStockQuoteBot
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import model.StockQuote
import model.StockUser
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.units.EntryUnit
import org.ehcache.expiry.Duration
import org.ehcache.expiry.Expirations
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {

	// Mongodb config
	val pojoCodecRegistry = fromRegistries(
			fromProviders(PojoCodecProvider.builder().register(StockQuote::class.java, StockUser::class.java, StockFeedback::class.java).build()),
			MongoClient.getDefaultCodecRegistry());

	val connectionString = MongoClientURI("mongodb://" + System.getenv("DB_CONNECTION"))
	val mongoClient = MongoClient(connectionString);
	val database = mongoClient.getDatabase("grabstockquote").withCodecRegistry(pojoCodecRegistry);


	val cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
			.withCache("stockCache",
					CacheConfigurationBuilder.newCacheConfigurationBuilder(String::class.javaObjectType, String::class.java,
							ResourcePoolsBuilder.newResourcePoolsBuilder()
									.heap(2, EntryUnit.ENTRIES)
					)
							.withExpiry(Expirations.timeToLiveExpiration(Duration.of(1, TimeUnit.HOURS)))
			).build(true)

	val myCache = cacheManager.getCache("stockCache", String::class.java, String::class.java)

	ApiContextInitializer.init()
	val botsApi: TelegramBotsApi = TelegramBotsApi()
	botsApi.registerBot(GrabStockQuoteBot(database, myCache))
	println("GrabStockQuoteBot now started...")
}


