package io.endeavour.stocks.service;

import io.endeavour.stocks.StockException;
import io.endeavour.stocks.dao.StockFundamentalsWithNamesDao;
import io.endeavour.stocks.dao.StockPriceHistoryDao;
import io.endeavour.stocks.entity.stocks.*;
import io.endeavour.stocks.remote.StockCalculationsClient;
import io.endeavour.stocks.remote.vo.CRWSInputVO;
import io.endeavour.stocks.remote.vo.CRWSOutputVO;
import io.endeavour.stocks.repository.stocks.SectorRepository;
import io.endeavour.stocks.repository.stocks.StockFundamentalsRepository;
import io.endeavour.stocks.repository.stocks.StockPriceHistoryRepository;
import io.endeavour.stocks.repository.stocks.SubSectorRepository;
import io.endeavour.stocks.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MarketAnalyticsService {

    private final static Logger LOGGER = LoggerFactory.getLogger(MarketAnalyticsService.class);

    StockPriceHistoryDao stockPriceHistoryDao;
    StockFundamentalsWithNamesDao stockFundamentalsWithNamesDao;
    StockFundamentalsRepository stockFundamentalsRepository;

    SectorRepository sectorRepository;
    SubSectorRepository subSectorRepository;
    StockPriceHistoryRepository stockPriceHistoryRepository;

    StockCalculationsClient stockCalculationsClient;

    @Autowired
    public MarketAnalyticsService(StockPriceHistoryDao stockPriceHistoryDao,
                                  StockFundamentalsWithNamesDao stockFundamentalsWithNamesDao,
                                  StockFundamentalsRepository stockFundamentalsRepository,
                                  SectorRepository sectorRepository,
                                  SubSectorRepository subSectorRepository,
                                  StockPriceHistoryRepository stockPriceHistoryRepository,
                                  StockCalculationsClient stockCalculationsClient) {
        this.stockPriceHistoryDao = stockPriceHistoryDao;
        this.stockFundamentalsWithNamesDao = stockFundamentalsWithNamesDao;
        this.stockFundamentalsRepository = stockFundamentalsRepository;
        this.sectorRepository = sectorRepository;
        this.subSectorRepository = subSectorRepository;
        this.stockPriceHistoryRepository = stockPriceHistoryRepository;
        this.stockCalculationsClient = stockCalculationsClient;
    }

    public List<StocksPriceHistoryVO> getSingleStockPriceHistory(String tickerSymbol, LocalDate fromDate, LocalDate toDate,
                                                                 Optional<String> sortFieldOptional, Optional<String> sortDirectionOptional){
        List<StocksPriceHistoryVO> stockPriceHistoryList = stockPriceHistoryDao.getSingleStockPriceHistory(tickerSymbol, fromDate, toDate);

        String fieldToSortBy = sortFieldOptional.orElse("TradingDate");
        String directionToSortBy = sortDirectionOptional.orElse("asc");

         Comparator sortComparator = switch (fieldToSortBy.toUpperCase()){
            case("OPENPRICE") -> Comparator.comparing(StocksPriceHistoryVO::getOpenPrice);
            case("CLOSEPRICE") -> Comparator.comparing(StocksPriceHistoryVO::getClosePrice);
            case("VOLUME") -> Comparator.comparing(StocksPriceHistoryVO::getVolume);
            case("TRADINGDATE") -> Comparator.comparing(StocksPriceHistoryVO::getTradingDate);
            default -> {
                LOGGER.error("Value entered for sortField is incorrect. Value entered is {}",fieldToSortBy);
                throw new IllegalArgumentException("Value entered for sortField is incorrect. Value entered is "+fieldToSortBy);
            }

        };

         if(directionToSortBy.equalsIgnoreCase("DESC")){
             sortComparator = sortComparator.reversed();
         }

        //Collections.sort(stockPriceHistoryList, sortComparator);
        stockPriceHistoryList.sort(sortComparator);

        return stockPriceHistoryList;
    }

    public List<StocksPriceHistoryVO> getMultipleStockPriceHistory(List<String> tickersList, LocalDate fromDate, LocalDate toDate){
        return stockPriceHistoryDao.getMultipleStockPriceHistory(tickersList, fromDate, toDate);
    }

    public List<StockFundamentalsWithNamesVO> getAllStockFundamentals(){
        LOGGER.debug("Entered the method getAllStockFundamentals() of the class {}", getClass());
        List<StockFundamentalsWithNamesVO> stockFundamentalsList = stockFundamentalsWithNamesDao.getAllStockFundamentalsWithNamesVO();
        LOGGER.debug("Generated a list of size {} from the DAO layer ", stockFundamentalsList.size());
        return stockFundamentalsList;
    }

    public List<StockFundamentalsWithNamesVO> getSomeStockFundamentals(List<String> tickersList){
        return stockFundamentalsWithNamesDao.getSomeStockFundamentals(tickersList);
    }

    public List<StockFundamentals> getAllStockFundamentalsJPA(){
        return stockFundamentalsRepository.findAll();
    }

    public List<Sector> getAllSectors(){
        return sectorRepository.findAll();
    }

    public List<SubSector> getAllSubSectors(){
        return subSectorRepository.findAll();
    }

    public Optional<StockPriceHistory> getStockPriceHistory(String tickerSymbol, LocalDate tradingDate){

        StockPriceHistoryKey primaryKeyObj = new StockPriceHistoryKey();
        primaryKeyObj.setTickerSymbol(tickerSymbol);
        primaryKeyObj.setTradingDate(tradingDate);

        return stockPriceHistoryRepository.findById(primaryKeyObj);
    }

    public List<TopStockBySectorVO> getTopStockBySector(){
        return stockFundamentalsRepository.getTopStockBySector();
    }

    public List<StockFundamentals> getTopNStocksNativeSQL(Integer num){
        return stockFundamentalsRepository.getTopNStocksNativeSQL(num);
    }

    public List<StockFundamentals> getTopNStocksJPQL(Integer num){
        return stockFundamentalsWithNamesDao.getTopNStocksJPQL(num);
    }

    public List<StockFundamentals> getTopNStocksCriteria(Integer num){
        return stockFundamentalsWithNamesDao.getTopNStocksCriteriaAPI(num);
    }

    public List<StockFundamentals> getNotNullCurrentRatioStocks(){
        return stockFundamentalsRepository.getNotNullCurrentRatioStocks();
    }

    public List<SectorVO> getTop3StocksBySector(){
        List<TopStockBySectorVO> top3StocksBySectorList = stockFundamentalsRepository.getTop3StocksBySector();


        //Group by sectorID to get a Map with SectorID as key and List of stocks for that sector as value
        Map<Integer, List<TopStockBySectorVO>> topStocksBySectorIDMap = top3StocksBySectorList.stream()
                .collect(Collectors.groupingBy(TopStockBySectorVO::getSectorID));

        List<SectorVO> finalOutputList = new ArrayList<>();

        //For each entry of the map, we need to create and populate SectorVO object and then push the object into finalOutputList
        topStocksBySectorIDMap.forEach((sectorID, stocksList)-> {
            SectorVO sectorVO = new SectorVO();
            List<StockVO> topStocksList = new ArrayList<>();
            sectorVO.setSectorID(sectorID);
            //Iterate the list to get the rest of the values out
            stocksList.forEach(topStockBySectorVO -> {
                sectorVO.setSectorName(topStockBySectorVO.getSectorName());
                StockVO stockVO = new StockVO();
                stockVO.setTickerSymbol(topStockBySectorVO.getTickerSymbol());
                stockVO.setTickerName(topStockBySectorVO.getTickerName());
                stockVO.setMarketCap(topStockBySectorVO.getMarketCap());
                topStocksList.add(stockVO);//Adding each StockVO object into the topStocksList
            });
            sectorVO.setTopStocks(topStocksList); //Add list to the SectorVO object
            finalOutputList.add(sectorVO);
        });

        finalOutputList.sort(Comparator.comparing(SectorVO::getSectorName));

        return finalOutputList;
    }

    public List<StockFundamentalsWithNamesVO> getTopNPerformingStocks(Integer num, LocalDate fromDate, LocalDate toDate, Long marketCapLimit){
        //List<StockFundamentals> allStocksList = stockFundamentalsRepository.findAll();

        List<StockFundamentalsWithNamesVO> allStocksList = populateCumulativeReturnIntoStocks(fromDate, toDate);

        List<StockFundamentalsWithNamesVO> finalOutputList = allStocksList.stream()
                .filter(stockFundamentals -> stockFundamentals.getCumulativeReturn() != null)
                .filter((stockFundamentals -> stockFundamentals.getMarketCap().compareTo(new BigDecimal(marketCapLimit)) > 0))
                .sorted(Comparator.comparing(StockFundamentalsWithNamesVO::getCumulativeReturn, Comparator.nullsFirst(BigDecimal::compareTo)).reversed())
                .limit(num)
                .collect(Collectors.toList());
        return finalOutputList;
    }

    /**
     * This common method generates StockFundamentalsList of all stocks in the database, with the cumulativeReturn values populated
     * CumulativeReturn values are fetched by hitting the CumulativeReturns Webservice
     * @param fromDate
     * @param toDate
     * @return List<StockFundamentalsWithNamesVO>
     */
    private List<StockFundamentalsWithNamesVO> populateCumulativeReturnIntoStocks(LocalDate fromDate, LocalDate toDate) {
        List<StockFundamentalsWithNamesVO> allStocksList = stockFundamentalsWithNamesDao.getAllStockFundamentalsWithNamesVO();

        List<String> allTickersList = allStocksList.stream()
                .map(stockFundamentals -> stockFundamentals.getTickerSymbol())
                .collect(Collectors.toList());

        LOGGER.info("Number of stocks that are being sent as inputs to the Cumulative Return Web Service is {}",allTickersList.size());
        CRWSInputVO crwsInputVO = new CRWSInputVO();
        crwsInputVO.setTickers(allTickersList);

        List<CRWSOutputVO> cumulativeReturnsList = stockCalculationsClient.getCumulativeReturns(fromDate, toDate, crwsInputVO);
        LOGGER.info("Number of stocks returned from the Cumulative Return Web Service {}", cumulativeReturnsList.size());

        if(cumulativeReturnsList==null || cumulativeReturnsList.isEmpty()){
            throw new StockException("Cumulative Return Webservice is down");
        }

        Map<String, BigDecimal> cumulativeReturnByTickerSymbolMap = cumulativeReturnsList.stream()
                .collect(Collectors.toMap(
                        CRWSOutputVO::getTickerSymbol,
                        CRWSOutputVO::getCumulativeReturn
                ));

        allStocksList.forEach(stockFundamentals -> {
            stockFundamentals.setCumulativeReturn(cumulativeReturnByTickerSymbolMap.get(stockFundamentals.getTickerSymbol()));
        });
        return allStocksList;
    }

    public List<SubSectorVO> getTopPerformingStocksBySubSector(Integer num, LocalDate fromDate, LocalDate toDate){
        List<StockFundamentalsWithNamesVO> allStocksList = populateCumulativeReturnIntoStocks(fromDate, toDate);

        //Grouping by SubSectorName to get Map with SubSectorName as key and List of Stocks belonging to that SubSector
        Map<String, List<StockFundamentalsWithNamesVO>> stocksListBySubSectorNameMap = allStocksList.stream()
                .collect(Collectors.groupingBy(StockFundamentalsWithNamesVO::getSubSectorName));

        //THis map contains SubSectorName as key, and List of Top N Performing Stocks as value
        Map<String, List<StockFundamentalsWithNamesVO>> topStocksListBySectorNameMap = new HashMap<>();

        stocksListBySubSectorNameMap.forEach((sectorName, stocksList) -> {
            List<StockFundamentalsWithNamesVO> topNPerformingList = stocksList.stream()
                    .filter(stockFundamentalsWithNamesVO -> stockFundamentalsWithNamesVO.getCumulativeReturn() != null)
                    .sorted(Comparator.comparing(StockFundamentalsWithNamesVO::getCumulativeReturn).reversed())
                    .limit(num)
                    .collect(Collectors.toList());
            topStocksListBySectorNameMap.put(sectorName, topNPerformingList);
        });

        List<SubSectorVO> finalOutputList = new ArrayList<>();

        topStocksListBySectorNameMap.forEach((subSectorName, topStocksList) -> {
            SubSectorVO subSectorVO = new SubSectorVO();
            subSectorVO.setSubSectorName(subSectorName);
            List<StockVO> topNStocksList = new ArrayList<>();
            topStocksList.forEach(stockFundamentalsWithNamesVO -> {
                subSectorVO.setSubSectorID(stockFundamentalsWithNamesVO.getSubSectorID());
                StockVO stockVO = new StockVO();
                stockVO.setTickerSymbol(stockFundamentalsWithNamesVO.getTickerSymbol());
                stockVO.setTickerName(stockFundamentalsWithNamesVO.getTickerName());
                stockVO.setMarketCap(stockFundamentalsWithNamesVO.getMarketCap());
                stockVO.setCumulativeReturn(stockFundamentalsWithNamesVO.getCumulativeReturn());
                topNStocksList.add(stockVO);
            });
            subSectorVO.setTopStocks(topNStocksList);
            finalOutputList.add(subSectorVO);
        });

        finalOutputList.sort(Comparator.comparing(SubSectorVO::getSubSectorName));

        return finalOutputList;
    }
}
