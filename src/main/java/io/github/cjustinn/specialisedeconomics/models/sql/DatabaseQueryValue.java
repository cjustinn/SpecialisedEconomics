package io.github.cjustinn.specialisedeconomics.models.sql;

import io.github.cjustinn.specialisedeconomics.enums.DatabaseQueryValueType;

public class DatabaseQueryValue<T> {
    public final int position;
    public final T value;
    public final DatabaseQueryValueType type;

    public DatabaseQueryValue(final int pos, final T v, final DatabaseQueryValueType type) {
        this.position = pos;
        this.value = v;
        this.type = type;
    }
}
