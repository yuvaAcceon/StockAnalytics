package io.endeavour.stocks.vo;

import java.util.List;

public class SectorVO {
    private Integer sectorID;
    private String sectorName;
    private List<StockVO> topStocks;

    public Integer getSectorID() {
        return sectorID;
    }

    public void setSectorID(Integer sectorID) {
        this.sectorID = sectorID;
    }

    public String getSectorName() {
        return sectorName;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    public List<StockVO> getTopStocks() {
        return topStocks;
    }

    public void setTopStocks(List<StockVO> topStocks) {
        this.topStocks = topStocks;
    }

    @Override
    public String toString() {
        return "SectorVO{" +
                "sectorID=" + sectorID +
                ", sectorName='" + sectorName + '\'' +
                ", topStocks=" + topStocks +
                '}';
    }
}
