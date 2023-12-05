package io.github.cjustinn.specialisedeconomics;

import io.github.cjustinn.specialisedeconomics.commands.balance.BalanceCommandExecutor;
import io.github.cjustinn.specialisedeconomics.commands.balance.BalanceCommandTabCompleter;
import io.github.cjustinn.specialisedeconomics.commands.balancetop.BalanceTopCommandExecutor;
import io.github.cjustinn.specialisedeconomics.commands.balancetop.BalanceTopCommandTabCompleter;
import io.github.cjustinn.specialisedeconomics.commands.pay.PayCommandExecutor;
import io.github.cjustinn.specialisedeconomics.commands.pay.PayCommandTabCompleter;
import io.github.cjustinn.specialisedeconomics.enums.DatabaseQuery;
import io.github.cjustinn.specialisedeconomics.listeners.PlayerListener;
import io.github.cjustinn.specialisedeconomics.models.SpecialisedEconomyProvider;
import io.github.cjustinn.specialisedeconomics.models.SpecialisedEconomyUser;
import io.github.cjustinn.specialisedeconomics.models.sql.MySQLCredential;
import io.github.cjustinn.specialisedeconomics.repositories.PluginSettingsRepository;
import io.github.cjustinn.specialisedeconomics.repositories.UserRepository;
import io.github.cjustinn.specialisedeconomics.services.ApiService;
import io.github.cjustinn.specialisedeconomics.services.DatabaseService;
import io.github.cjustinn.specialisedeconomics.services.EconomyService;
import io.github.cjustinn.specialisedeconomics.services.LoggingService;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import spark.Spark;

import javax.xml.crypto.Data;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.logging.Level;

import static spark.Spark.*;

public final class SpecialisedEconomics extends JavaPlugin {
    public static Plugin plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        if (this.initialisePlugin()) {
            LoggingService.writeLog(Level.INFO, "Completed startup.");
        } else {
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private boolean initialisePlugin() {
        return registerEconomy() && loadConfiguration() && initialiseDatabase() && registerEndpoints() && registerListeners();
    }

    private boolean registerEconomy() {
        Plugin vault = getServer().getPluginManager().getPlugin("Vault");
        if (vault != null) {
            Bukkit.getServicesManager().register(Economy.class, new SpecialisedEconomyProvider(), vault, ServicePriority.Normal);
            EconomyService.setEconomy(Bukkit.getServicesManager().getRegistration(Economy.class).getProvider());

            return true;
        } else return false;
    }

    private boolean loadConfiguration() {
        LoggingService.writeLog(Level.INFO, "Loading economy configuration...");

        saveDefaultConfig();
        FileConfiguration config = getConfig();

        // Get API settings.
        PluginSettingsRepository.enableApi = config.getBoolean("api.general.enabled", true);
        PluginSettingsRepository.apiPort = config.getInt("api.general.port", 0000);
        PluginSettingsRepository.requireLinkToken = config.getBoolean("api.general.requireLinkToken", true);

        PluginSettingsRepository.enableApiBalanceQueries = config.getBoolean("api.endpoints.allowBalanceQueries", true);
        PluginSettingsRepository.enableApiModifyQueries = config.getBoolean("api.endpoints.allowBalanceModificationQueries", false);

        // Get limits settings.
        PluginSettingsRepository.maximumBalance = config.getDouble("limits.maxBalance", 100000000000.0);
        PluginSettingsRepository.minimumBalance = config.getDouble("limits.minBalance", -10000.0);
        PluginSettingsRepository.minimumPaymentAmount = config.getDouble("limits.minPaymentAmount", 0.01);

        // Get general settings.
        PluginSettingsRepository.allowLoans = config.getBoolean("general.allowLoans", true);
        PluginSettingsRepository.currencySymbolIsPrefix = config.getBoolean("general.currencySymbolIsPrefix", true);
        PluginSettingsRepository.currencySymbol = config.getString("general.currencySymbol", "$");
        PluginSettingsRepository.currencyFormat = config.getString("general.currencyFormat", "#,###.00");
        PluginSettingsRepository.startingBalance = config.getDouble("general.startingBalance", 0.0);
        PluginSettingsRepository.baltopUsersPerPage = config.getInt("general.baltopUsersPerPage", 5);

        // Set MySQL settings
        DatabaseService.enableMySQL = config.getBoolean("mysql.enabled", false);
        DatabaseService.credentials = new MySQLCredential(config.getConfigurationSection("mysql"));

        return true;
    }

    private boolean registerEndpoints() {
        LoggingService.writeLog(Level.INFO, "Registering enabled API endpoints...");

        if (PluginSettingsRepository.enableApi) {
            Thread sparkThread = new Thread(() -> {
                port(PluginSettingsRepository.apiPort);

                // Basic economy info fetch endpoint.
                get("/api/economy", (request, response) -> {
                    response.status(200);
                    response.type("application/json");

                    return ApiService.createObject(
                            new HashMap<String, Object>() {{
                                put("Currency", ApiService.createObject(
                                        new HashMap<String, Object>() {{
                                            put("Symbol", PluginSettingsRepository.currencySymbol);
                                            put("Format", PluginSettingsRepository.currencyFormat);
                                            put("IsPrefix", PluginSettingsRepository.currencySymbolIsPrefix);
                                        }}
                                ));
                                put("AllowsBalanceQuerying", PluginSettingsRepository.enableApiBalanceQueries);
                                put("AllowsBalanceModifying", PluginSettingsRepository.enableApiModifyQueries);
                            }}
                    );
                });

                if (PluginSettingsRepository.enableApiBalanceQueries) {
                    get("/api/balance", (request, response) -> {
                        response.type("application/json");

                        final String requestedId = request.queryParamOrDefault("id", null);
                        final String requestedType = request.queryParamOrDefault("type", null);

                        if (requestedType == "all") {
                            response.status(200);
                            return ApiService.createObject(new HashMap<String, Object>());
                        } else {
                            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(requestedId));
                            if (!EconomyService.getEconomy().hasAccount(player)) {
                                response.status(404);
                                return ApiService.createObject(new HashMap<String, Object>() {{
                                    put("error", "The provided user currently has no economy data.");
                                }});
                            } else {
                                response.status(200);

                                return ApiService.createObject(new HashMap<String, Object>() {{
                                    put("uuid", requestedId);
                                    put("balance", EconomyService.getEconomy().getBalance(player));
                                }});
                            }
                        }
                    });
                }

                if (PluginSettingsRepository.enableApiModifyQueries) {}

                Spark.awaitInitialization();
            });

            sparkThread.start();
        }

        return true;
    }
    private boolean initialiseDatabase() {
        LoggingService.writeLog(Level.INFO, "Loading database connection and tables...");

        File dataFolder = null;

        if (!DatabaseService.enableMySQL) {
            File pluginFolder = getDataFolder();
            dataFolder = new File(pluginFolder, "data");

            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }
        }

