package com.raulbsouza.wex.TransactionSolution.client;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class ClientConfiguration {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
