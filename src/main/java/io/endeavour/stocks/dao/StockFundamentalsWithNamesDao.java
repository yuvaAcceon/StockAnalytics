package io.endeavour.stocks.dao;

import io.endeavour.stocks.entity.stocks.Stock;
import io.endeavour.stocks.entity.stocks.StockFundamentals;
import io.endeavour.stocks.vo.StockFundamentalsWithNamesVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Component
public class StockFundamentalsWithNamesDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(StockFundamentalsWithNamesDao.class);
    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    @Qualifier(value = "entityManagerFactory")
    EntityManager entityManager;

    public List<StockFundamentalsWithNamesVO> getAllStockFundamentalsWithNamesVO(){
        LOGGER.debug("Now in the getAllStockFundamentalsWithNamesVO() method of the class {} ", getClass());
        String sqlQuery = """
                	select
                		sf.ticker_symbol,
                		sl.ticker_name,
                		sf.sector_id,
                		sl1.sector_name,
                		sf.subsector_id,
                		sl2.subsector_name,
                		sf.market_cap,
                		sf.current_ratio
                	from
                		endeavour.stock_fundamentals sf,
                		endeavour.stocks_lookup sl,
                		endeavour.sector_lookup sl1,
                		endeavour.subsector_lookup sl2
                	where
                		sf.ticker_symbol = sl.ticker_symbol
                		and sf.sector_id = sl1.sector_id
                		and sf.subsector_id = sl2.subsector_id
                	
                """;
        List<StockFundamentalsWithNamesVO> stockFundamentalsList = namedParameterJdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> {
                    StockFundamentalsWithNamesVO stockFundamentalsWithNamesVO = new StockFundamentalsWithNamesVO();

                    stockFundamentalsWithNamesVO.setTickerSymbol(rs.getString("ticker_symbol"));
                    stockFundamentalsWithNamesVO.setTickerName(rs.getString("ticker_name"));
                    stockFundamentalsWithNamesVO.setSectorID(rs.getInt("sector_id"));
                    stockFundamentalsWithNamesVO.setSectorName(rs.getString("sector_name"));
                    stockFundamentalsWithNamesVO.setSubSectorID(rs.getInt("subsector_id"));
                    stockFundamentalsWithNamesVO.setSubSectorName(rs.getString("subsector_name"));
                    stockFundamentalsWithNamesVO.setMarketCap(rs.getBigDecimal("market_cap"));
                    stockFundamentalsWithNamesVO.setCurrentRatio(rs.getBigDecimal("current_ratio"));

                    return stockFundamentalsWithNamesVO;
                });
        LOGGER.debug("Generated list from thegetAllStockFundamentalsWithNamesVO() in the class {} is ",getClass(), stockFundamentalsList);
        return stockFundamentalsList;
    }

    public List<StockFundamentalsWithNamesVO> getSomeStockFundamentals(List<String> tickersList){
        LOGGER.debug("Now in the getSomeStockFundamentals() method of the class {} ", getClass());

        LOGGER.info("Input List received by the getSomeStockFundamentals() method of the {} class is {} ", getClass(), tickersList);
        String sqlQuery = """
                	select
                		sf.ticker_symbol,
                		sl.ticker_name,
                		sf.sector_id,
                		sl1.sector_name,
                		sf.subsector_id,
                		sl2.subsector_name,
                		sf.market_cap,
                		sf.current_ratio
                	from
                		endeavour.stock_fundamentals sf,
                		endeavour.stocks_lookup sl,
                		endeavour.sector_lookup sl1,
                		endeavour.subsector_lookup sl2
                	where
                		sf.ticker_symbol = sl.ticker_symbol
                		and sf.sector_id = sl1.sector_id
                		and sf.subsector_id = sl2.subsector_id
                		and sf.ticker_symbol IN (:inputTickers)
                """;
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("inputTickers", tickersList);
        List<StockFundamentalsWithNamesVO> stockFundamentalsList = namedParameterJdbcTemplate.query(sqlQuery, mapSqlParameterSource,
                (rs, rowNum) -> {
                    StockFundamentalsWithNamesVO stockFundamentalsWithNamesVO = new StockFundamentalsWithNamesVO();

                    stockFundamentalsWithNamesVO.setTickerSymbol(rs.getString("ticker_symbol"));
                    stockFundamentalsWithNamesVO.setTickerName(rs.getString("ticker_symbol"));
                    stockFundamentalsWithNamesVO.setSectorID(rs.getInt("sector_id"));
                    stockFundamentalsWithNamesVO.setSectorName(rs.getString("sector_name"));
                    stockFundamentalsWithNamesVO.setSubSectorID(rs.getInt("subsector_id"));
                    stockFundamentalsWithNamesVO.setSubSectorName(rs.getString("subsector_name"));
                    stockFundamentalsWithNamesVO.setMarketCap(rs.getBigDecimal("market_cap"));
                    stockFundamentalsWithNamesVO.setCurrentRatio(rs.getBigDecimal("current_ratio"));

                    return stockFundamentalsWithNamesVO;
                });
        LOGGER.info("Generated a Stock Fundamentals list of size {} ", stockFundamentalsList.size());
        LOGGER.debug("Generated Stock Fundamentals list is {} ", stockFundamentalsList);

        return stockFundamentalsList;
    }

    public List<StockFundamentals> getTopNStocksJPQL(Integer num){
        String jpqlQuery = """
                select
                    sf
                from
                    StockFundamentals sf
                where
                    sf.marketCap is not null
                order by sf.marketCap desc  
                """;
        TypedQuery<StockFundamentals> query = entityManager.createQuery(jpqlQuery, StockFundamentals.class);
        query.setMaxResults(num);
        return query.getResultList();
    }

    public List<StockFundamentals> getTopNStocksCriteriaAPI(Integer num){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<StockFundamentals> criteriaQuery = cb.createQuery(StockFundamentals.class);
        Root<StockFundamentals> root = criteriaQuery.from(StockFundamentals.class);
        criteriaQuery.select(root)
                .where(cb.isNotNull(root.get("marketCap")))
                .orderBy(cb.desc(root.get("marketCap")));
        List<StockFundamentals> resultList = entityManager.createQuery(criteriaQuery)
                .setMaxResults(num)
                .getResultList();
        return resultList;
    }
}
