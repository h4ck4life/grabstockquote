import model.StockQuote
import org.junit.Assert
import org.junit.Test
import service.StockQuoteService
import service.impl.StockQuoteServiceImpl

class StockQuoteServiceTest {

	val tickerExpected = "Digi"
	val stockQuoteService: StockQuoteService = StockQuoteServiceImpl()
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