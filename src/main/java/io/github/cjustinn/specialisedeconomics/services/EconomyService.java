package io.github.cjustinn.specialisedeconomics.services;

import net.milkbowl.vault.economy.Economy;

public class EconomyService {
    private static Economy economy;

    public static Economy getEconomy() {
        return economy;
    }

    public static void setEconomy(Economy econ) {
        economy = econ;
    }
}
