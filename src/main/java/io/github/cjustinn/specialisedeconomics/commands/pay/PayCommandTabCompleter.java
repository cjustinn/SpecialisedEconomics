package io.github.cjustinn.specialisedeconomics.commands.pay;

import io.github.cjustinn.specialisedeconomics.repositories.UserRepository;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PayCommandTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> options = new ArrayList<String>();

        if (args.length == 1) {
            for (final String uuid : UserRepository.users.keySet()) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                if (player != null) {
                    options.add(player.getName());
                }
            }
        }

        return options.stream().filter((option) -> option.startsWith(args[0])).collect(Collectors.toList());
    }
}
