package com.dragosghinea.royale.currencies.suggestions;

import lombok.Getter;

@Getter
public enum SuggestedPlaceholders {

    PLAYER_NAME("%player_name%"),
    PLAYER_DISPLAY_NAME("%player_display_name%"),
    CURRENCY_NAME("%currency_name%"), // if amount exists, should dynamically change to singular or plural
    CURRENCY_NAME_SINGULAR("%currency_name_singular%"),
    CURRENCY_NAME_PLURAL("%currency_name_plural%"),
    CURRENCY_COLOR("%currency_color%"),
    CURRENCY_ID("%currency_id%"),
    AMOUNT("%amount_raw%"), // amount used in a transaction
    AMOUNT_FORMATTED("%amount%"), // formatted amount used in a transaction
    CURRENT_BALANCE("%balance_raw%"), // typically, the balance after the transaction
    CURRENT_BALANCE_FORMATTED("%balance%"); // formatted balance after the transaction

    private final String placeholder;

    SuggestedPlaceholders(String placeholder) {
        this.placeholder = placeholder;
    }

}
