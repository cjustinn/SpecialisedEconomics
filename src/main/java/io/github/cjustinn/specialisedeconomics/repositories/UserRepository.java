package io.github.cjustinn.specialisedeconomics.repositories;

import io.github.cjustinn.specialisedeconomics.enums.DatabaseQuery;
import io.github.cjustinn.specialisedeconomics.models.SpecialisedEconomyUser;
import io.github.cjustinn.specialisedlib.Database.DatabaseService;
import io.github.cjustinn.specialisedlib.Database.DatabaseValue;
import io.github.cjustinn.specialisedlib.Database.DatabaseValueType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class UserRepository {
    public static Map<String, SpecialisedEconomyUser> users = new HashMap<String, SpecialisedEconomyUser>();

    public static boolean createUser(final String uuid) {
        if (DatabaseService.RunUpdate(DatabaseQuery.InsertUser, new DatabaseValue[] {
                new DatabaseValue(1, uuid, DatabaseValueType.String),
                new DatabaseValue(2, PluginSettingsRepository.startingBalance, DatabaseValueType.Double)
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
