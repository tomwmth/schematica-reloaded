package com.github.lunatrius.schematica.config.property;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @author Thomas Wearmouth
 * Created on 02/10/2024
 */
public final class BooleanProperty extends Property<Boolean> {
    public BooleanProperty(final String key, final boolean defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(this.getValue());
    }

    @Override
    public void deserialize(final JsonObject object) {
        final JsonElement element = object.get(this.getKey());
        this.setValue(element.getAsBoolean());
    }
}
