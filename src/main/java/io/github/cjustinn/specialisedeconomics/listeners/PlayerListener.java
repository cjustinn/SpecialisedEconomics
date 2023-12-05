package io.github.cjustinn.specialisedeconomics.listeners;

import io.github.cjustinn.specialisedeconomics.repositories.UserRepository;
import io.github.cjustinn.specialisedlib.Logging.LoggingService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Level;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joiningPlayer = event.getPlayer();

        if (!UserRepository.users.containsKey(joiningPlayer.getUniqueId().toString())) {
            final boolean success = UserRepository.createUser(joiningPlayer.getUniqueId().toString());

            if (success) {
                LoggingService.writeLog(Level.INFO, "Created economy user for id: " + joiningPlayer.getUniqueId().toString());
            } else {
                LoggingService.writeLog(Level.WARNING, "Unable to create economy user for id: " + joiningPlayer.getUniqueId().toString());
            }
        }
    }
}
