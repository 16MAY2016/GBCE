package com.gbce.stocks;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class Stock {

    private final int lastDividend;

    private final int parValue;

    private final String stockSymbol;

    private final List<Trade> trades = new ArrayList<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private Trade lastTrade;

    /**
     * Constructor.
     *
     * @param stockSymbol   stock symbol must be specified otherwise exception is thrown
     * @param parValue      par value must not be less than zero otherwise exception is thrown
     * @param lastDividend  par value must not be less than zero otherwise exception is thrown
     */
    protected Stock(String stockSymbol, int parValue, int lastDividend) {
        Objects.requireNonNull(stockSymbol);
        if (parValue < 0) throw new IllegalArgumentException("Par value cannot be less than 0");
        if (lastDividend < 0) throw new IllegalArgumentException("Last dividend cannot be less than 0");
        this.stockSymbol = stockSymbol;
        this.parValue = parValue;
        this.lastDividend = lastDividend;
    }

    /**
     * Buys a number of shares at given price.
     *
     * @param quantity number of shares to buy
     * @param price price
     */
    public void buy(int quantity, BigDecimal price) {
        trade(quantity, price, Trade.TradeIndicator.BUY);
    }

    /**
     * Sells a number of shares at given price.
     *
     * @param quantity number of shares to buy
     * @param price price
     */
    public void sell(int quantity, BigDecimal price) {
        trade(quantity, price, Trade.TradeIndicator.SELL);
    }

    private void trade(int quantity, BigDecimal price, Trade.TradeIndicator indicator) {
        lock.writeLock().lock();
        try {
            lastTrade = new Trade(quantity, price, Instant.now(), indicator);
            trades.add(lastTrade);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Returns the last trade.
     *
     * @return last trade or null if there have not been any trades done yet
     */
    public Trade getLastTrade() {
        lock.readLock().lock();
        try {
            return lastTrade;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Volume weighted stock price for this stock based on trades in last 5 minutes.
     *
     * @return volume weighted stock price if there trades have been done in the last 5 minutes otherwise null
     */
    public BigDecimal getVolumeWeightedStockPrice() {
        return this.getVolumeWeightedStockPrice(Instant.now().minusMillis(300_000));
    }

    /**
     * Volume weighted stock price for this stock.
     *
     * @param timestamp All trades after this timestamp will be used to calculate value.
     * @return volume weighted stock price if there trades have been done after the specified timestamp otherwise null
     */
    public BigDecimal getVolumeWeightedStockPrice(Instant timestamp) {
        long quantity = 0;
        BigDecimal total = BigDecimal.ZERO;
        lock.readLock().lock();

        try {
            ListIterator<Trade> it = trades.listIterator(trades.size());

            while (it.hasPrevious()) {
                Trade trade = it.previous();
                if (trade.getTimestamp().isBefore(timestamp)) break;
                total = total.add(trade.getTotal());
                quantity += trade.getQuantity();
            }
        } finally {
            lock.readLock().unlock();
        }
        return quantity != 0 ? total.divide(BigDecimal.valueOf(quantity), MathContext.DECIMAL128) : null;
    }

    /**
     * Returns trades of this stock.
     *
     * @return trades of this stock
     */
    public List<Trade> getTrades() {
        // so the underlying collection of trades for this stock is not modified by external code
        List<Trade> trades = new ArrayList<>();
        lock.readLock().lock();
        try {
            trades.addAll(this.trades);

        } finally {
            lock.readLock().unlock();
        }
        return trades;
    }

    /**
     * PE ratio for this stock.
     *
     * @return PE ratio or null if PE ratio cannot be evaluated (e.g. company has not paid dividend yet or there has been no trades yet)
     */
    public BigDecimal getPERatio() {
        Trade trade = this.getLastTrade();
        return getLastDividend() == 0 || trade == null ? null : trade.getPrice().divide(BigDecimal.valueOf(getLastDividend()), MathContext.DECIMAL128);
    }

    /**
     * Returns last dividend for this stock.
     *
     * @return lst dividend
     */
    public int getLastDividend() { return lastDividend; }

    /**
     * Returns par value.
     *
     * @return lst dividend
     */
    public int getParValue() {
        return parValue;
    }

    /**
     * Returns dividend yield for this stock.
     *
     * @return dividend yield
     */
    public abstract BigDecimal getDividendYield();
}
