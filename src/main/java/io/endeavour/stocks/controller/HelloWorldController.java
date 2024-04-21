package io.endeavour.stocks.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RestController
public class HelloWorldController {

    @GetMapping(value = "/helloworld")
    public String firstMethod(){
        return "Hello World !";
    }

    @GetMapping(value = "hello/world")
    public String callingHelloWorldAgain(){
        return "Hello World Again !";
    }

    @GetMapping(value = "concatString/{inputString}") //Annotation
    public String concatString(@PathVariable(value = "inputString") String sampleString){ //Sending string value in Path parameter
        return sampleString+sampleString;
    }

    @GetMapping(value = "contactStringDate/{inputString}/{inputDate}")
    public String concatStringDate(@PathVariable(value = "inputString") String sampleString,
                                   @PathVariable(value = "inputDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate sampleDate){
        return sampleString+sampleDate.minusMonths(5);
    }

    @GetMapping(value = "concatStringDate/queryParam/{inputString}")
    public String concatStringDateQP(@PathVariable(value = "inputString") String sampleString,
                                     @RequestParam(value = "inputDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate sampleDate){
        return sampleString+sampleDate.plusMonths(6);
    }

    @PostMapping(value = "sortTickers/{inputString}")
    public List<String> sortTickerList(@PathVariable(value = "inputString") String sampleString, //Path parameter
                                       @RequestParam(value = "inputDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate someDate, //Query Param
                                       @RequestBody List<String> tickerList){
        Collections.sort(tickerList);
        return tickerList;
    }

}
