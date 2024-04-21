package io.endeavour.stocks.dao;

import io.endeavour.stocks.mapper.PriceHistoryRowMapper;
import io.endeavour.stocks.vo.StocksPriceHistoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class StockPriceHistoryDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<StocksPriceHistoryVO> getSingleStockPriceHistory(String tickerSymbol, LocalDate fromDate, LocalDate toDate){
        String sqlQuery = """
                	select
                		sph.ticker_symbol,
                		sph.trading_date,
                		sph.open_price,
                		sph.close_price,
                		sph.volume
                	from
                		endeavour.stocks_price_history sph
                	where
                		sph.ticker_symbol = ?
                		and sph.trading_date between ? and ?
                """;
        Object[] inputParams = new Object[]{tickerSymbol, fromDate, toDate};
        List<StocksPriceHistoryVO> stocksPriceHistoryList = jdbcTemplate.query(sqlQuery, inputParams, new PriceHistoryRowMapper());
        return stocksPriceHistoryList;
    }

    public List<StocksPriceHistoryVO> getMultipleStockPriceHistory(List<String> tickerList, LocalDate fromDate, LocalDate toDate){
        String sqlQuery = """
                	select
                		sph.ticker_symbol,
                		sph.trading_date,
                		sph.open_price,
                		sph.close_price,
                		sph.volume
                	from
                		endeavour.stocks_price_history sph
                	where
                		sph.ticker_symbol IN (:tickerSymbols)
                		and sph.trading_date between :fromDate and :toDate
                """;

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("toDate", toDate);
        mapSqlParameterSource.addValue("tickerSymbols", tickerList);
        mapSqlParameterSource.addValue("fromDate", fromDate);

        List<StocksPriceHistoryVO> stockPriceHistoryList = namedParameterJdbcTemplate.query(sqlQuery, mapSqlParameterSource,
                (rs, rowNum)->{
                    StocksPriceHistoryVO stocksPriceHistoryVO = new StocksPriceHistoryVO();

                    stocksPriceHistoryVO.setTickerSymbol(rs.getString("ticker_symbol"));
                    stocksPriceHistoryVO.setTradingDate(rs.getDate("trading_date").toLocalDate());
                    stocksPriceHistoryVO.setOpenPrice(rs.getBigDecimal("open_price"));
                    stocksPriceHistoryVO.setClosePrice(rs.getBigDecimal("close_price"));
                    stocksPriceHistoryVO.setVolume(rs.getLong("volume"));

                    return stocksPriceHistoryVO;
                });
        return stockPriceHistoryList;
    }
}
