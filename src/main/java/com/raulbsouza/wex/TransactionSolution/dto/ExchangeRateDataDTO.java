package com.raulbsouza.wex.TransactionSolution.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class ExchangeRateDataDTO {
    List<ExchangeRateDTO> data = new ArrayList<>();
}
