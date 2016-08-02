package com.gbce.stocks;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;
import java.util.Set;

public class StockIndex {

    private final String name;
    private final Set<Stock> stocks;

    /**
     * Creates new stock index.
     *
     * @param name index name
     * @param stocks stocks in this index
     */
    public StockIndex(String name, Set<Stock> stocks) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(stocks);
        if (stocks.isEmpty()) throw new IllegalArgumentException("Stocks cannot be empty");
        this.name = name;
        this.stocks = stocks;
    }

    /**
     * Calculates index using the geometric mean of the volume weighted stock price for stocks in this index.
     *
     * @return index value for stocks in this index that has been made trades of, return 0.0 if there has not been any trades for any stock yet
     */
    public double calculate() {
        // needed for a case edge when for example there two stocks in this stock index
        // and each of these two stocks have the volume weighted stock price equal to one
        boolean volumeWeightedStockPriceExists = false;
        BigDecimal indexValue = BigDecimal.ONE;

        for (Stock stock : stocks) {
            // no need to calculate if no trades have been done
            if (stock.getLastTrade() != null) {
                volumeWeightedStockPriceExists = true;
                BigDecimal volumeWeightedStockPrice = stock.getVolumeWeightedStockPrice();
                if (volumeWeightedStockPrice != null) {
                    indexValue = indexValue.multiply(volumeWeightedStockPrice, MathContext.DECIMAL128);
                }
            }
        }
        return !volumeWeightedStockPriceExists ?  0.0 : Math.pow(indexValue.doubleValue(), 1.0/stocks.size());
    }

    /**
     * Return name of this index.
     *
     * @return name of this index
     */
    public String getName() { return name; }
}
