package io.endeavour.stocks.entity.stocks;

import javax.persistence.*;
import javax.swing.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "stocks_price_history", schema = "endeavour")
@IdClass(value = StockPriceHistoryKey.class) // Add this annotation when the table has a composite primary key
public class StockPriceHistory {
    @Column(name = "ticker_symbol")
    @Id
    private String tickerSymbol;
    @Column(name = "trading_date")
    @Id
    private LocalDate tradingDate;
    @Column(name = "open_price")
    private BigDecimal openPrice;
    @Column(name = "close_price")
    private BigDecimal closePrice;
    @Column(name = "volume")
    private Long volume;

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public LocalDate getTradingDate() {
        return tradingDate;
    }

    public void setTradingDate(LocalDate tradingDate) {
        this.tradingDate = tradingDate;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockPriceHistory that = (StockPriceHistory) o;
        return Objects.equals(tickerSymbol, that.tickerSymbol) && Objects.equals(tradingDate, that.tradingDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tickerSymbol, tradingDate);
    }
}
