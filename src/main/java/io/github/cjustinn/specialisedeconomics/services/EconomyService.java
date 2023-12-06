package io.github.cjustinn.specialisedeconomics.services;

import io.github.cjustinn.specialisedeconomics.models.SpecialisedEconomyUser;
import io.github.cjustinn.specialisedlib.Economy.SpecialisedEconomy;
import net.milkbowl.vault.economy.Economy;

public class EconomyService {
    private static SpecialisedEconomy economy;

    public static SpecialisedEconomy getEconomy() {
        return economy;
    }

    public static void setEconomy(SpecialisedEconomy econ) {
        economy = econ;
    }
}
