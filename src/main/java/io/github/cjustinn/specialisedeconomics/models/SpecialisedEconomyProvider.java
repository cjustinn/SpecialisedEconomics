package io.github.cjustinn.specialisedeconomics.models;

import io.github.cjustinn.specialisedeconomics.repositories.PluginSettingsRepository;
import io.github.cjustinn.specialisedeconomics.repositories.UserRepository;
import io.github.cjustinn.specialisedeconomics.services.UtilService;
import io.github.cjustinn.specialisedlib.Economy.SpecialisedEconomy;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class SpecialisedEconomyProvider implements SpecialisedEconomy {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "SpecialisedEconomics";
    }

    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double v) {
        return UtilService.formatCurrency(v);
    }

    @Override
    public String currencyNamePlural() {
        return PluginSettingsRepository.currencySymbol;
    }

    @Override
    public String currencyNameSingular() {
        return this.currencyNamePlural();
    }

    @Override @Deprecated
    public boolean hasAccount(String s) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return UserRepository.users.containsKey(offlinePlayer.getUniqueId().toString());
    }

    @Override @Deprecated
    public boolean hasAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return this.hasAccount(offlinePlayer);
    }

    @Override @Deprecated
    public double getBalance(String s) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        if (this.hasAccount(offlinePlayer)) {
            return UserRepository.users.get(offlinePlayer.getUniqueId().toString()).getBalance();
        } else return 0.00;
    }

    @Override @Deprecated
    public double getBalance(String s, String s1) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return this.getBalance(offlinePlayer);
    }

    @Override @Deprecated
    public boolean has(String s, double v) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        if (this.hasAccount(offlinePlayer)) {
            return UserRepository.users.get(offlinePlayer.getUniqueId().toString()).canAfford(v);
        } else return false;
    }

    @Override @Deprecated
    public boolean has(String s, String s1, double v) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return this.has(offlinePlayer, v);
    }

    @Override @Deprecated
    public EconomyResponse withdrawPlayer(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        if (this.hasAccount(offlinePlayer)) {
            SpecialisedEconomyUser economyUser = UserRepository.users.get(offlinePlayer.getUniqueId().toString());
            return economyUser.modifyBalance(-v);
        } else return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "You do not have an account registered with SpecialisedEconomics!");
    }

    @Override @Deprecated
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return this.withdrawPlayer(offlinePlayer, v);
    }

    @Override @Deprecated
    public EconomyResponse depositPlayer(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        if (this.hasAccount(offlinePlayer)) {
            SpecialisedEconomyUser economyUser = UserRepository.users.get(offlinePlayer.getUniqueId().toString());
            return economyUser.modifyBalance(v);
        } else return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "You do not have an account registered with SpecialisedEconomics!");
    }

    @Override @Deprecated
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return this.depositPlayer(offlinePlayer, v);
    }

    @Override @Deprecated
    public EconomyResponse createBank(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        // Implement this.
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        // Implement this.
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        // Implement this.
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        // Implement this.
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        // Implement this.
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        // Implement this.
        return null;
    }

    @Override @Deprecated
    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        // Implement this.
        return null;
    }

    @Override @Deprecated
    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        // Implement this.
        return null;
    }

    @Override
    public List<String> getBanks() {
        // Implement this.
        return null;
    }

    @Override @Deprecated
    public boolean createPlayerAccount(String s) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return UserRepository.createUser(offlinePlayer.getUniqueId().toString());
    }

    @Override @Deprecated
    public boolean createPlayerAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return this.createPlayerAccount(offlinePlayer);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v, String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v, String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v, String s1, String s2) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v, String s1, String s2) {
        return null;
    }
}
