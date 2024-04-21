package io.endeavour.stocks.vo;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public class StockPriceHistoryRequestVO {
    private List<String> tickersList;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;

    public List<String> getTickersList() {
        return tickersList;
    }

    public void setTickersList(List<String> tickersList) {
        this.tickersList = tickersList;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }



    @Override
    public String toString() {
        return "StockPriceHistoryRequestVO{" +
                "tickersList=" + tickersList +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                '}';
    }
}
