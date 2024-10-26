package com.github.lunatrius.schematica.client.gui.config.button;

import com.github.lunatrius.schematica.config.property.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

/**
 * @author Thomas Wearmouth
 * Created on 27/10/2024
 */
public abstract class PropertyTextField<T extends Property<?>> extends PropertyButton<T> {
    public final GuiTextField textField;

    public PropertyTextField(final T property, final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final FontRenderer fontRenderer) {
        super(property, buttonId, x, y, widthIn, heightIn, null);
        this.textField = new TextFieldExt(this, buttonId, fontRenderer, x + 1, y, widthIn - 2, heightIn - 2);
    }

    protected abstract void writeProperty();

    @Override
    public void drawButton(final Minecraft mc, final int mouseX, final int mouseY) {
        // NO-OP
    }

    @Override
    public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
        return false;
    }

    private static final class TextFieldExt extends GuiTextField {
        private final PropertyTextField<?> parent;

        private TextFieldExt(final PropertyTextField<?> parent, final int id, final FontRenderer fontRenderer, final int x, final int y, final int width, final int height) {
            super(id, fontRenderer, x, y, width, height);
            this.parent = parent;
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            if (!focused) {
                this.parent.writeProperty();
            }
        }

        @Override
        public boolean textboxKeyTyped(char character, int keyCode) {
            if (keyCode == Keyboard.KEY_RETURN) {
                this.setFocused(false);
                return false;
            }
            return super.textboxKeyTyped(character, keyCode);
        }
    }
}
