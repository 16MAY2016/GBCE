package com.gbce.stocks;

import com.gbce.stocks.Stock;
import com.gbce.stocks.StockCommon;
import com.gbce.stocks.StockPreferred;

import java.math.BigDecimal;

class StockFactory {

    public static Stock getStock(String symbol) {
        switch (symbol) {
            case "TEA":
                return new StockCommon("TEA", 100, 0);
            case "POP":
                return new StockCommon("POP", 100, 8);
            case "ALE":
                return new StockCommon("ALE", 60, 23);
            case "JOE":
                return new StockCommon("JOE", 250, 13);
            case "GIN":
                return new StockPreferred("GIN", 100, 8, new BigDecimal("2"));
            default:
                return null;
        }
    }
}