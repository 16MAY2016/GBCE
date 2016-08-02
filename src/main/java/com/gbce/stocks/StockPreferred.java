package com.gbce.stocks;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

public class StockPreferred extends Stock {

    private static final BigDecimal PERCENT = BigDecimal.valueOf(100);

    private final BigDecimal fixedDividend;

    /**
     * Creates preferred stock. This constructor takes additional parameter.
     *
     * @param fixedDividend fixed dividend
     * @see Stock#Stock(String, int, int)
     */
    public StockPreferred(String stockSymbol, int parValue, int lastDividend, BigDecimal fixedDividend) {
        super(stockSymbol, parValue, lastDividend);
        Objects.requireNonNull(fixedDividend);
        this.fixedDividend = fixedDividend;
    }

    /**
     * Returns dividend yield for this stock.
     *
     * @return dividend yield for this stock if there have been trades done otherwise null
     */
    @Override
    public BigDecimal getDividendYield() {
        Trade lastTrade = this.getLastTrade();
        return lastTrade == null ? null : this.getFixedDividend().divide(PERCENT, MathContext.DECIMAL128)
                .multiply(new BigDecimal(this.getParValue()), MathContext.DECIMAL128)
                .divide(lastTrade.getPrice(), MathContext.DECIMAL128);
    }

    /**
     * Returns fixed dividend for this stock.
     *
     * @return fixed dividend
     */
    public BigDecimal getFixedDividend() {
        return fixedDividend;
    }
}
