package io.github.cjustinn.specialisedeconomics.services;

import io.github.cjustinn.specialisedeconomics.enums.DatabaseQuery;
import io.github.cjustinn.specialisedeconomics.models.sql.DatabaseQueryValue;
import io.github.cjustinn.specialisedeconomics.models.sql.MySQLCredential;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.logging.Level;

public class DatabaseService {
    public static boolean enableMySQL = false;

    public static @Nullable MySQLCredential credentials = null;
    public static Connection connection = null;

    public static boolean CreateConnection(String path) {
        try {
            Class.forName("org.sqlite.JDBC");
            if (connection == null) {
                if (enableMySQL) {
                    connection = DriverManager.getConnection(credentials.getConnectionString(), credentials.getUsername(), credentials.getPassword());
                } else {
                    connection = DriverManager.getConnection("jdbc:sqlite:" + path);
                }

                return true;
            }

            return false;
        } catch (ClassNotFoundException | SQLException err) {
            LoggingService.writeLog(Level.SEVERE, String.format("Failed to create the database connection: %s", err.getMessage()));
            return false;
        }
    }

    public static boolean CloseConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;

                return true;
            } catch (SQLException e) {
                return false;
            }
        }

        return false;
    }

    public static @Nullable ResultSet RunQuery(DatabaseQuery query) {
        try {
            PreparedStatement statement = connection.prepareStatement(enableMySQL ? query.mysql : query.sqlite);
            return statement.executeQuery();
        } catch (SQLException e) {
            LoggingService.writeLog(Level.SEVERE, String.format(
                    "Error occurred running query [%s]: %s",
                    query.name(),
                    e.getMessage()
            ));

            return null;
        }
    }

    public static @Nullable ResultSet RunQuery(DatabaseQuery query, DatabaseQueryValue[] values) {
        try {
            PreparedStatement statement = connection.prepareStatement(enableMySQL ? query.mysql : query.sqlite);

            for (DatabaseQueryValue queryValue : values) {
                switch (queryValue.type) {
                    case Integer:
                        statement.setInt(queryValue.position, (int) queryValue.value);
                        break;
                    case Double:
                        statement.setDouble(queryValue.position, (double) queryValue.value);
                        break;
                    case Boolean:
                        statement.setBoolean(queryValue.position, (boolean) queryValue.value);
                        break;
                    case Date:
                        statement.setDate(queryValue.position, (Date) queryValue.value);
                        break;
                    default:
                        statement.setString(queryValue.position, (String) queryValue.value);
                        break;
                }
            }

            return statement.executeQuery();
        } catch (SQLException e) {
            LoggingService.writeLog(Level.SEVERE, String.format(
                    "Error occurred running query [%s]: %s",
                    query.name(),
                    e.getMessage()
            ));

            return null;
        }
    }

    public static boolean RunUpdate(DatabaseQuery query) {
        try {
            PreparedStatement statement = connection.prepareStatement(enableMySQL ? query.mysql : query.sqlite);
            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            LoggingService.writeLog(Level.SEVERE, String.format(
                    "Error occurred running query [%s]: %s",
                    query.name(),
                    e.getMessage()
            ));

            return false;
        }
    }

    public static boolean RunUpdate(DatabaseQuery query, DatabaseQueryValue[] values) {
        try {
            PreparedStatement statement = connection.prepareStatement(enableMySQL ? query.mysql : query.sqlite);

            for (DatabaseQueryValue queryValue : values) {
                switch (queryValue.type) {
                    case Integer:
                        statement.setInt(queryValue.position, (int) queryValue.value);
                        break;
                    case Double:
                        statement.setDouble(queryValue.position, (double) queryValue.value);
                        break;
                    case Boolean:
                        statement.setBoolean(queryValue.position, (boolean) queryValue.value);
                        break;
                    case Date:
                        statement.setDate(queryValue.position, (Date) queryValue.value);
                        break;
                    default:
                        statement.setString(queryValue.position, (String) queryValue.value);
                        break;
                }
            }

            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            LoggingService.writeLog(Level.SEVERE, String.format(
                    "Error occurred running query [%s]: %s",
                    query.name(),
                    e.getMessage()
            ));

            return false;
        }
    }
}
