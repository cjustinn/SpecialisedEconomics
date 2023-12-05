package io.github.cjustinn.specialisedeconomics.models;

import io.github.cjustinn.specialisedeconomics.enums.DatabaseQuery;
import io.github.cjustinn.specialisedeconomics.repositories.PluginSettingsRepository;
import io.github.cjustinn.specialisedlib.Database.DatabaseService;
import io.github.cjustinn.specialisedlib.Database.DatabaseValue;
import io.github.cjustinn.specialisedlib.Database.DatabaseValueType;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.UUID;

public class SpecialisedEconomyUser {
    public final String uuid;
    public final String discordId;
    public final Date created;
    private double balance = 0.00;

    public SpecialisedEconomyUser(final String user, final @Nullable String discord, final double balance, final Date createdDate) {
        this.uuid = user;
        this.discordId = discord;
        this.balance = balance;
        this.created = createdDate;
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(UUID.fromString(this.uuid));
    }

    public Player getPlayer() {
        return this.getOfflinePlayer().getPlayer();
    }

    public double getBalance() { return this.balance; }
    public EconomyResponse setBalance(double amount) {
        if (!PluginSettingsRepository.allowLoans && amount < 0) amount = 0;
        else if (amount < PluginSettingsRepository.minimumBalance) amount = PluginSettingsRepository.minimumBalance;
        else if (amount > PluginSettingsRepository.maximumBalance) amount = PluginSettingsRepository.maximumBalance;

        final double originalBalance = this.balance;

        this.balance = amount;
        final boolean successfulSave = this.save();

        if (!successfulSave) this.balance = originalBalance;
        return new EconomyResponse(
                successfulSave ? amount : 0,
                this.balance,
                successfulSave ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE,
                "Failed to save your updated balance."
        );
    }

    public EconomyResponse modifyBalance(double amount) {
        return this.setBalance(this.balance + amount);
    }

    public boolean hasBalance(double target) {
        return this.balance >= target;
    }

    public boolean canAfford(double target) {
        if (this.hasBalance(target))
            return true;
        else if (!this.hasBalance(target) && PluginSettingsRepository.allowLoans && (target - this.balance) >= PluginSettingsRepository.minimumBalance)
            return true;
        else
            return false;
    }

    public boolean save() {
        return DatabaseService.RunUpdate(DatabaseQuery.UpdateUser, new DatabaseValue[] {
                new DatabaseValue(1, this.discordId, DatabaseValueType.String),
                new DatabaseValue(2, this.balance, DatabaseValueType.Double),
                new DatabaseValue(3, this.uuid, DatabaseValueType.String)
        });
    }
}
