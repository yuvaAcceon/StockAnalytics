package io.endeavour.stocks.entity.stocks;

import java.io.Serializable;
import java.time.LocalDate;

public class StockPriceHistoryKey implements Serializable { //Serializable is a Marker Interface
    private String tickerSymbol;
    private LocalDate tradingDate;

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

}
