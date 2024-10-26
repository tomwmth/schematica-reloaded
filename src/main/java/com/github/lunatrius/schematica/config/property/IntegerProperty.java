package com.github.lunatrius.schematica.config.property;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.MathHelper;

/**
 * @author Thomas Wearmouth
 * Created on 02/10/2024
 */
public final class IntegerProperty extends Property<Integer> {
    private final int minimumValue, maximumValue;
    private final String suffix;

    public IntegerProperty(final String key, final int defaultValue, final int minimumValue, final int maximumValue) {
        this(key, defaultValue, minimumValue, maximumValue, "");
    }

    public IntegerProperty(final String key, final int defaultValue, final int minimumValue, final int maximumValue, final String suffix) {
        super(key, defaultValue);
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.suffix = suffix;
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(this.getValue());
    }

    @Override
    public void deserialize(final JsonObject object) {
        final JsonElement element = object.get(this.getKey());
        this.setValue(element.getAsInt());
    }

    @Override
    public void setValue(final Integer value) {
        super.setValue(MathHelper.clamp_int(value, this.minimumValue, this.maximumValue));
    }

    public int getMinimumValue() {
        return this.minimumValue;
    }

    public int getMaximumValue() {
        return this.maximumValue;
    }

    public String getSuffix() {
        return this.suffix;
    }
}
