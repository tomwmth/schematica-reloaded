package com.github.lunatrius.schematica.client.gui.config.button;

import com.github.lunatrius.schematica.config.property.Property;
import net.minecraft.client.gui.GuiButton;

/**
 * @author Thomas Wearmouth
 * Created on 04/10/2024
 */
public abstract class PropertyButton<T extends Property<?>> extends GuiButton {
    protected final T property;

    public PropertyButton(final T property, final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.property = property;
    }
}