        final boolean connectionCreated = DatabaseService.CreateConnection(DatabaseService.enableMySQL ? null : new File(dataFolder, "database.db").getPath());
        LoggingService.writeLog(Level.INFO, "Created database connection.");
        boolean initialisedTables = true;

        if (connectionCreated) {
            DatabaseQuery[] installOrder = new DatabaseQuery[] {
                    DatabaseQuery.CreateUsersTable,
                    DatabaseQuery.CreateTransactionsTable,
//                    DatabaseQuery.CreateBanksTable,
//                    DatabaseQuery.CreateBankTransactionsTable,
//                    DatabaseQuery.CreateBankMembersTable
            };

            for (DatabaseQuery query : installOrder) {
                if (initialisedTables && ((DatabaseService.enableMySQL ? query.mysql : query.sqlite).length() > 0)) {
                    initialisedTables = initialisedTables && DatabaseService.RunUpdate(query);

                    if (!initialisedTables) {
                        LoggingService.writeLog(Level.WARNING, String.format("Error completing %s table creation query.", query.name()));
                    }
                }

                LoggingService.writeLog(Level.INFO, "Ran query: " + query.name());
            }

            if (initialisedTables) {
                // Log the completion of this phase of startup.
                LoggingService.writeLog(Level.INFO, "Initialised database tables and procedures.");
            } else {
                // Log the failed completion of this phase of startup.
                LoggingService.writeLog(Level.SEVERE, "Could not create required database tables.");
            }
        } else {
            LoggingService.writeLog(Level.SEVERE, "Could not create connection to the database!");
        }

        return connectionCreated && initialisedTables && this.loadDatabaseData(initialisedTables);
    }

    private boolean loadDatabaseData(final boolean initialisedTables) {
        LoggingService.writeLog(Level.INFO, "Loading economy data...");

        if (initialisedTables) {
            boolean loadedUsers = true;

            ResultSet userResults = DatabaseService.RunQuery(DatabaseQuery.SelectAllUsers);
            if (userResults != null) {
                try {
                    while (userResults.next()) {
                        UserRepository.users.put(userResults.getString(1), new SpecialisedEconomyUser(
                                userResults.getString(1),
                                userResults.getString(2),
                                userResults.getDouble(3),
                                userResults.getDate(4)
                        ));
                    }

                    loadedUsers = true;
                    LoggingService.writeLog(Level.INFO, String.format("Loaded %s economy users.", UserRepository.users.size()));
                } catch (SQLException e) {
                    loadedUsers = false;
                    LoggingService.writeLog(Level.SEVERE, "Failed to load users from the plugin's database.");
                }
            }

            return loadedUsers;
        } else return false;
    }

    private boolean registerListeners() {
        LoggingService.writeLog(Level.INFO, "Registering event handlers...");

        // Commands
        getCommand("balance").setExecutor(new BalanceCommandExecutor());
        getCommand("balance").setTabCompleter(new BalanceCommandTabCompleter());

        getCommand("pay").setExecutor(new PayCommandExecutor());
        getCommand("pay").setTabCompleter(new PayCommandTabCompleter());

        getCommand("balancetop").setExecutor(new BalanceTopCommandExecutor());
        getCommand("balancetop").setTabCompleter(new BalanceTopCommandTabCompleter());

        // Event Handlers / Listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        return true;
    }
}
