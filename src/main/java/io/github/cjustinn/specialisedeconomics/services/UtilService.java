package io.github.cjustinn.specialisedeconomics.services;

import io.github.cjustinn.specialisedeconomics.repositories.PluginSettingsRepository;
import net.milkbowl.vault.economy.Economy;

import java.text.DecimalFormat;

public class UtilService {
    public static String formatCurrency(double value) {
        final DecimalFormat formatter = new DecimalFormat(PluginSettingsRepository.currencyFormat);
        return String.format("%s%s",
                PluginSettingsRepository.currencySymbolIsPrefix ? EconomyService.getEconomy().currencyNamePlural() : formatter.format(value),
                PluginSettingsRepository.currencySymbolIsPrefix ? formatter.format(value) : EconomyService.getEconomy().currencyNamePlural()
        );
    }
}
