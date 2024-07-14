package com.dragosghinea.royale.currencies.suggestions;

import lombok.Getter;

@Getter
public enum SuggestedMessageKeys {

    BALANCE("balance"),
    RECEIVE_CURRENCY("receive_currency"),
    SEND_CURRENCY("send_currency");

    private final String messageKey;

    SuggestedMessageKeys(String messageKey) {
        this.messageKey = messageKey;
    }
}