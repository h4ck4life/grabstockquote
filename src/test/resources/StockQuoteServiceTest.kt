import model.StockQuote
import org.junit.Assert
import org.junit.Test
import service.StockQuoteService
import service.impl.StockQuoteServiceImpl

class StockQuoteServiceTest {

	val ticker = "Digi"
	val stockQuoteService: StockQuoteService = StockQuoteServiceImpl()
	val stockQuote = stockQuoteService.getStockQuote(ticker)

	@Test
	fun getStockQuoteNotNull() {
		Assert.assertNotNull(stockQuote)
	}
	
	@Test
	fun getStockQuoteTickerEqualsTo() {
		Assert.assertEquals(stockQuote.ticker, ticker.toUpperCase())
	}
	
	@Test
	fun getTopGainersList() {
		val stockTopGainersList: List<StockQuote> = stockQuoteService.getTopGainersList()
		Assert.assertNotNull(stockTopGainersList);
	}
	
	@Test
	fun getTopLosersList() {
		val stockTopLosersList: List<StockQuote> = stockQuoteService.getTopLosersList()
		Assert.assertNotNull(stockTopLosersList);
	}
}