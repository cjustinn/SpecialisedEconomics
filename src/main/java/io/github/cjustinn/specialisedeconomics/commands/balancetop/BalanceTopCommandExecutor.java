package io.github.cjustinn.specialisedeconomics.commands.balancetop;

import io.github.cjustinn.specialisedeconomics.models.SpecialisedEconomyUser;
import io.github.cjustinn.specialisedeconomics.repositories.UserRepository;
import io.github.cjustinn.specialisedeconomics.services.EconomyService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class BalanceTopCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1 && !this.isValidNumber(args[0])) {
            commandSender.sendMessage(
                    Component.text(
                            String.format("The provided page number is invalid."),
                            NamedTextColor.RED
                    )
            );
        } else if (args.length == 0 || (args.length == 1 && this.isValidNumber(args[0]))) {
            final int page = args.length == 0 ? 1 : Integer.parseInt(args[0]);
            List<SpecialisedEconomyUser> pageUsers = UserRepository.getBaltopUsersForPage(page);

            if (pageUsers != null) {
                final String header = "SpecialisedEconomics Leaderboard";

                commandSender.sendMessage(Component.text("-".repeat(header.length()), NamedTextColor.DARK_GREEN));
                commandSender.sendMessage(Component.text(header, NamedTextColor.GREEN));
                commandSender.sendMessage(Component.text("-".repeat(header.length()), NamedTextColor.DARK_GREEN));

                for (final SpecialisedEconomyUser user : pageUsers) {
                    OfflinePlayer ply = Bukkit.getOfflinePlayer(UUID.fromString(user.uuid));
                    commandSender.sendMessage(
                            Component.text().append(
                                    Component.text(
                                            String.format("%s", commandSender instanceof Player && ((Player) commandSender).getUniqueId().toString().equals(user.uuid) ? "> " : ""),
                                            NamedTextColor.GOLD
                                    ).decorate(TextDecoration.BOLD)
                            ).append(Component.text(
                                    String.format("%s - ", ply.getName()),
                                    NamedTextColor.GREEN
                            )).append(Component.text(
                                    String.format("%s", EconomyService.getEconomy().format(EconomyService.getEconomy().getBalance(ply))),
                                    NamedTextColor.GOLD
                            ))
                    );
                }

                commandSender.sendMessage(Component.text("-".repeat(header.length()), NamedTextColor.DARK_GREEN));
                commandSender.sendMessage(Component.text(String.format("Page %d / %d", page, (int) Math.ceil((double) UserRepository.users.size() / UserRepository.getBaltopUserLimit())), NamedTextColor.GREEN));
                commandSender.sendMessage(Component.text("-".repeat(header.length()), NamedTextColor.DARK_GREEN));
            } else {
                commandSender.sendMessage(
                        Component.text(
                                "Unable to fetch the requested balance data.",
                                NamedTextColor.RED
                        )
                );
            }
        }
        return true;
    }

    private boolean isValidNumber(String v) {
        try {
            final int page = Integer.parseInt(v);

            return page >= 1 && page <= (int) Math.ceil(UserRepository.users.size() / UserRepository.getBaltopUserLimit());
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
