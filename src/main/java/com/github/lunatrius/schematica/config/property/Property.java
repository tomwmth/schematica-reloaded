package com.github.lunatrius.schematica.config.property;

import com.github.lunatrius.schematica.reference.Names;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Objects;

/**
 * @author Thomas Wearmouth
 * Created on 02/10/2024
 */
public abstract class Property<T> {
    private final String key;

    private T value;

    private final T defaultValue;

    protected Property(final String key, final T defaultValue) {
        this.key = key;
        this.value = this.defaultValue = defaultValue;
    }

    public abstract JsonElement serialize();

    public abstract void deserialize(final JsonObject object);

    public String getKey() {
        return this.key;
    }

    public String getTranslationKey() {
        return Names.Config.LANG_PREFIX + "." + this.key;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(final T value) {
        this.value = value;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public boolean isDefault() {
        return Objects.equals(this.value, this.defaultValue);
    }
}
