package com.github.lunatrius.schematica.client.gui.config.button;

import com.github.lunatrius.schematica.config.property.SwapSlotsProperty;
import com.github.lunatrius.schematica.reference.Names;
import net.minecraft.client.resources.I18n;

/**
 * @author Thomas Wearmouth
 * Created on 27/10/2024
 */
public final class SwapSlotsButton extends PropertyButton<SwapSlotsProperty> {
    public SwapSlotsButton(final SwapSlotsProperty property, final int buttonId, final int x, final int y, final int widthIn, final int heightIn) {
        super(property, buttonId, x, y, widthIn, heightIn, I18n.format(Names.Gui.Config.SWAP_SLOTS_EDIT));
    }
}
