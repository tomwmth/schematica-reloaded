package com.github.lunatrius.schematica.client.gui.config.button;

import com.github.lunatrius.schematica.config.property.IntegerProperty;

/**
 * @author Thomas Wearmouth
 * Created on 05/10/2024
 */
public final class IntegerSlider extends PropertySlider<IntegerProperty> {
    public IntegerSlider(final IntegerProperty property, final int id, final int xPos, final int yPos, final int width, final int height) {
        super(property, id, xPos, yPos, width, height, "", property.getSuffix(), property.getMinimumValue(), property.getMaximumValue(), property.getValue(), false, true, slider -> {
            property.setValue(slider.getValueInt());
        });
        this.precision = 0;
    }
}
