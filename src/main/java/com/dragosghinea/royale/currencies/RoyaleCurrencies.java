package com.dragosghinea.royale.currencies;

import com.dragosghinea.royale.currencies.exceptions.DuplicateCurrency;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoyaleCurrencies {

    private static volatile RoyaleCurrencies instance;

    private final Map<String, Currency> currencies = new ConcurrentHashMap<>();

    private RoyaleCurrencies() {
    }

    public static RoyaleCurrencies getInstance() {
        if (instance == null) {
            synchronized (RoyaleCurrencies.class) {
                if (instance == null) {
                    instance = new RoyaleCurrencies();
                }
            }
        }
        return instance;
    }

    public synchronized void registerCurrency(Currency currency) {
        String pluginName = currency.getPlugin().getName().toLowerCase();
        currencies.put(currency.getId(), currency);
        Currency old = currencies.put(pluginName + ":" + currency.getId(), currency); // in case of duplicate ids but different plugins
        if (old != null) {
            throw new DuplicateCurrency("Duplicate currency id: " + currency.getId(), old, currency);
        }
    }

    public synchronized boolean unregisterCurrency(Currency currency) {
        String pluginName = currency.getPlugin().getName().toLowerCase();

        // only remove if the currency is the same instance
        currencies.computeIfPresent(currency.getId(), (currencyId, existingCurrency) -> {
            if (existingCurrency == currency) {
                return null;
            }

            return existingCurrency;
        });

        return currencies.remove(pluginName + ":" + currency.getId()) != null;
    }

    public Currency getCurrency(String id) {
        return currencies.get(id);
    }

}
