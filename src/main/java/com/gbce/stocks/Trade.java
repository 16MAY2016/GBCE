package com.gbce.stocks;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public final class Trade {

    public enum TradeIndicator { BUY, SELL }

    private final long quantity;

    private final BigDecimal price;

    private final BigDecimal total;

    private final Instant timestamp;

    private final TradeIndicator indicator;

    public Trade(long quantity, BigDecimal price, Instant timestamp, TradeIndicator indicator) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity cannot be less or equal to 0.");
        Objects.requireNonNull(price);
        if (BigDecimal.ZERO.equals(price) || price.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Price cannot be less or equal to 0.");
        Objects.requireNonNull(timestamp);
        Objects.requireNonNull(indicator);
        this.quantity = quantity;
        this.price = price;
        this.total = price.multiply(BigDecimal.valueOf(quantity));
        this.timestamp = timestamp;
        this.indicator = indicator;
    }

    public long getQuantity() {
        return quantity;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public BigDecimal getTotal() { return total; }

    public BigDecimal getPrice() { return price; }

    public TradeIndicator getIndicator() { return indicator; }
}
