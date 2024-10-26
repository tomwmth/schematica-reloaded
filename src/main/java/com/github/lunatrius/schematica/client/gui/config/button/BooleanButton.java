package com.github.lunatrius.schematica.client.gui.config.button;

import com.github.lunatrius.schematica.config.property.BooleanProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author Thomas Wearmouth
 * Created on 04/10/2024
 */
public final class BooleanButton extends PropertyButton<BooleanProperty> {
    public BooleanButton(final BooleanProperty property, final int buttonId, final int x, final int y, final int widthIn, final int heightIn) {
        super(property, buttonId, x, y, widthIn, heightIn, null);
        this.setDisplayString();
    }

    @Override
    public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
        final boolean clicked = super.mousePressed(mc, mouseX, mouseY);
        if (clicked) {
            final boolean old = this.property.getValue();
            this.property.setValue(!old);
            this.setDisplayString();
        }
        return clicked;
    }

    private void setDisplayString() {
        final boolean value = this.property.getValue();
        this.displayString = value ? EnumChatFormatting.GREEN + "true" : EnumChatFormatting.RED + "false";
    }
}
