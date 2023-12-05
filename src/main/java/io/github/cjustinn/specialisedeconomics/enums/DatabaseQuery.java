package io.github.cjustinn.specialisedeconomics.enums;

public enum DatabaseQuery {
    // Table Creators
    CreateUsersTable(
            "CREATE TABLE IF NOT EXISTS se_users (uuid VARCHAR(36) NOT NULL PRIMARY KEY, discord_id VARCHAR(18) DEFAULT NULL, balance DOUBLE DEFAULT 0.00, created DATETIME DEFAULT CURRENT_TIMESTAMP);",
            "CREATE TABLE IF NOT EXISTS se_users (uuid VARCHAR(36) NOT NULL PRIMARY KEY, discord_id VARCHAR(18) DEFAULT NULL, balance DOUBLE DEFAULT 0.00, created DATETIME DEFAULT CURRENT_TIMESTAMP);"
    ),
    CreateTransactionsTable(
            "CREATE TABLE IF NOT EXISTS se_transactions (id INT AUTO_INCREMENT PRIMARY KEY, transaction_owner VARCHAR(36) NOT NULL, sender VARCHAR(350), recipient VARCHAR(350), amount DOUBLE NOT NULL, balance DOUBLE NOT NULL, details VARCHAR(250) DEFAULT \"Unspecified Transaction\", completed DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (transaction_owner) REFERENCES se_users(uuid));",
            "CREATE TABLE IF NOT EXISTS se_transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, transaction_owner VARCHAR(36) NOT NULL, sender VARCHAR(350), recipient VARCHAR(350), amount DOUBLE NOT NULL, balance DOUBLE NOT NULL, details VARCHAR(250) DEFAULT \"Unspecified Transaction\", completed DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (transaction_owner) REFERENCES se_users(uuid));"
    ),
    CreateBanksTable(
            "CREATE TABLE IF NOT EXISTS se_banks (id INT AUTO_INCREMENT PRIMARY KEY, display_name VARCHAR(350) NOT NULL, owner_uuid VARCHAR(36) NOT NULL, balance DOUBLE DEFAULT 0.00, created DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (owner_uuid) REFERENCES se_users(uuid));",
            "CREATE TABLE IF NOT EXISTS se_banks (id INTEGER PRIMARY KEY AUTOINCREMENT, display_name VARCHAR(350) NOT NULL, owner_uuid VARCHAR(36) NOT NULL, balance DOUBLE DEFAULT 0.00, created DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (owner_uuid) REFERENCES se_users(uuid));"
    ),
    CreateBankTransactionsTable(
            "CREATE TABLE IF NOT EXISTS se_bank_transaction (id INT AUTO_INCREMENT PRIMARY KEY, bank INT NOT NULL, transferrer VARCHAR(36) NOT NULL, amount DOUBLE NOT NULL, reason VARCHAR(250) DEFAULT \"Unspecified Transaction\", completed DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (bank) REFERENCES se_banks(id), FOREIGN KEY (transferrer) REFERENCES se_users(uuid));",
            "CREATE TABLE IF NOT EXISTS se_bank_transaction (id INTEGER PRIMARY KEY AUTOINCREMENT, bank INT NOT NULL, transferrer VARCHAR(36) NOT NULL, amount DOUBLE NOT NULL, reason VARCHAR(250) DEFAULT \"Unspecified Transaction\", completed DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (bank) REFERENCES se_banks(id), FOREIGN KEY (transferrer) REFERENCES se_users(uuid));"
    ),
    CreateBankMembersTable(
            "CREATE TABLE IF NOT EXISTS se_bank_members(id INT AUTO_INCREMENT PRIMARY KEY, bank INT NOT NULL, uuid VARCHAR(36) NOT NULL, added DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (bank) REFERENCES se_banks(id), FOREIGN KEY (uuid) REFERENCES se_users(uuid));",
            "CREATE TABLE IF NOT EXISTS se_bank_members(id INTEGER PRIMARY KEY AUTOINCREMENT, bank INT NOT NULL, uuid VARCHAR(36) NOT NULL, added DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (bank) REFERENCES se_banks(id), FOREIGN KEY (uuid) REFERENCES se_users(uuid));"
    ),
    // User Queries
    InsertUser(
            "INSERT INTO se_users (uuid, balance) VALUES (?, ?);",
            "INSERT INTO se_users (uuid, balance) VALUES (?, ?);"
    ),
    SelectAllUsers(
            "SELECT uuid, discord_id, balance, created FROM se_users;",
            "SELECT uuid, discord_id, balance, created FROM se_users;"
    ),
    UpdateUser(
            "UPDATE se_users SET discord_id = ?, balance = ? WHERE uuid = ?;",
            "UPDATE se_users SET discord_id = ?, balance = ? WHERE uuid = ?;"
    )
    // User Transaction Queries
    // Bank Queries
    // Bank Transaction Queries
    // Bank Member Queries
    ;

    public final String mysql;
    public final String sqlite;

    DatabaseQuery(final String mysql, final String sqlite) {
        this.mysql = mysql;
        this.sqlite = sqlite;
    }
}
