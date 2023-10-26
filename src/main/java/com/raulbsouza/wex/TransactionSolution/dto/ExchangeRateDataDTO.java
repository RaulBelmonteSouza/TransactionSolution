package com.raulbsouza.wex.TransactionSolution.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateDataDTO {
    List<ExchangeRateDTO> data = new ArrayList<>();
}
