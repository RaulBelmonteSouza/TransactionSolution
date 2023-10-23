package com.raulbsouza.wex.TransactionSolution.client;

import com.raulbsouza.wex.TransactionSolution.dto.ExchangeRateDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "ExchangeRateClient", url = ExchangeRateFields.BASE_URL)
public interface ExchangeRateClient {

    String GET_ALL_VALID_CURRENCIES
            = "?fields=country_currency_desc&filter=record_date:gte:{data}&page[size]=350";

    @RequestMapping(method = RequestMethod.GET,
            value = ExchangeRateFields.RATES_OF_EXCHANGE_ENDPOINT + GET_ALL_VALID_CURRENCIES)
    ExchangeRateDataDTO getExchangeRateDate(@PathVariable String data);
}
