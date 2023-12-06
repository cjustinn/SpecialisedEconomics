package io.github.cjustinn.specialisedeconomics.models;

import io.github.cjustinn.specialisedeconomics.enums.DatabaseQuery;
import io.github.cjustinn.specialisedeconomics.repositories.PluginSettingsRepository;
import io.github.cjustinn.specialisedeconomics.services.EconomyService;
import io.github.cjustinn.specialisedlib.Database.DatabaseService;
import io.github.cjustinn.specialisedlib.Database.DatabaseValue;
import io.github.cjustinn.specialisedlib.Database.DatabaseValueType;
import io.github.cjustinn.specialisedlib.Logging.LoggingService;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;

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
    public EconomyResponse setBalance(double amount, String target, String details) {
        if (!PluginSettingsRepository.allowLoans && amount < 0) amount = 0;
        else if (amount < PluginSettingsRepository.minimumBalance) amount = PluginSettingsRepository.minimumBalance;
        else if (amount > PluginSettingsRepository.maximumBalance) amount = PluginSettingsRepository.maximumBalance;

        final double originalBalance = this.balance;

        this.balance = amount;

        final double difference = this.balance - originalBalance;
        final boolean successfulSave = this.save();

        if (!successfulSave) this.balance = originalBalance;
        else {
            // Create the transaction log. If the difference is positive, it's a deposit; set sender value. If it's negative, it's a withdrawal; set recipient.
            final boolean isDeposit = difference > 0.0;

            if (!DatabaseService.RunUpdate(DatabaseQuery.InsertUserTransaction, new DatabaseValue[] {
                    new DatabaseValue(1, this.uuid, DatabaseValueType.String),
                    new DatabaseValue(2, isDeposit ? target : this.uuid, DatabaseValueType.String),
                    new DatabaseValue(3, isDeposit ? this.uuid : target, DatabaseValueType.String),
                    new DatabaseValue(4, difference, DatabaseValueType.Double),
                    new DatabaseValue(5, this.balance, DatabaseValueType.Double),
                    new DatabaseValue(6, details, DatabaseValueType.String)
            })) {
                LoggingService.writeLog(Level.SEVERE, String.format("Failed to log transaction for user: %s (%s -> %s)", this.uuid, EconomyService.getEconomy().format(originalBalance), EconomyService.getEconomy().format(this.balance)));
            }
        }

        return new EconomyResponse(
                successfulSave ? difference : 0,
                this.balance,
                successfulSave ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE,
                successfulSave ? null : "Failed to save your updated balance."
        );
    }

    public EconomyResponse setBalance(double amount) {
        return this.setBalance(amount, "Unknown", "The cause of this transaction is unknown. The plugin which triggered this transaction does not integrate with SpecialisedEconomies.");
    }

    public EconomyResponse modifyBalance(double amount, String target, String details) {
        return this.setBalance(this.balance + amount, target, details);
    }

    public EconomyResponse modifyBalance(double amount) {
        return this.modifyBalance(this.balance + amount, "Unknown", "The cause of this transaction is unknown. The plugin which triggered this transaction does not integrate with SpecialisedEconomies.");
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
