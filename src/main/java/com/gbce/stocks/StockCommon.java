package com.gbce.stocks;

import java.math.BigDecimal;
import java.math.MathContext;

public class StockCommon extends Stock {

    /**
     * {@inheritDoc}
     */
    public StockCommon(String stockSymbol, int parValue, int lastDividend) {
        super(stockSymbol, parValue, lastDividend);
    }

    /**
     * Returns dividend yield for this stock.
     *
     * @return dividend yield for this stock if there have been trades done otherwise null
     */
    @Override
    public BigDecimal getDividendYield() {
        Trade trade = this.getLastTrade();
        return trade != null ? new BigDecimal(this.getLastDividend()).divide(trade.getPrice(), MathContext.DECIMAL128) : null;
    }
}
