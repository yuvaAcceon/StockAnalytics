package io.endeavour.stocks.controller;

import io.endeavour.stocks.entity.stocks.*;
import io.endeavour.stocks.service.MarketAnalyticsService;
import io.endeavour.stocks.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "Stocks API", description = "This API generates cool trends related to the US Stock Market")
@RequestMapping(value = "/stocks")
public class StocksController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StocksController.class);

    @Autowired
    MarketAnalyticsService marketAnalyticsService;
    @GetMapping("/getSamplePriceHistory")
    public StocksPriceHistoryVO getSamplePriceHistory(){

        StocksPriceHistoryVO stocksPriceHistoryVO = new StocksPriceHistoryVO();

        stocksPriceHistoryVO.setTickerSymbol("V");
        stocksPriceHistoryVO.setTradingDate(LocalDate.now());
        stocksPriceHistoryVO.setOpenPrice(new BigDecimal("154.34"));
        stocksPriceHistoryVO.setClosePrice(new BigDecimal("155.93"));
        stocksPriceHistoryVO.setVolume(890634L);

        return stocksPriceHistoryVO;
    }

    @GetMapping(value = "/getSingleStockPriceHistory/{tickerSymbol}")
    public List<StocksPriceHistoryVO> getSingleStockPriceHistory(@PathVariable(name = "tickerSymbol") String tickerSymbol,
                                                                 @RequestParam(name = "fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
                                                                 @RequestParam(name = "toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
                                                                 @RequestParam(name = "sortField") Optional<String> sortFieldOptional,
                                                                 @RequestParam(name = "sortDirection") Optional<String> sortDirectionOptional){
        if(fromDate.isAfter(toDate)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fromDate cannot be greater than toDate");
        }
        System.out.println("From the request, the parameter TickerSymbol :"+tickerSymbol+", fromDate : "+fromDate+", toDate : "+toDate);
        LOGGER.info("From the request, the parameter TickerSymbol : {}, fromDate : {}, toDate : {}", tickerSymbol, fromDate, toDate);
        return marketAnalyticsService.getSingleStockPriceHistory(tickerSymbol, fromDate, toDate, sortFieldOptional, sortDirectionOptional);
    }

    @PostMapping(value = "/getMultipleStockPriceHistory")
    @Operation(method = "GetMultipleStockPriceHistory", description = "This API will return Stock Price History for the given list of stocks for the given timeframe")
    public List<StocksPriceHistoryVO> getMultipleStockPriceHistory(@RequestBody StockPriceHistoryRequestVO stockPriceHistoryRequestVO){
        LOGGER.info("Values received from the Http Request are : tickersList : {}, fromDate : {}, toDate : {}",
                stockPriceHistoryRequestVO.getTickersList(), stockPriceHistoryRequestVO.getFromDate(),
                stockPriceHistoryRequestVO.getToDate());

        return marketAnalyticsService.getMultipleStockPriceHistory(stockPriceHistoryRequestVO.getTickersList(),
                stockPriceHistoryRequestVO.getFromDate(), stockPriceHistoryRequestVO.getToDate());
    }

    @GetMapping(value = "/getAllStockFundamentalsJDBC")
    public List<StockFundamentalsWithNamesVO> getAllStockFundamentalsJDBC(){
        LOGGER.debug("In the getAllStockFundamentalsJDBC() method of the class {} ", getClass());
        return marketAnalyticsService.getAllStockFundamentals();
    }

    @PostMapping(value = "/getSomeStockFundamentals")
    @Operation(method = "GetSomeStockFundamentals", description = "Gets the Stock Information for the given list of TickerSymbols")
    @ApiResponse(responseCode = "200", description = "Returns List of Stock Fundamental information for the given ticker Symbols")
    @ApiResponse(responseCode = "400", description = "Returns a Http 400 Bad Request if the input Ticker Symbols list is not sent or is empty")
    public List<StockFundamentalsWithNamesVO> getSomeStockFundamentals(
            @RequestBody
            @Schema(name = "tickersList", description = "List of ticker Symbols for which the Stock Information needs to be retrieved", example = "[\"AAPL\", \"MSFT\", \"NVDA\"]") // \ is the escape character
            Optional<List<String>> tickersListOptional){
        if(tickersListOptional.isPresent()){ //Input Validation
            List<String> tickersList = tickersListOptional.get();
            if(tickersList.isEmpty()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "TickersList is empty or not sent");
            }else{
                return marketAnalyticsService.getSomeStockFundamentals(tickersList);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No TickersList was sent");
        }

    }

    @GetMapping(value = "/getAllStockFundamentalsJPA")
    public List<StockFundamentals> getAllStockFundamentalsJPA(){
        return marketAnalyticsService.getAllStockFundamentalsJPA();
    }

    @GetMapping(value = "/getAllSectorsJPA")
    public List<Sector> getAllSectors(){
        return marketAnalyticsService.getAllSectors();
    }

    @GetMapping(value = "/getAllSubSectorsJPA")
    public List<SubSector> getAllSubSectors(){
        return marketAnalyticsService.getAllSubSectors();
    }

    @GetMapping(value = "/getStockPriceHistory/{tickerSymbol}")
    public ResponseEntity<StockPriceHistory> getStockPriceHistory(@PathVariable(value = "tickerSymbol") String tickerSymbol,
                                                                  @RequestParam(value = "tradingDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tradingDate){
       return ResponseEntity.of(marketAnalyticsService.getStockPriceHistory(tickerSymbol, tradingDate));
    }

    @GetMapping(value = "/getTopStockBySector")
    public List<TopStockBySectorVO> getTopStockBySector(){
        return marketAnalyticsService.getTopStockBySector();
    }

    @GetMapping(value = "/getTopNStocksNativeSQL")
    public List<StockFundamentals> getTopNStocksNativeSQL(@RequestParam(value = "num") Integer num){
        return marketAnalyticsService.getTopNStocksNativeSQL(num);
    }

    @GetMapping(value = "/getTopNStocksJPQL")
    public List<StockFundamentals> getTopNStocksJPQL(@RequestParam(value = "num") Integer num){
        return marketAnalyticsService.getTopNStocksJPQL(num);
    }

    @GetMapping(value = "/getTopNStocksCriteria")
    public List<StockFundamentals> getTopNStocksCriteria(@RequestParam(value = "num") Integer num){
        return marketAnalyticsService.getTopNStocksCriteria(num);
    }

    @GetMapping(value = "/getNotNullCurrentRatioStocks")
    public List<StockFundamentals> getNotNullCurrentRatioStocks(){
        return marketAnalyticsService.getNotNullCurrentRatioStocks();
    }

    @GetMapping(value = "/getTop3StocksBySector")
    public List<SectorVO> getTop3StocksBySector(){
        return marketAnalyticsService.getTop3StocksBySector();
    }

    @GetMapping(value = "/getTopNPerformingStocks/{num}")
    public List<StockFundamentalsWithNamesVO> getTopNPerformingStocks(@PathVariable(value = "num") Integer num,
                                                           @RequestParam(name = "fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
                                                           @RequestParam(name = "toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
                                                           @RequestParam(value = "marketCapLimit") Long marketCapLimit){
        return marketAnalyticsService.getTopNPerformingStocks(num, fromDate, toDate, marketCapLimit);
    }

    @GetMapping(value = "/getTopPerformingStocksBySubSector/{num}")
    public List<SubSectorVO> getTopPerformingStocksBySubSector(@PathVariable Integer num,
                                                               @RequestParam(value = "fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
                                                               @RequestParam(value = "toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate){
        return marketAnalyticsService.getTopPerformingStocksBySubSector(num, fromDate, toDate);
    }

    @ExceptionHandler({IllegalArgumentException.class, SQLException.class, NullPointerException.class})
    public ResponseEntity generateExceptionResponse(Exception e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
