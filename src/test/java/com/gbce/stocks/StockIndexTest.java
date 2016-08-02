package com.gbce.stocks;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

public class StockIndexTest {

    private StockIndex stockIndex;
    private Stock teaCommonStock;
    private Stock popCommonStock;
    private Stock aleCommonStock;
    private Stock joeCommonStock;
    private Stock ginPreferredStock;
    private Set<Stock> stocks = new HashSet<>();

    @Before
    public void init(){
        teaCommonStock = StockFactory.getStock("TEA");
        popCommonStock = StockFactory.getStock("POP");
        aleCommonStock = StockFactory.getStock("TEA");
        joeCommonStock = StockFactory.getStock("POP");
        ginPreferredStock = StockFactory.getStock("GIN");
        stocks.add(teaCommonStock);
        stocks.add(popCommonStock);
        stocks.add(aleCommonStock);
        stocks.add(joeCommonStock);
        stocks.add(ginPreferredStock);
        stockIndex = new StockIndex("GBCE Index", stocks);
    }

    @Test
    public void whenThereHaveNotBeenAnyTradesDoneYetThenThenStockIndexIsCalculatedToZero() {
        assertEquals(0.0, stockIndex.calculate());
    }

    @Test
    public void whenThereHaveBeenTradesDoneThenStockIndexIsCalculatedCorrectly() {
        teaCommonStock.sell(1, BigDecimal.valueOf(1));
        popCommonStock.sell(1, BigDecimal.valueOf(3));
        aleCommonStock.sell(1, BigDecimal.valueOf(5));
        joeCommonStock.sell(1, BigDecimal.valueOf(7));
        ginPreferredStock.sell(1, BigDecimal.valueOf(9));
        assertEquals(new BigDecimal("3.94"), BigDecimal.valueOf(stockIndex.calculate()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
    }
}
