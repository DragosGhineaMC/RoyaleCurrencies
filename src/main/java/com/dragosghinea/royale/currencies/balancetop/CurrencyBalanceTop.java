package com.dragosghinea.royale.currencies.balancetop;

import java.util.List;

public interface CurrencyBalanceTop {

    List<CurrencyBalanceTopEntry> getTopEntries(int page);

    int getTotalNumberOfPages();

    int getPageSize();
}
