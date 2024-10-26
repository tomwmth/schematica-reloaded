package com.github.lunatrius.schematica.client.gui.config.button;

import com.github.lunatrius.schematica.config.property.DoubleProperty;

/**
 * @author Thomas Wearmouth
 * Created on 05/10/2024
 */
public final class DoubleSlider extends PropertySlider<DoubleProperty> {
    public DoubleSlider(final DoubleProperty property, final int id, final int xPos, final int yPos, final int width, final int height) {
        super(property, id, xPos, yPos, width, height, "", "", property.getMinimumValue(), property.getMaximumValue(), property.getValue(), true, true, slider -> {
            property.setValue(slider.getValue());
        });
        this.precision = property.getPrecision();
    }
}
