package com.raulbsouza.wex.TransactionSolution.client;

import com.raulbsouza.wex.TransactionSolution.exception.ExchangeRateClientException;
import com.raulbsouza.wex.TransactionSolution.exception.ResourceNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class ExchangeRateClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {

        return switch (response.status()) {
            case 400 -> new ExchangeRateClientException("Request to rates of exchange api was malformed");
            case 404 -> new ResourceNotFoundException("Failing to retrieve resource from rates of exchange api");
            default -> new Exception("Error at exchange rate client. Client response: "
                    + response.status() + " " + response.reason());
        };
    }
}
