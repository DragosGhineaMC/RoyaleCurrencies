package com.dragosghinea.royale.currencies;

import com.dragosghinea.royale.currencies.suggestions.SuggestedPermissionKeys;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public interface ExchangeableCurrency extends Currency {
    @Override
    default Map<String, String> getSuggestedPermissions() {
        Map<String, String> toReturn = new HashMap<>();
        toReturn.put(SuggestedPermissionKeys.BYPASS_EXCHANGE.getPermissionKey(), "currencies."+getId() + ".bypass.exchange");
        return toReturn;
    }

    default BigDecimal getMinimumExchangeBuyAmount() {
        return BigDecimal.ZERO;
    }

    default BigDecimal getMinimumExchangeSellAmount() {
        return BigDecimal.ZERO;
    }

    BigDecimal getExchangePercentOnBuy();

    BigDecimal getExchangePercentOnSell();

    default BigDecimal calculateBuyFee(BigDecimal coins) {
        BigDecimal amountFromPercentage = coins.multiply(getExchangePercentOnBuy())
                .divide(BigDecimal.valueOf(100), numberOfDecimals(), RoundingMode.HALF_UP);

        if (amountFromPercentage.compareTo(getMinimumExchangeBuyAmount()) < 0) {
            return getMinimumExchangeBuyAmount();
        }

        return amountFromPercentage;
    }

    default BigDecimal calculateSellFee(BigDecimal coins) {
        BigDecimal amountFromPercentage = coins.multiply(getExchangePercentOnSell())
                .divide(BigDecimal.valueOf(100), numberOfDecimals(), RoundingMode.HALF_UP);

        if (amountFromPercentage.compareTo(getMinimumExchangeSellAmount()) < 0) {
            return getMinimumExchangeSellAmount();
        }

        return amountFromPercentage;
    }

}
