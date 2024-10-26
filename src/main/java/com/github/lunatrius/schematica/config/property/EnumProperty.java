package com.github.lunatrius.schematica.config.property;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @author Thomas Wearmouth
 * Created on 02/10/2024
 */
// TODO: add a general-purpose next (and maybe previous) function
public final class EnumProperty<T extends Enum<T>> extends Property<T> {
    public EnumProperty(final String key, final T defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(this.getValue().name());
    }

    @Override
    public void deserialize(final JsonObject object) {
        final JsonElement element = object.get(this.getKey());
        T resolved = this.getDefaultValue();
        try {
            resolved = (T) Enum.valueOf(resolved.getClass(), element.getAsString());
        }
        catch (Exception ignored) {}
        this.setValue(resolved);
    }
}
