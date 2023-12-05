package io.github.cjustinn.specialisedeconomics.commands.balance;

import io.github.cjustinn.specialisedeconomics.repositories.UserRepository;
import io.github.cjustinn.specialisedeconomics.services.EconomyService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BalanceCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(Component.text("The default balance command cannot be run from the console!", NamedTextColor.RED));
            } else {
                Player player = (Player) commandSender;
                Economy economy = EconomyService.getEconomy();

                if (economy.hasAccount(player)) {
                    player.sendMessage(
                            Component.text(
                                    "Your current balance is ",
                                    NamedTextColor.GREEN
                            ).append(
                                    Component.text(
                                            String.format(
                                                    "%s",
                                                    economy.format(economy.getBalance(player))
                                            ),
                                            NamedTextColor.GOLD
                                    )
                            ).append(
                                    Component.text(
                                            ".",
                                            NamedTextColor.GREEN
                                    )
                            )
                    );
                } else {
                    player.sendMessage(Component.text("Your account is not associated with an economy account!", NamedTextColor.RED));
                }
            }
        } else if (args.length == 1) {
            OfflinePlayer ply = Bukkit.getOfflinePlayer(args[0]);
            if (commandSender instanceof Player && !((Player) commandSender).hasPermission("specialisedeconomics.balance.other")) {
                commandSender.sendMessage(Component.text(
                        "You do not have the required permissions to run that command!",
                        NamedTextColor.RED
                ));
            } else if (ply == null || !UserRepository.users.containsKey(ply.getUniqueId().toString())) {
                commandSender.sendMessage(Component.text(
                        "No player was found with that name!",
                        NamedTextColor.RED
                ));
            } else {
                commandSender.sendMessage(Component.text(
                        String.format("%s's current balance is ", ply.getName()),
                        NamedTextColor.GREEN
                ).append(Component.text(
                        String.format("%s", EconomyService.getEconomy().format(EconomyService.getEconomy().getBalance(ply))),
                        NamedTextColor.GOLD
                )).append(Component.text(
                        ".",
                        NamedTextColor.GREEN
                )));
            }
        } else {
            commandSender.sendMessage(Component.text(
                    "You have provided too many command arguments!",
                    NamedTextColor.RED
            ));
        }

        return true;
    }
}
