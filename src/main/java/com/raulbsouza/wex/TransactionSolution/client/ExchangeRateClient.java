package com.raulbsouza.wex.TransactionSolution.client;

import com.raulbsouza.wex.TransactionSolution.dto.ExchangeRateDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "ExchangeRateClient", url = ExchangeRateFields.BASE_URL)
public interface ExchangeRateClient {

    String GET_ALL_VALID_CURRENCIES
            = "?fields={fields}&filter=record_date:gte:{data}&page[size]=350";
    String GET_EXCHANGE_HISTORY_FOR_COUNTRY = "?fields={fields}" +
            "&filter=country_currency_desc:eq:{currency},record_date:gte:{startDate},record_date:lte:{endDate}" +
            "&sort=-record_date";

    @RequestMapping(method = RequestMethod.GET,
            value = ExchangeRateFields.RATES_OF_EXCHANGE_ENDPOINT + GET_ALL_VALID_CURRENCIES)
    ExchangeRateDataDTO getValidCurrencies(@PathVariable String[] fields, @PathVariable String data);

    @RequestMapping(method = RequestMethod.GET,
            value = ExchangeRateFields.RATES_OF_EXCHANGE_ENDPOINT + GET_EXCHANGE_HISTORY_FOR_COUNTRY)
    ExchangeRateDataDTO getExchangeHistoryForCountry(@PathVariable String[] fields,
                                                 @PathVariable String currency,
                                                 @PathVariable String startDate,
                                                 @PathVariable String endDate);
}
