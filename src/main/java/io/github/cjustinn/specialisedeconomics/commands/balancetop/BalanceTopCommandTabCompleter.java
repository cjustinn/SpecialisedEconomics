package io.github.cjustinn.specialisedeconomics.commands.balancetop;

import io.github.cjustinn.specialisedeconomics.repositories.UserRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BalanceTopCommandTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> options = new ArrayList<String>();

        if (args.length == 1) {
            for (int i = 1; i <= (int) Math.ceil(UserRepository.users.size() / UserRepository.getBaltopUserLimit()); i++) {
                options.add(String.format("%d", i));
            }
        }

        return options.stream().filter((option) -> option.startsWith(args[0])).collect(Collectors.toList());
    }
}
