package com.dragosghinea.royale.currencies.vault;

import com.dragosghinea.royale.currencies.Currency;
import com.dragosghinea.royale.currencies.exceptions.NoEconomyFound;
import com.dragosghinea.royale.currencies.exceptions.VaultNotFound;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class VaultCurrency implements Currency {
    private final Economy economy;

    private final String id;
    private final String currencyColor;
    private final ItemStack icon;

    private final String currencyNameSingular;
    private final String currencyNamePlural;
    private final int numberOfDecimals;

    @Setter
    private Function<BigDecimal, String> formatMoneyFunction;

    public VaultCurrency(String id) {
        this(id, ChatColor.GOLD.toString(), null, null, new ItemStack(Material.GOLD_NUGGET));
    }

    public VaultCurrency(String id, String currencyColor) {
        this(id, currencyColor, null, null, new ItemStack(Material.GOLD_NUGGET));
    }

    public VaultCurrency(String id, String currencyColor, ItemStack icon) {
        this(id, currencyColor, null, null, icon);
    }

    public VaultCurrency(String id, String currencyColor, Integer overrideNumberOfDecimals, ItemStack icon) {
        this(id, currencyColor, overrideNumberOfDecimals, null, null, icon);
    }

    public VaultCurrency(String id, String currencyColor, String overrideCurrencyNameSingular, String overrideCurrencyNamePlural) {
        this(id, currencyColor, overrideCurrencyNameSingular, overrideCurrencyNamePlural, new ItemStack(Material.GOLD_NUGGET));
    }

    public VaultCurrency(String id, String currencyColor, Integer overrideNumberOfDecimals, String overrideCurrencyNameSingular, String overrideCurrencyNamePlural) {
        this(id, currencyColor, overrideNumberOfDecimals, overrideCurrencyNameSingular, overrideCurrencyNamePlural, new ItemStack(Material.GOLD_NUGGET));
    }

    public VaultCurrency(String id, String currencyColor, String overrideCurrencyNameSingular, String overrideCurrencyNamePlural, ItemStack icon) {
        this(id, currencyColor, null, overrideCurrencyNameSingular, overrideCurrencyNamePlural, icon);
    }

    public VaultCurrency(String id, String currencyColor, Integer overrideNumberOfDecimals, String overrideCurrencyNameSingular, String overrideCurrencyNamePlural, ItemStack icon) {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            throw new VaultNotFound("Vault plugin not found.");
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new NoEconomyFound("No economy found.");
        }

        economy = rsp.getProvider();

        this.id = id;
        this.currencyColor = currencyColor;
        this.icon = icon;
        this.currencyNameSingular = overrideCurrencyNameSingular == null ? economy.currencyNameSingular() : overrideCurrencyNameSingular;
        this.currencyNamePlural = overrideCurrencyNamePlural == null ? economy.currencyNamePlural() : overrideCurrencyNamePlural;
        this.numberOfDecimals = overrideNumberOfDecimals == null ? getNumberOfDecimalsFromVault() : overrideNumberOfDecimals;

        this.formatMoneyFunction = amount -> economy.format(amount.doubleValue());
    }

    private int getNumberOfDecimalsFromVault() {
        int digits = economy.fractionalDigits();
        if (digits == -1)
            return 6; // -1 is not supported

        return Math.min(digits, 6);
    }

    @Override
    public Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("Vault");
    }

    @Override
    public ItemStack getIcon() {
        return icon;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int numberOfDecimals() {
        return numberOfDecimals;
    }

    @Override
    public String getCurrencyNameSingular() {
        return currencyNameSingular;
    }

    @Override
    public String getCurrencyNamePlural() {
        return currencyNamePlural;
    }

    @Override
    public String getCurrencyColor() {
        return currencyColor;
    }

    @Override
    public BigDecimal getUnitValue() {
        return BigDecimal.ONE;
    }

    @Override
    public Map<String, String> getSuggestedMessages() {
        return Currency.super.getSuggestedMessages();
    }

    @Override
    public Map<String, String> getSuggestedPermissions() {
        return Currency.super.getSuggestedPermissions();
    }

    @Override
    public Map<String, Object> getExtraData() {
        return Currency.super.getExtraData();
    }

    @Override
    public String formatMoney(BigDecimal amount) {
        return formatMoneyFunction.apply(amount);
    }

    @Override
    public boolean addAmount(String identifier, BigDecimal amount) {
        OfflinePlayer offlinePlayer = computeOfflinePlayer(identifier);

        if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore())
            return economy.depositPlayer(identifier, amount.doubleValue()).transactionSuccess();
        else
            return economy.depositPlayer(offlinePlayer, amount.doubleValue()).transactionSuccess();
    }

    @Override
    public boolean removeAmount(String identifier, BigDecimal amount) {
        OfflinePlayer offlinePlayer = computeOfflinePlayer(identifier);

        if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore())
            return economy.withdrawPlayer(identifier, amount.doubleValue()).transactionSuccess();

        return economy.withdrawPlayer(offlinePlayer, amount.doubleValue()).transactionSuccess();
    }

    @Override
    public void setAmount(String identifier, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }

        OfflinePlayer offlinePlayer = computeOfflinePlayer(identifier);

        if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
            double balance = economy.getBalance(identifier);

            if (balance > amount.doubleValue()) {
                economy.withdrawPlayer(identifier, balance - amount.doubleValue());
            } else {
                economy.depositPlayer(identifier, amount.doubleValue() - balance);
            }
        }
        else {
            double balance = economy.getBalance(offlinePlayer);

            if (balance > amount.doubleValue()) {
                economy.withdrawPlayer(offlinePlayer, balance - amount.doubleValue());
            } else {
                economy.depositPlayer(offlinePlayer, amount.doubleValue() - balance);
            }
        }
    }

    @Override
    public BigDecimal getAmount(String identifier) {
        OfflinePlayer offlinePlayer = computeOfflinePlayer(identifier);

        if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore())
            return BigDecimal.valueOf(economy.getBalance(identifier));

        return BigDecimal.valueOf(economy.getBalance(offlinePlayer));
    }

    private OfflinePlayer computeOfflinePlayer(String identifier) {
        try {
            UUID uuid = UUID.fromString(identifier);
            return Bukkit.getOfflinePlayer(uuid);
        } catch (IllegalArgumentException e) {
            return Bukkit.getOfflinePlayer(identifier);
        }
    }
}
