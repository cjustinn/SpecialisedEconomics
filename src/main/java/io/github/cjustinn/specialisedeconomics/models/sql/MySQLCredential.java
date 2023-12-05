package io.github.cjustinn.specialisedeconomics.models.sql;

import org.bukkit.configuration.ConfigurationSection;

public class MySQLCredential {
    private final String host;
    private final String port;
    private final String database;
    private final String user;
    private final String password;

    public MySQLCredential(ConfigurationSection section) {
        this.host = section.getString("host");
        this.port = section.getString("port");
        this.database = section.getString("database");
        this.user = section.getString("username");
        this.password = section.getString("password");
    }

    public String getConnectionString() {
        return String.format("jdbc:mysql://%s:%s/%s", this.host, this.port, this.database);
    }

    public String getUsername() { return this.user; }
    public String getPassword() { return this.password; }
}
