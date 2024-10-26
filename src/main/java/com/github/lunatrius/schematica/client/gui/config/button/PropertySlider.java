package com.github.lunatrius.schematica.client.gui.config.button;

import com.github.lunatrius.schematica.config.property.Property;
import net.minecraftforge.fml.client.config.GuiSlider;

/**
 * @author Thomas Wearmouth
 * Created on 05/10/2024
 */
public abstract class PropertySlider<T extends Property<?>> extends GuiSlider {
    protected final T property;

    public PropertySlider(final T property, final int id, final int xPos, final int yPos, final String displayStr, final double minVal, final double maxVal, final double currentVal, ISlider par) {
        super(id, xPos, yPos, displayStr, minVal, maxVal, currentVal, par);
        this.property = property;
    }

    public PropertySlider(final T property, final int id, final int xPos, final int yPos, final int width, final int height, final String prefix, final String suf, final double minVal, final double maxVal, final double currentVal, final boolean showDec, final boolean drawStr, ISlider par) {
        super(id, xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, par);
        this.property = property;
    }

    @Override
    public double getValue() {
        // Disgusting hack to reduce precision issues
        final double original = super.getValue();
        StringBuilder str = new StringBuilder(Double.toString(original));
        if (str.substring(str.indexOf(".") + 1).length() > this.precision) {
            str = new StringBuilder(str.substring(0, str.indexOf(".") + this.precision + 1));

            if (str.toString().endsWith(".")) {
                str = new StringBuilder(str.substring(0, str.indexOf(".") + this.precision));
            }
        }
        else {
            while (str.substring(str.indexOf(".") + 1).length() < this.precision) {
                str.append("0");
            }
        }
        return Double.parseDouble(str.toString());
    }
}
