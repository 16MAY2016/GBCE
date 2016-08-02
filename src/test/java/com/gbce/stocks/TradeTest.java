package com.gbce.stocks;

import com.gbce.stocks.Trade;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static junit.framework.TestCase.assertEquals;

public class TradeTest {

    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionWhenQuantityEqualToZero() {
        new Trade(0, BigDecimal.valueOf(1), Instant.now(), Trade.TradeIndicator.BUY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionWhenQuantityIsLessThanZero()  {
        new Trade(-1, BigDecimal.valueOf(1), Instant.now(), Trade.TradeIndicator.BUY);
    }

    @Test(expected = NullPointerException.class)
    public void constructorShouldThrowExceptionWhenPriceIsNotSpecified()  {
        new Trade(1, null, Instant.now(), Trade.TradeIndicator.BUY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionWhenPriceIsEqualToZero()  {
        new Trade(1, BigDecimal.valueOf(0), Instant.now(), Trade.TradeIndicator.BUY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionWhenPriceIsLessThanZero()  {
        new Trade(1, BigDecimal.valueOf(-1), Instant.now(), Trade.TradeIndicator.BUY);
    }

    @Test(expected = NullPointerException.class)
    public void constructorShouldThrowExceptionWhenTimestampIsNotSpecified()  {
        new Trade(1, BigDecimal.valueOf(1), null, Trade.TradeIndicator.BUY);
    }

    @Test(expected = NullPointerException.class)
    public void constructorShouldThrowExceptionWhenTradeIndicatorIsNotSpecified()  {
        new Trade(1, BigDecimal.valueOf(1), Instant.now(), null);
    }

    @Test
    public void whenTradeHasBeenCreatedThenTradeTotalMustBeEvaluatedCorrectly()  {
        assertEquals(BigDecimal.valueOf(213).setScale(2, BigDecimal.ROUND_HALF_EVEN),
                new Trade(100, new BigDecimal("2.13"), Instant.now(), Trade.TradeIndicator.BUY).getTotal());
    }
}
