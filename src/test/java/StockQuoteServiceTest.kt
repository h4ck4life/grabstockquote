import model.StockQuote
import org.junit.Assert
import org.junit.Test
import service.StockQuoteService
import service.impl.StockQuoteServiceImpl
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.provider

class StockQuoteServiceTest {
	
	// Dependency injection
	val kodein = Kodein {
		bind<StockQuoteService>() with provider { StockQuoteServiceImpl() }
	}

	val tickerExpected = "Digi"
	val stockQuoteService = kodein.provider<StockQuoteService>().invoke()
	val stockQuote = stockQuoteService.getStockQuote(tickerExpected)

	@Test
	fun getStockQuoteNotNullTest() {
		Assert.assertNotNull(stockQuote)
	}
	
	@Test
	fun getStockQuoteTickerEqualsTest() {
		Assert.assertEquals(stockQuote.ticker, "DIGI")
	}
	
	@Test
	fun getTopGainersListTest() {
		val stockTopGainersList: List<StockQuote> = stockQuoteService.getTopGainersList()
		Assert.assertNotNull(stockTopGainersList);
	}
	
	@Test
	fun getTopLosersListTest() {
		val stockTopLosersList: List<StockQuote> = stockQuoteService.getTopLosersList()
		Assert.assertNotNull(stockTopLosersList);
	}
}