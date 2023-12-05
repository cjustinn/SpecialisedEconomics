package io.github.cjustinn.specialisedeconomics.repositories;

public class PluginSettingsRepository {
    // API Settings
    public static boolean enableApi = true;
    public static boolean requireLinkToken = true;
    public static int apiPort = 0000;
    public static boolean enableApiBalanceQueries = true;
    public static boolean enableApiModifyQueries = false;

    // Limit Settings
    public static double maximumBalance = 100000000000.0;
    public static double minimumBalance = -10000.0;
    public static double minimumPaymentAmount = 0.01;

    // General Settings
    public static String currencySymbol = "$";
    public static boolean currencySymbolIsPrefix = true;
    public static String currencyFormat = "#,###.00";
    public static boolean allowLoans = true;
    public static double startingBalance = 0.00;
    public static int baltopUsersPerPage = 5;
}
