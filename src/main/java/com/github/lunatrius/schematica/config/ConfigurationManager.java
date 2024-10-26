package com.github.lunatrius.schematica.config;

import com.github.lunatrius.schematica.config.property.Property;
import com.github.lunatrius.schematica.reference.Reference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.List;

/**
 * @author Thomas Wearmouth
 * Created on 02/10/2024
 */
public final class ConfigurationManager {
    public static File file;

    public static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public static void init(final File configFile) {
        final String filePath = configFile.getPath();
        if (!filePath.endsWith(".json")) {
            file = new File(filePath.replaceAll("\\.(.+)$", ".json"));
        }
        else {
            file = configFile;
        }

        load();
    }

    public static void load() {
        if (!file.exists()) {
            save();
            return;
        }

        try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
            final JsonObject parentObj = GSON.fromJson(reader, JsonObject.class);

            for (final Category category : Category.values()) {
                final JsonObject categoryObj = parentObj.getAsJsonObject(category.getId());
                final List<Property<?>> properties = Configuration.properties.get(category);
                for (final Property<?> property : properties) {
                    property.deserialize(categoryObj);
                }
            }
        }
        catch (Exception ex) {
            Reference.logger.error("Failed to load configuration", ex);
        }
    }

    public static void save() {
        try {
            if (!file.exists() && !file.createNewFile()) {
                throw new IllegalStateException("Failed to create configuration file");
            }

            try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                final JsonObject parentObj = new JsonObject();

                for (final Category category : Category.values()) {
                    final JsonObject categoryObj = new JsonObject();
                    final List<Property<?>> properties = Configuration.properties.get(category);
                    for (final Property<?> property : properties) {
                        categoryObj.add(property.getKey(), property.serialize());
                    }
                    parentObj.add(category.getId(), categoryObj);
                }

                GSON.toJson(parentObj, writer);
            }
        }
        catch (Exception ex) {
            Reference.logger.error("Failed to save configuration", ex);
        }
    }
}
