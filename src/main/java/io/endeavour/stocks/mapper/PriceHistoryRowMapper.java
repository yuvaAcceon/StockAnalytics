package io.endeavour.stocks.mapper;

import io.endeavour.stocks.vo.StocksPriceHistoryVO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PriceHistoryRowMapper implements RowMapper<StocksPriceHistoryVO> {
    @Override
    public StocksPriceHistoryVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        StocksPriceHistoryVO stocksPriceHistoryVO = new StocksPriceHistoryVO();

        stocksPriceHistoryVO.setTickerSymbol(rs.getString("ticker_symbol"));
        stocksPriceHistoryVO.setTradingDate(rs.getDate("trading_date").toLocalDate());
        stocksPriceHistoryVO.setOpenPrice(rs.getBigDecimal("open_price"));
        stocksPriceHistoryVO.setClosePrice(rs.getBigDecimal("close_price"));
        stocksPriceHistoryVO.setVolume(rs.getLong("volume"));

        return stocksPriceHistoryVO;
    }
}
