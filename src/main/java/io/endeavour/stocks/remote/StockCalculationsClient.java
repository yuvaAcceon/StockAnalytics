package io.endeavour.stocks.remote;

import io.endeavour.stocks.config.StocksFeignConfig;
import io.endeavour.stocks.remote.vo.CRWSInputVO;
import io.endeavour.stocks.remote.vo.CRWSOutputVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "stockCalculationsClient", configuration = {StocksFeignConfig.class}, url = "${client.stock-calculations.url}")
public interface StockCalculationsClient {

    @PostMapping(value = "/calculate/cumulative-return/{fromDate}/{toDate}")
    public List<CRWSOutputVO> getCumulativeReturns(@PathVariable(value = "fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
                                                   @PathVariable(value = "toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
                                                   @RequestBody CRWSInputVO crwsInputVO);

}




