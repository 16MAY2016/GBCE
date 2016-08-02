package com.gbce.stocks;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

public class StockCommonTest {

    private Stock stock;

    @Before
    public void init(){
        stock = StockFactory.getStock("POP");
    }

    @Test(expected = NullPointerException.class)
    public void constructorShouldThrowExceptionWhenStockSymbolIsNotSpecified() {
        new StockCommon(null, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionWhenParValueIsLessThanZero() {
        new StockCommon("StockSymbol", -1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionWhenLastDividendIsLessThanZero() {
        new StockCommon("StockSymbol", 0, -1);
    }

    @Test
    public void whenThereHaveBeenTradesDoneThenDividendYieldIsEvaluatedCorrectly() {
        stock.sell(1, BigDecimal.valueOf(15));
        assertEquals(new BigDecimal("0.53"), stock.getDividendYield().setScale(2, BigDecimal.ROUND_HALF_EVEN));
    }

    @Test
    public void whenThereHaveNotBeenAnyTradesDoneYetThenDividendYieldIsNull()  {
        assertNull(stock.getDividendYield());
    }
}
