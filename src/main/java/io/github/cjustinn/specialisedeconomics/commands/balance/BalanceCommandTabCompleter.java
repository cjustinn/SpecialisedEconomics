package io.github.cjustinn.specialisedeconomics.commands.balance;

import io.github.cjustinn.specialisedeconomics.repositories.UserRepository;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BalanceCommandTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> options = new ArrayList<String>();

        if (args.length == 1 && commandSender instanceof Player && ((Player) commandSender).hasPermission("specialisedeconomics.balance.other")) {
            options = new ArrayList<String>() {{
                for (final String key : UserRepository.users.keySet()) {
                    OfflinePlayer ply = Bukkit.getOfflinePlayer(UUID.fromString(key));
                    if (ply != null) {
                        add(ply.getName());
                    }
                }
            }}.stream().filter((option) -> option.startsWith(args[0])).collect(Collectors.toList());
        }

        return options;
    }
}
