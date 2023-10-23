package com.raulbsouza.wex.TransactionSolution.model;

import lombok.Getter;

@Getter
public enum FiscalQuarter {
    Q1_OCTOBER_TO_DECEMBER(1),
    Q2_JANUARY_TO_MARCH(2),
    Q3_APRIL_TO_JUNE(3),
    Q4_JULY_TO_SEPTEMBER(4);

    private final int value;

    FiscalQuarter(int value) {
        this.value = value;
    }
}
