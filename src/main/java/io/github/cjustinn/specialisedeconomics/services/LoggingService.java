package io.github.cjustinn.specialisedeconomics.services;

import io.github.cjustinn.specialisedeconomics.SpecialisedEconomics;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingService {
    private static final Logger logger = Logger.getLogger("Minecraft");
    public static void writeLog(Level severity, String message) {
        logger.log(
                severity,
                String.format("[%s]: %s", SpecialisedEconomics.plugin.getName(), message)
        );
    }
}
