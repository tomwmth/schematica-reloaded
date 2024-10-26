package com.github.lunatrius.schematica.client.gui.config.button;

import com.github.lunatrius.schematica.config.property.DirectoryProperty;
import net.minecraft.client.gui.FontRenderer;

import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * @author Thomas Wearmouth
 * Created on 05/10/2024
 */
public final class DirectoryButton extends PropertyTextField<DirectoryProperty> {
    public DirectoryButton(final DirectoryProperty property, final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final FontRenderer fontRenderer) {
        super(property, buttonId, x, y, widthIn, heightIn, fontRenderer);
        this.setDisplayString();
    }

    @Override
    protected void writeProperty() {
        final Path value = Paths.get(this.textField.getText());
        this.property.setValue(value);
    }

    private void setDisplayString() {
        final Path value = this.property.getValue();
        this.textField.setText(value.toString());
    }
}
