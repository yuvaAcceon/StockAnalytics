package io.endeavour.stocks.vo;

import java.util.List;

public class SubSectorVO {
    private String subSectorName;
    private Integer subSectorID;
    private List<StockVO> topStocks;

    public String getSubSectorName() {
        return subSectorName;
    }

    public void setSubSectorName(String subSectorName) {
        this.subSectorName = subSectorName;
    }

    public Integer getSubSectorID() {
        return subSectorID;
    }

    public void setSubSectorID(Integer subSectorID) {
        this.subSectorID = subSectorID;
    }

    public List<StockVO> getTopStocks() {
        return topStocks;
    }

    public void setTopStocks(List<StockVO> topStocks) {
        this.topStocks = topStocks;
    }
}
