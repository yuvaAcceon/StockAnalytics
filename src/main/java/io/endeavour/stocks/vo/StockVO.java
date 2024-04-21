package io.endeavour.stocks.vo;

import java.math.BigDecimal;

public class StockVO {
    private String tickerSymbol;
    private String tickerName;
    private BigDecimal marketCap;

    private BigDecimal cumulativeReturn;

    public BigDecimal getCumulativeReturn() {
        return cumulativeReturn;
    }

    public void setCumulativeReturn(BigDecimal cumulativeReturn) {
        this.cumulativeReturn = cumulativeReturn;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public String getTickerName() {
        return tickerName;
    }

    public void setTickerName(String tickerName) {
        this.tickerName = tickerName;
    }

    public BigDecimal getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }

    @Override
    public String toString() {
        return "StockVO{" +
                "tickerSymbol='" + tickerSymbol + '\'' +
                ", tickerName='" + tickerName + '\'' +
                ", marketCap=" + marketCap +
                ", cumulativeReturn=" + cumulativeReturn +
                '}';
    }
}
