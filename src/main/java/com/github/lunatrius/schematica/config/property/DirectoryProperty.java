package com.github.lunatrius.schematica.config.property;

import com.github.lunatrius.schematica.reference.Reference;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Thomas Wearmouth
 * Created on 02/10/2024
 */
public final class DirectoryProperty extends Property<Path> {
    public DirectoryProperty(final String key, final Path defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(this.getValue().toString());
    }

    @Override
    public void deserialize(final JsonObject object) {
        final JsonElement element = object.get(this.getKey());
        try {
            final Path path = Paths.get(element.getAsString());
            this.setValue(path);
        }
        catch (Exception ex) {
            Reference.logger.error("Unable to load value for directory property", ex);
        }
    }
}
