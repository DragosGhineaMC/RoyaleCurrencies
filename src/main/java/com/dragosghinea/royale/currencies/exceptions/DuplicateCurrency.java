package com.dragosghinea.royale.currencies.exceptions;

import com.dragosghinea.royale.currencies.Currency;
import lombok.Getter;

@Getter
public class DuplicateCurrency extends RuntimeException {

    private final Currency alreadyExistingCurrency;

    private final Currency triedToCreateCurrency;

    public DuplicateCurrency(String message, Currency alreadyExistingCurrency, Currency triedToCreateCurrency) {
        super(message);
        this.alreadyExistingCurrency = alreadyExistingCurrency;
        this.triedToCreateCurrency = triedToCreateCurrency;
    }
}
