package com.github.lunatrius.schematica.config.property;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.MathHelper;

/**
 * @author Thomas Wearmouth
 * Created on 02/10/2024
 */
public final class DoubleProperty extends Property<Double> {
    private final double minimumValue, maximumValue;
    private final int precision;

    public DoubleProperty(final String key, final double defaultValue, final double minimumValue, final double maximumValue) {
        this(key, defaultValue, minimumValue, maximumValue, 1);
    }

    public DoubleProperty(final String key, final double defaultValue, final double minimumValue, final double maximumValue, final int precision) {
        super(key, defaultValue);
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.precision = precision;
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(this.getValue());
    }

    @Override
    public void deserialize(final JsonObject object) {
        final JsonElement element = object.get(this.getKey());
        this.setValue(element.getAsDouble());
    }

    @Override
    public void setValue(final Double value) {
        super.setValue(MathHelper.clamp_double(value, this.minimumValue, this.maximumValue));
    }

    public double getMinimumValue() {
        return this.minimumValue;
    }

    public double getMaximumValue() {
        return this.maximumValue;
    }

    public int getPrecision() {
        return this.precision;
    }
}
