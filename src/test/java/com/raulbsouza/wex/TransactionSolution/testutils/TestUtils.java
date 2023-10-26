package com.raulbsouza.wex.TransactionSolution.testutils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TestUtils {

    public static BigDecimal randomAmount() {
        int range = 1000;
        return new BigDecimal(Math.random() * range)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
