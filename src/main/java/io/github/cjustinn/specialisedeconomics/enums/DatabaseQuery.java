package io.github.cjustinn.specialisedeconomics.enums;

import io.github.cjustinn.specialisedlib.Database.DatabaseMultiQuery;

public enum DatabaseQuery implements DatabaseMultiQuery {
    // Table Creators
    CreateUsersTable(
            "create_users_table",
            "CREATE TABLE IF NOT EXISTS se_users (uuid VARCHAR(36) NOT NULL PRIMARY KEY, discord_id VARCHAR(18) DEFAULT NULL, balance DOUBLE DEFAULT 0.00, created DATETIME DEFAULT CURRENT_TIMESTAMP);",
            "CREATE TABLE IF NOT EXISTS se_users (uuid VARCHAR(36) NOT NULL PRIMARY KEY, discord_id VARCHAR(18) DEFAULT NULL, balance DOUBLE DEFAULT 0.00, created DATETIME DEFAULT CURRENT_TIMESTAMP);"
    ),
    CreateTransactionsTable(
            "create_user_transactions_table",
            "CREATE TABLE IF NOT EXISTS se_transactions (id INT AUTO_INCREMENT PRIMARY KEY, transaction_owner VARCHAR(36) NOT NULL, sender VARCHAR(350), recipient VARCHAR(350), amount DOUBLE NOT NULL, balance DOUBLE NOT NULL, details VARCHAR(250) DEFAULT \"Unspecified Transaction\", completed DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (transaction_owner) REFERENCES se_users(uuid));",
            "CREATE TABLE IF NOT EXISTS se_transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, transaction_owner VARCHAR(36) NOT NULL, sender VARCHAR(350), recipient VARCHAR(350), amount DOUBLE NOT NULL, balance DOUBLE NOT NULL, details VARCHAR(250) DEFAULT \"Unspecified Transaction\", completed DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (transaction_owner) REFERENCES se_users(uuid));"
    ),
    CreateBanksTable(
            "create_bank_table",
            "CREATE TABLE IF NOT EXISTS se_banks (id INT AUTO_INCREMENT PRIMARY KEY, display_name VARCHAR(350) NOT NULL, owner_uuid VARCHAR(36) NOT NULL, balance DOUBLE DEFAULT 0.00, created DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (owner_uuid) REFERENCES se_users(uuid));",
            "CREATE TABLE IF NOT EXISTS se_banks (id INTEGER PRIMARY KEY AUTOINCREMENT, display_name VARCHAR(350) NOT NULL, owner_uuid VARCHAR(36) NOT NULL, balance DOUBLE DEFAULT 0.00, created DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (owner_uuid) REFERENCES se_users(uuid));"
    ),
    CreateBankTransactionsTable(
            "create_bank_transactions_table",
            "CREATE TABLE IF NOT EXISTS se_bank_transaction (id INT AUTO_INCREMENT PRIMARY KEY, bank INT NOT NULL, transferrer VARCHAR(36) NOT NULL, amount DOUBLE NOT NULL, reason VARCHAR(250) DEFAULT \"Unspecified Transaction\", completed DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (bank) REFERENCES se_banks(id), FOREIGN KEY (transferrer) REFERENCES se_users(uuid));",
            "CREATE TABLE IF NOT EXISTS se_bank_transaction (id INTEGER PRIMARY KEY AUTOINCREMENT, bank INT NOT NULL, transferrer VARCHAR(36) NOT NULL, amount DOUBLE NOT NULL, reason VARCHAR(250) DEFAULT \"Unspecified Transaction\", completed DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (bank) REFERENCES se_banks(id), FOREIGN KEY (transferrer) REFERENCES se_users(uuid));"
    ),
    CreateBankMembersTable(
            "create_bank_member_table",
            "CREATE TABLE IF NOT EXISTS se_bank_members(id INT AUTO_INCREMENT PRIMARY KEY, bank INT NOT NULL, uuid VARCHAR(36) NOT NULL, added DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (bank) REFERENCES se_banks(id), FOREIGN KEY (uuid) REFERENCES se_users(uuid));",
            "CREATE TABLE IF NOT EXISTS se_bank_members(id INTEGER PRIMARY KEY AUTOINCREMENT, bank INT NOT NULL, uuid VARCHAR(36) NOT NULL, added DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (bank) REFERENCES se_banks(id), FOREIGN KEY (uuid) REFERENCES se_users(uuid));"
    ),
    // User Queries
    InsertUser(
            "insert_user",
            "INSERT INTO se_users (uuid, balance) VALUES (?, ?);",
            "INSERT INTO se_users (uuid, balance) VALUES (?, ?);"
    ),
    SelectAllUsers(
            "select_all_users",
            "SELECT uuid, discord_id, balance, created FROM se_users;",
            "SELECT uuid, discord_id, balance, created FROM se_users;"
    ),
    UpdateUser(
            "update_user",
            "UPDATE se_users SET discord_id = ?, balance = ? WHERE uuid = ?;",
            "UPDATE se_users SET discord_id = ?, balance = ? WHERE uuid = ?;"
    ),
    // User Transaction Queries
    InsertUserTransaction(
            "insert_user_transaction",
            "INSERT INTO se_transactions (transaction_owner, sender, recipient, amount, balance, details) VALUES (?, ?, ?, ?, ?, ?);",
            "INSERT INTO se_transactions (transaction_owner, sender, recipient, amount, balance, details) VALUES (?, ?, ?, ?, ?, ?);"
    )
    // Bank Queries
    // Bank Transaction Queries
    // Bank Member Queries
    ;

    private final String id;
    private final String mysql;
    private final String sqlite;

    DatabaseQuery(final String id, final String mysql, final String sqlite) {
        this.id = id;
        this.mysql = mysql;
        this.sqlite = sqlite;
    }

    @Override
    public String getQueryId() {
        return this.id;
    }

    @Override
    public String getMySQL() {
        return this.mysql;
    }

    @Override
    public String getSQLite() {
        return this.sqlite;
    }
}
