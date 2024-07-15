package com.dragosghinea.royale.currencies.vault;

import com.dragosghinea.royale.currencies.Currency;
import com.dragosghinea.royale.currencies.exceptions.NoEconomyFound;
import com.dragosghinea.royale.currencies.exceptions.VaultNotFound;
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

public class VaultCurrency implements Currency {
    private final Economy economy;

    private final String id;
    private final String currencyColor;
    private final ItemStack icon;

    public VaultCurrency(String id) {
        this(id, ChatColor.GOLD.toString(), new ItemStack(Material.GOLD_NUGGET));
    }

    public VaultCurrency(String id, String currencyColor) {
        this(id, currencyColor, new ItemStack(Material.GOLD_NUGGET));
    }

    public VaultCurrency(String id, String currencyColor, ItemStack icon) {
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
        int digits = economy.fractionalDigits();
        if (digits == -1)
            return 6; // -1 is not supported

        return digits;
    }

    @Override
    public String getCurrencyNameSingular() {
        return economy.currencyNameSingular();
    }

    @Override
    public String getCurrencyNamePlural() {
        return economy.currencyNamePlural();
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
        return economy.format(amount.doubleValue());
    }

    @Override
    public void addAmount(String identifier, BigDecimal amount) {
        OfflinePlayer offlinePlayer = computeOfflinePlayer(identifier);

        if (!offlinePlayer.hasPlayedBefore())
            economy.depositPlayer(identifier, amount.doubleValue());
        else
            economy.depositPlayer(offlinePlayer, amount.doubleValue());
    }

    @Override
    public boolean removeAmount(String identifier, BigDecimal amount) {
        OfflinePlayer offlinePlayer = computeOfflinePlayer(identifier);

        if (!offlinePlayer.hasPlayedBefore())
            return economy.withdrawPlayer(identifier, amount.doubleValue()).transactionSuccess();

        return economy.withdrawPlayer(offlinePlayer, amount.doubleValue()).transactionSuccess();
    }

    @Override
    public void setAmount(String identifier, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }

        OfflinePlayer offlinePlayer = computeOfflinePlayer(identifier);

        if (!offlinePlayer.hasPlayedBefore()) {
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

        if (!offlinePlayer.hasPlayedBefore())
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
