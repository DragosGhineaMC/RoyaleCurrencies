package com.dragosghinea.royale.currencies.balancetop;

import java.math.BigDecimal;

public interface CurrencyBalanceTopEntry {

    String getIdentifier();

    String getDisplayName();

    BigDecimal getBalance();

    String getFormattedBalance();

    int getRank();
}
