package com.gbce.stocks;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

public class StockPreferredTest {

    private Stock stock;

    @Before
    public void init(){
        stock = StockFactory.getStock("GIN");
    }

    @Test(expected = NullPointerException.class)
    public void constructorShouldThrowExceptionWhenStockSymbolIsNotSpecified()  {
        new StockPreferred(null, 0, 0, BigDecimal.ONE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionForCommonStockWhenParValueIsLessThanZero()  {
        new StockPreferred("StockSymbol", -1, 0, BigDecimal.ONE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionWhenLastDividendIsLessThanZero() {
        new StockPreferred("StockSymbol", 0, -1, BigDecimal.ONE);
    }

    @Test(expected = NullPointerException.class)
    public void constructorShouldThrowExceptionWhenFixedDividendIsNotSpecified() {
        new StockPreferred("StockSymbol", 0, 0, null);
    }

    @Test
    public void whenThereHaveBeenTradesDoneThenDividendYieldIsEvaluatedCorrectly() {
        stock.sell(1, BigDecimal.valueOf(15));
        assertEquals(new BigDecimal("0.13"), stock.getDividendYield().setScale(2, BigDecimal.ROUND_HALF_EVEN));
    }

    @Test
    public void whenThereHaveNotBeenAnyTradesDoneYetThenDividendYieldIsNull()  {
        assertNull(stock.getDividendYield());
    }
}
