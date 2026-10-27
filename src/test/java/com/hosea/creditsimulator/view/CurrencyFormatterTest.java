package com.hosea.creditsimulator.view;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurrencyFormatterTest {

    private final CurrencyFormatter formatter = new CurrencyFormatter();

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "3500000|Rp. 3,500,000.00",
            "2289233.70|Rp. 2,289,233.70",
            "1000000000|Rp. 1,000,000,000.00",
            "0.005|Rp. 0.01",
            "0|Rp. 0.00"
    })
    void formatsRupiah(String amount, String expected) {
        assertEquals(expected, formatter.formatRupiah(new BigDecimal(amount)));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "8|8%",
            "8.0|8%",
            "8.1|8,1%",
            "8.60|8,6%",
            "10|10%",
            "10.3|10,3%"
    })
    void formatsRateWithCommaDecimal(String rate, String expected) {
        assertEquals(expected, formatter.formatRate(new BigDecimal(rate)));
    }
}
