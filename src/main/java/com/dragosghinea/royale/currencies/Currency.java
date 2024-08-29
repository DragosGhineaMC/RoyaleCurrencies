package com.dragosghinea.royale.currencies;

import com.dragosghinea.royale.currencies.balancetop.CurrencyBalanceTop;
import com.dragosghinea.royale.currencies.suggestions.SuggestedMessageKeys;
import com.dragosghinea.royale.currencies.suggestions.SuggestedPlaceholders;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface Currency {

    Plugin getPlugin();

    default ItemStack getIcon() {
        return new ItemStack(Material.BOOK);
    }

    String getId();

    default CurrencyBalanceTop getBalanceTop() {
        return null;
    }

    int numberOfDecimals();

    String getCurrencyNameSingular();

    String getCurrencyNamePlural();

    String getCurrencyColor();

    /**
     * @return The value of a single unit of this currency as compared to the default currency.<br>
     * <p>
     * - If value is 10, then 1 default currency is equal to 10 of this currency.<br>
     * - If value is 0.1, then 10 default currency is equal to 1 of this currency.
     * </p>
     */
    BigDecimal getUnitValue();

    default Map<String, String> getSuggestedMessages() {
        Map<String, String> toReturn = new HashMap<>();

        toReturn.put(SuggestedMessageKeys.BALANCE.getMessageKey(), String.join("",
                "&fYou have ",
                SuggestedPlaceholders.CURRENCY_COLOR.getPlaceholder(),
                SuggestedPlaceholders.AMOUNT.getPlaceholder(),
                " ",
                SuggestedPlaceholders.CURRENCY_NAME.getPlaceholder(), "&f.")
        );

        toReturn.put(SuggestedMessageKeys.RECEIVE_CURRENCY.getMessageKey(), String.join("",
                "&fYou have received ",
                SuggestedPlaceholders.CURRENCY_COLOR.getPlaceholder(),
                SuggestedPlaceholders.AMOUNT.getPlaceholder(),
                " ",
                SuggestedPlaceholders.CURRENCY_NAME.getPlaceholder(),
                " from ",
                SuggestedPlaceholders.PLAYER_NAME.getPlaceholder(), "&f.")
        );

        toReturn.put(SuggestedMessageKeys.SEND_CURRENCY.getMessageKey(), String.join("",
                "&fYou have sent ",
                SuggestedPlaceholders.CURRENCY_COLOR.getPlaceholder(),
                SuggestedPlaceholders.AMOUNT.getPlaceholder(),
                " ",
                SuggestedPlaceholders.CURRENCY_NAME.getPlaceholder(),
                " to ",
                SuggestedPlaceholders.PLAYER_NAME.getPlaceholder(), "&f.")
        );

        return toReturn;
    }

    default Map<String, String> getSuggestedPermissions() {
        return Collections.emptyMap();
    }

    default Map<String, Object> getExtraData() {
        return Collections.emptyMap();
    }

    String formatMoney(BigDecimal amount);

    boolean addAmount(String identifier, BigDecimal amount);

    boolean removeAmount(String identifier, BigDecimal amount);

    void setAmount(String identifier, BigDecimal amount);

    BigDecimal getAmount(String identifier);
}
