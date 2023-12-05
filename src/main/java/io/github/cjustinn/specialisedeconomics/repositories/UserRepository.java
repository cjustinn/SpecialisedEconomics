package io.github.cjustinn.specialisedeconomics.repositories;

import io.github.cjustinn.specialisedeconomics.enums.DatabaseQuery;
import io.github.cjustinn.specialisedeconomics.enums.DatabaseQueryValueType;
import io.github.cjustinn.specialisedeconomics.models.SpecialisedEconomyUser;
import io.github.cjustinn.specialisedeconomics.models.sql.DatabaseQueryValue;
import io.github.cjustinn.specialisedeconomics.services.DatabaseService;
import io.github.cjustinn.specialisedeconomics.services.LoggingService;

import javax.annotation.Nullable;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class UserRepository {
    public static Map<String, SpecialisedEconomyUser> users = new HashMap<String, SpecialisedEconomyUser>();

    public static boolean createUser(final String uuid) {
        if (DatabaseService.RunUpdate(DatabaseQuery.InsertUser, new DatabaseQueryValue[] {
                new DatabaseQueryValue(1, uuid, DatabaseQueryValueType.String),
                new DatabaseQueryValue(2, PluginSettingsRepository.startingBalance, DatabaseQueryValueType.Double)
        })) {
            users.put(uuid, new SpecialisedEconomyUser(uuid, null, PluginSettingsRepository.startingBalance, new Date()));
            return true;
        } else return false;
    }

    public static int getBaltopUserLimit() {
        return PluginSettingsRepository.baltopUsersPerPage;
    }

    public static @Nullable List<SpecialisedEconomyUser> getBaltopUsersForPage(final int page) {
        if (page < 1 || page > (int) Math.ceil((double) users.size() / getBaltopUserLimit())) {
            return null;
        } else {
            final int startIndex = getBaltopUserLimit() * (page - 1);
            final int endIndex = Math.min(startIndex + (getBaltopUserLimit() - 1), (users.size() - 1));

            return new ArrayList<SpecialisedEconomyUser>() {{
                List<SpecialisedEconomyUser> allUsers = users.values().stream().collect(Collectors.toList());
                Collections.sort(allUsers, Comparator.comparingDouble(SpecialisedEconomyUser::getBalance).reversed());

                for (int i = startIndex; i <= endIndex; i++) {
                    add(allUsers.get(i));
                }
            }};
        }
    }
}
