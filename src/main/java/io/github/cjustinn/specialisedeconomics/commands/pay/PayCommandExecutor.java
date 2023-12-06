package io.github.cjustinn.specialisedeconomics.commands.pay;

import io.github.cjustinn.specialisedeconomics.repositories.PluginSettingsRepository;
import io.github.cjustinn.specialisedeconomics.services.EconomyService;
import io.github.cjustinn.specialisedlib.Logging.LoggingService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class PayCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (commandSender instanceof Player) {
            if (args.length < 2) {
                commandSender.sendMessage(
                        Component.text(
                                "You have provided too few arguments for that command.",
                                NamedTextColor.RED
                        )
                );
            } else if (args.length > 2) {
                commandSender.sendMessage(
                        Component.text(
                                "You have provided too many arguments for that command.",
                                NamedTextColor.RED
                        )
                );
            } else {
                Player player = (Player) commandSender;
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

                if (player.getUniqueId().equals(target.getUniqueId())) {
                    commandSender.sendMessage(
                            Component.text(
                                    "You cannot send a payment to yourself.",
                                    NamedTextColor.RED
                            )
                    );
                } else if (EconomyService.getEconomy().hasAccount(target)) {
                    try {
                        final double paymentAmount = Double.parseDouble(args[1]);
                        if (paymentAmount < PluginSettingsRepository.minimumPaymentAmount) {
                            commandSender.sendMessage(
                                    Component.text(
                                            String.format("Payments must be at least %s.", EconomyService.getEconomy().format(PluginSettingsRepository.minimumPaymentAmount)),
                                            NamedTextColor.RED
                                    )
                            );
                        } else if (paymentAmount <= 0) {
                            commandSender.sendMessage(
                                    Component.text(
                                            String.format("Payments must be above %s.", EconomyService.getEconomy().format(0.0)),
                                            NamedTextColor.RED
                                    )
                            );
                        } else {
                            // Remove the funds from the commandSender player.
                            if (EconomyService.getEconomy().withdrawPlayer(player, paymentAmount, target.getUniqueId().toString(), String.format("You sent an in-game payment to %s.", target.getName())).transactionSuccess()) {
                                // Add the funds to the target.
                                if (EconomyService.getEconomy().depositPlayer(target, paymentAmount, player.getUniqueId().toString(), String.format("You were sent an in-game payment by %s.", player.getName())).transactionSuccess()) {
                                    // Alert the user that their payment has been made.
                                    player.sendMessage(
                                            Component.text(
                                                    "You have paid ",
                                                    NamedTextColor.GREEN
                                            ).append(Component.text(
                                                    String.format("%s", EconomyService.getEconomy().format(paymentAmount)),
                                                    NamedTextColor.GOLD
                                            )).append(Component.text(
                                                    String.format(" to %s.", target.getName()),
                                                    NamedTextColor.GREEN
                                            ))
                                    );

                                    // If the target player is online, let them know that they've received a payment.
                                    if (target.isOnline()) {
                                        target.getPlayer().sendMessage(
                                                Component.text(
                                                        String.format("%s has paid you ", player.getName()),
                                                        NamedTextColor.GREEN
                                                ).append(Component.text(
                                                        String.format("%s", EconomyService.getEconomy().format(paymentAmount)),
                                                        NamedTextColor.GOLD
                                                )).append(Component.text(
                                                        ".",
                                                        NamedTextColor.GREEN
                                                ))
                                        );
                                    }
                                } else {
                                    // Target deposit failure.
                                    commandSender.sendMessage(
                                            Component.text(
                                                    "Failed to deposit funds into recipient's account. Please try again.",
                                                    NamedTextColor.RED
                                            )
                                    );

                                    if (!EconomyService.getEconomy().depositPlayer(player, paymentAmount, player.getUniqueId().toString(), String.format("Your payment to %s failed and you have been refunded.", target.getName())).transactionSuccess()) {
                                        LoggingService.writeLog(Level.SEVERE, String.format("Failed to refund %s to %s on failed target deposit.", EconomyService.getEconomy().format(paymentAmount), player.getName()));
                                    }
                                }
                            } else {
                                // Invoker withdraw failure.
                                commandSender.sendMessage(
                                        Component.text(
                                                "Failed to remove the funds from your account. Please try again.",
                                                NamedTextColor.RED
                                        )
                                );
                            }
                        }
                    } catch (NumberFormatException e) {
                        commandSender.sendMessage(
                                Component.text(
                                        "The payment amount you provided is invalid.",
                                        NamedTextColor.RED
                                )
                        );
                    }
                } else {
                    commandSender.sendMessage(
                            Component.text(
                                    "The provided player does not have any economy data.",
                                    NamedTextColor.RED
                            )
                    );
                }
            }
        } else {
            commandSender.sendMessage(Component.text("This command cannot be run from the console!", NamedTextColor.RED));
        }

        return true;
    }
}
