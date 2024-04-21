package io.endeavour.stocks.service;

import io.endeavour.stocks.StockException;
import io.endeavour.stocks.UnitTestBase;
import io.endeavour.stocks.dao.StockFundamentalsWithNamesDao;
import io.endeavour.stocks.remote.StockCalculationsClient;
import io.endeavour.stocks.remote.vo.CRWSOutputVO;
import io.endeavour.stocks.vo.StockFundamentalsWithNamesVO;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarketAnalyticsServiceTest extends UnitTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketAnalyticsServiceTest.class);
    @Autowired
    MarketAnalyticsService marketAnalyticsService;
    @MockBean
    StockFundamentalsWithNamesDao stockFundamentalsWithNamesDao;
    @MockBean
    StockCalculationsClient stockCalculationsClient;

    @Test
    public void topNPerformingStocks_HappyPath(){

        List<StockFundamentalsWithNamesVO> dummyStockOutputList = List.of(
                createStockFundamental("AAPL", new BigDecimal("1000"), new BigDecimal("2.354")),
                createStockFundamental("MSFT", new BigDecimal("1100"), new BigDecimal("1.98")),
                createStockFundamental("GOOGL", new BigDecimal("800"), new BigDecimal("1.99")),
                createStockFundamental("AMD", new BigDecimal("235"), new BigDecimal("1.11")),
                createStockFundamental("V", new BigDecimal("455"), new BigDecimal("3.33"))
        );

        //This code intercepts the method call of the Mocked Bean, and returns our dummy list as the Output List
        //The actual database call is bypassed in the business code and replaced with the Mock DAO call which returns the dummyOutputList
        Mockito.when(stockFundamentalsWithNamesDao.getAllStockFundamentalsWithNamesVO())
                .thenReturn(dummyStockOutputList);

        List<CRWSOutputVO> dummyWSOutputList = List.of(
                createWSOutput("AAPL", new BigDecimal("1.90")),
                createWSOutput("V", new BigDecimal("2.12")),
                createWSOutput("MSFT", new BigDecimal("3.15")),
                createWSOutput("GOOGL", new BigDecimal("1.65")),
                createWSOutput("AMD", new BigDecimal("1.10"))
        );

        //This code intercepts the method call of the Mocked Bean, and returns our dummy list as the Output List
        //The actual Webservice call is bypassed in the business code and replaced with the Mock WS call which returns the dummyOutputList
        Mockito.when(stockCalculationsClient.getCumulativeReturns(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(dummyWSOutputList);


        List<StockFundamentalsWithNamesVO> topNPerformingStocksList = marketAnalyticsService.getTopNPerformingStocks(3,
                LocalDate.now().minusMonths(6),
                LocalDate.now(), 0L);
        LOGGER.info("Size of the returned list from the actual Business service class is {}", topNPerformingStocksList.size());

        //Check if Top 3 stocks are returned
        assertEquals(3, topNPerformingStocksList.size());

        //Check if the stocks are sorted by CumulativeReturn Desc
        assertEquals("V", topNPerformingStocksList.get(1).getTickerSymbol());

        //Check if GOOGL or AMD are excluded or not
        StockFundamentalsWithNamesVO dummyAMDStock = new StockFundamentalsWithNamesVO();
        dummyAMDStock.setTickerSymbol("GOOGL");
        assertFalse(topNPerformingStocksList.contains(dummyAMDStock));
    }

    @Test
    public void topNPerformingStocks_UnMatchedData(){
        List<StockFundamentalsWithNamesVO> dummyStockOutputList = List.of(
                createStockFundamental("AAPL", new BigDecimal("1000"), new BigDecimal("2.354")),
                createStockFundamental("MSFT", new BigDecimal("1100"), new BigDecimal("1.98")),
                createStockFundamental("GOOGL", new BigDecimal("800"), new BigDecimal("1.99")),
                createStockFundamental("AMD", new BigDecimal("235"), new BigDecimal("1.11")),
                createStockFundamental("V", new BigDecimal("455"), new BigDecimal("3.33"))
        );

        //This code intercepts the method call of the Mocked Bean, and returns our dummy list as the Output List
        //The actual database call is bypassed in the business code and replaced with the Mock DAO call which returns the dummyOutputList
        Mockito.when(stockFundamentalsWithNamesDao.getAllStockFundamentalsWithNamesVO())
                .thenReturn(dummyStockOutputList);

        List<CRWSOutputVO> dummyWSOutputList = List.of(
                createWSOutput("AAPL", new BigDecimal("1.90")),
                createWSOutput("V", new BigDecimal("2.12")),
                createWSOutput("MSFT", new BigDecimal("3.15"))
        );

        //This code intercepts the method call of the Mocked Bean, and returns our dummy list as the Output List
        //The actual Webservice call is bypassed in the business code and replaced with the Mock WS call which returns the dummyOutputList
        Mockito.when(stockCalculationsClient.getCumulativeReturns(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(dummyWSOutputList);

        List<StockFundamentalsWithNamesVO> topNPerformingStocksList = marketAnalyticsService.getTopNPerformingStocks(4,
                LocalDate.now().minusMonths(6), LocalDate.now(), 0L);

        assertEquals(3, topNPerformingStocksList.size());
    }

    @Test
    public void topNPerformingStocks_WSDown(){
        List<StockFundamentalsWithNamesVO> dummyStockOutputList = List.of(
                createStockFundamental("AAPL", new BigDecimal("1000"), new BigDecimal("2.354")),
                createStockFundamental("MSFT", new BigDecimal("1100"), new BigDecimal("1.98")),
                createStockFundamental("GOOGL", new BigDecimal("800"), new BigDecimal("1.99")),
                createStockFundamental("AMD", new BigDecimal("235"), new BigDecimal("1.11")),
                createStockFundamental("V", new BigDecimal("455"), new BigDecimal("3.33"))
        );

        //This code intercepts the method call of the Mocked Bean, and returns our dummy list as the Output List
        //The actual database call is bypassed in the business code and replaced with the Mock DAO call which returns the dummyOutputList
        Mockito.when(stockFundamentalsWithNamesDao.getAllStockFundamentalsWithNamesVO())
                .thenReturn(dummyStockOutputList);

        //This call simulates the Webservice being down, as it returns an Empty list
        Mockito.when(stockCalculationsClient.getCumulativeReturns(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Collections.emptyList());

        assertThrows(StockException.class, () -> marketAnalyticsService.getTopNPerformingStocks(5, LocalDate.now().minusMonths(6), LocalDate.now(),0L));

        Exception exception = assertThrows(StockException.class, () -> marketAnalyticsService.getTopNPerformingStocks(5, LocalDate.now().minusMonths(1), LocalDate.now(), 0L));

        String expectedMessage = "Webservice is down";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.toUpperCase().contains(expectedMessage.toUpperCase()));
    }

    private StockFundamentalsWithNamesVO createStockFundamental(String tickerSymbol,
                                                                BigDecimal marketCap,
                                                                BigDecimal currentRatio){
        StockFundamentalsWithNamesVO stockFundamentalsWithNamesVO = new StockFundamentalsWithNamesVO();
        stockFundamentalsWithNamesVO.setTickerSymbol(tickerSymbol);
        stockFundamentalsWithNamesVO.setMarketCap(marketCap);
        stockFundamentalsWithNamesVO.setCurrentRatio(currentRatio);
        return stockFundamentalsWithNamesVO;
    }

    private CRWSOutputVO createWSOutput( String tickerSymbol, BigDecimal cumulativeReturn){
        CRWSOutputVO wsOutputVO = new CRWSOutputVO();
        wsOutputVO.setTickerSymbol(tickerSymbol);
        wsOutputVO.setCumulativeReturn(cumulativeReturn);
        return wsOutputVO;
    }


























}