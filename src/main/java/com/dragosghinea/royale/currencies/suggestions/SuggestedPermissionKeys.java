package com.dragosghinea.royale.currencies.suggestions;

import lombok.Getter;

@Getter
public enum SuggestedPermissionKeys {

    EXCHANGE("exchange"),
    OWN("own"),
    SEND("send"),
    BUY("buy"),
    SELL("sell"),
    BYPASS_EXCHANGE("bypass");

    private final String permissionKey;

    SuggestedPermissionKeys(String permissionKey) {
        this.permissionKey = permissionKey;
    }
}
