package com.gbce.stocks;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class StockTest {

    private static final int QUANTITY = 1;
    private static final int NUMBER_OF_TRANSACTIONS = 1_000_000;

    private Stock teaStock;
    private Stock popStock;

    @Before
    public void init(){
        teaStock = StockFactory.getStock("TEA");
        popStock = StockFactory.getStock("POP");
    }

    @Test
    public void whenStockHasBeenBoughtThenStocksLastTradeMustExist() {
        teaStock.buy(QUANTITY, BigDecimal.ONE);
        assertNotNull(teaStock.getLastTrade());
    }

    @Test
    public void whenStockHasBeenSoldThenStocksLastTradeMustExist() {
        teaStock.sell(QUANTITY, BigDecimal.ONE);
        assertNotNull(teaStock.getLastTrade());
    }

    @Test
    public void whenStockHasBeenBoughtThenStocksLastTradeMustHaveBuyTradeIndicator() {
        teaStock.buy(QUANTITY, BigDecimal.ONE);
        assertEquals(Trade.TradeIndicator.BUY, teaStock.getLastTrade().getIndicator());
    }

    @Test
    public void whenStockHasBeenSoldThenStocksLastTradeMustHaveBuyTradeIndicator() {
        teaStock.sell(QUANTITY, BigDecimal.ONE);
        assertEquals(Trade.TradeIndicator.SELL, teaStock.getLastTrade().getIndicator());
    }

    @Test
    public void whenTradesHaveBeenAddedToStocksReturnedTradesThenNumberOfTradesOfStockMustNotChange() {
        IntStream.rangeClosed(1, 9).forEach(element -> teaStock.buy(QUANTITY, BigDecimal.ONE));
        List<Trade> trades = teaStock.getTrades();
        trades.add(new Trade(100, BigDecimal.ONE, Instant.now(), Trade.TradeIndicator.BUY));
        assertEquals(9, teaStock.getTrades().size());
    }

    @Test
    public void whenTradesHaveBeenDoneThenAllTradesForAGivenStockMustBeInTheOrderAccordingToTheTimeOfTrade() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(NUMBER_OF_TRANSACTIONS);
        Set<Callable<Void>> transactions = new HashSet<>();
        Random random = new Random(1);
        IntStream.range(0, NUMBER_OF_TRANSACTIONS).forEach(element -> transactions.add(() -> {
            if (random.nextBoolean())
                teaStock.buy(QUANTITY, BigDecimal.ONE);
            else
                teaStock.sell(QUANTITY, BigDecimal.ONE);
            countDownLatch.countDown();
            return null;
        }));
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()).invokeAll(transactions);
        countDownLatch.await();

        Iterator<Trade> it = teaStock.getTrades().iterator();
        Trade trade = it.next();
        while (it.hasNext()) {
            Trade nextTrade = it.next();
            assertTrue(trade.getTimestamp().toEpochMilli() <= nextTrade.getTimestamp().toEpochMilli());
            trade = nextTrade;
        }
    }

    @Test
    public void whenBuyTradesHaveBeenDoneThenNumberOfTradesAndNumberOfTransactionsMustBeEqual() {
        IntStream.rangeClosed(1, 4).forEach(element -> teaStock.buy(QUANTITY, BigDecimal.ONE));
        assertEquals(4, teaStock.getTrades().size());
    }

    @Test
    public void whenSellTradesHaveBeenDoneThenNumberOfTradesAndNumberOfTransactionsMustBeEqual() {
        IntStream.rangeClosed(1, 10).forEach(element -> teaStock.sell(QUANTITY, BigDecimal.ONE));
        assertEquals(10, teaStock.getTrades().size());
    }

    @Test
    public void whenBuyAndSellTradesHaveBeenDoneThenNumberOfTradesAndNumberOfTransactionsMustBeEqual() {
        IntStream.rangeClosed(1, 50).forEach(element -> teaStock.buy(QUANTITY, BigDecimal.ONE));
        IntStream.rangeClosed(1, 50).forEach(element -> teaStock.sell(QUANTITY, BigDecimal.ONE));
        assertEquals(100, teaStock.getTrades().size());
    }

    @Test
    public void whenLastDividendIsZeroThenPERatioCannotBeEvaluated() {
        assertNull(teaStock.getPERatio());
    }

    @Test
    public void whenLastDividendIsNotZeroAndThereHasBeenNoTradesYetThenPERatioCannotBeEvaluated() {
        assertNull(popStock.getPERatio());
    }

    @Test
    public void whenTradesHaveBeenDoneAndLastDividendIsZeroThenPERatioCannotBeEvaluated() {
        teaStock.buy(QUANTITY, BigDecimal.ONE);
        assertNull(teaStock.getPERatio());
    }

    @Test
    public void whenTradesHaveBeenDoneAndLastDividendIsMoreThanZeroThenPERatioCanBeEvaluated() {
        popStock.buy(QUANTITY, BigDecimal.ONE);
        assertNotNull(popStock.getPERatio());
    }

    @Test
    public void whenTradesHaveBeenDoneAndLastDividendIsMoreThanZeroThenPERatioIsEvaluatedCorrectly() {
        popStock.sell(QUANTITY, new BigDecimal(357));
        assertEquals(new BigDecimal("44.625"), popStock.getPERatio());
    }

    @Test
    public void whenTradesHaveBeenDoneAndSpecifiedTimestampIndicatesThatNoTradesQualifyForEvaluationThenVolumeWeightedStockPriceCannotBeEvaluated() throws InterruptedException {
        popStock.sell(QUANTITY, BigDecimal.valueOf(1));
        popStock.buy(QUANTITY, BigDecimal.valueOf(1));
        Thread.sleep(1);
        assertEquals(null, popStock.getVolumeWeightedStockPrice(Instant.now()));
    }

    @Test
    public void whenTradesHaveBeenDoneAndSpecifiedTimestampIndicatesThatSomeTradesQualifyForEvaluationThenVolumeWeightedStockPriceIsEvaluatedCorrectly() throws InterruptedException {
        popStock.sell(55, BigDecimal.valueOf(12));
        Thread.sleep(1);
        Instant instant = Instant.now();
        popStock.sell(23, BigDecimal.valueOf(12));
        popStock.buy(34, BigDecimal.valueOf(13));
        assertEquals(new BigDecimal("12.60"), popStock.getVolumeWeightedStockPrice(instant).setScale(2, BigDecimal.ROUND_HALF_EVEN));
    }

    @Test
    public void whenTradesHaveBeenDoneThenVolumeWeightedStockPriceIsEvaluatedCorrectlyForTheTradesBasedInLastFiveMinutes() {
        popStock.sell(23, BigDecimal.valueOf(12));
        popStock.buy(34, BigDecimal.valueOf(13));
        assertEquals(new BigDecimal("12.60"), popStock.getVolumeWeightedStockPrice().setScale(2, BigDecimal.ROUND_HALF_EVEN));
    }
}
