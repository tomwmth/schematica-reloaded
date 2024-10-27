package com.github.lunatrius.schematica.client.gui.config;

import com.github.lunatrius.core.client.gui.GuiScreenBase;
import com.github.lunatrius.schematica.config.ConfigurationManager;
import com.github.lunatrius.schematica.config.property.*;
import com.github.lunatrius.schematica.reference.Names;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.List;

/**
 * @author Thomas Wearmouth
 * Created on 27/10/2024
 */
// TODO: this is a lot of duplicated code, but it works for now
public final class GuiSwapSlots extends GuiScreenBase {
    private static final int HEADER_HEIGHT = Math.round(20 * 1.75F);
    private static final int BUTTON_WIDTH = 360;
    private static final int BUTTON_HEIGHT = 18;
    private static final int BUTTON_PADDING = 4;
    private static final int BUTTON_LABEL_WIDTH = 80;
    private static final int HOVER_OVERLAY_WIDTH = 200;
    private static final int TINT_COLOR = 0x5F000000;

    private final SwapSlotsProperty property;

    public GuiSwapSlots(final GuiScreen guiScreen, final SwapSlotsProperty property) {
        super(guiScreen);
        this.property = property;
    }

    @Override
    public void initGui() {
        super.initGui();

        final int centerX = this.width / 2, centerY = this.height / 2;

        final int propertyX = centerX - (BUTTON_WIDTH / 2);
        int propertyY = HEADER_HEIGHT + BUTTON_PADDING;
        final boolean[] values = this.property.getValue();
        for (int i = 0; i < values.length; i++) {
            final String translationKey = Names.Config.LANG_PREFIX + "." + Names.Config.SWAP_SLOTS + i;

            final GuiLabel label = new GuiLabel(this.fontRendererObj, i, propertyX + 1, propertyY + 1, BUTTON_LABEL_WIDTH, BUTTON_HEIGHT, 0xFFFFFF);
            label.func_175202_a(translationKey);

            final ToggleButton button = new ToggleButton(i, values[i],propertyX + BUTTON_LABEL_WIDTH, propertyY, BUTTON_WIDTH - BUTTON_LABEL_WIDTH - BUTTON_PADDING, BUTTON_HEIGHT);

            propertyY += BUTTON_HEIGHT + BUTTON_PADDING;
            this.buttonList.add(button);
            this.labelList.add(label);
        }
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.drawGradientRect(0, 0, this.width, HEADER_HEIGHT, TINT_COLOR, TINT_COLOR);
        this.drawCenteredString(I18n.format(Names.Gui.Config.SWAP_SLOTS_TITLE), this.width / 2, HEADER_HEIGHT / 2, 0xFFFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);

        for (final GuiLabel label : this.labelList) {
            final int labelX = label.field_146162_g, labelY = label.field_146174_h;
            if (mouseX >= labelX && mouseX <= labelX + BUTTON_LABEL_WIDTH &&
                    mouseY >= labelY && mouseY <= labelY + BUTTON_HEIGHT) {
                final int id = label.field_175204_i;
                final List<String> hoverLines = Lists.newArrayList(
                        EnumChatFormatting.GRAY + I18n.format(Names.Config.LANG_PREFIX + "." + Names.Config.SWAP_SLOTS + id + ".tooltip"),
                        "",
                        EnumChatFormatting.YELLOW + "Default value: " + EnumChatFormatting.ITALIC + this.property.getDefaultValue()[id]
                );
                GuiUtils.drawHoveringText(hoverLines, mouseX, mouseY, this.width, this.height, HOVER_OVERLAY_WIDTH, this.fontRendererObj);
            }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        final boolean[] values = new boolean[9];
        for (final GuiButton button : this.buttonList) {
            if (button instanceof ToggleButton) {
                ToggleButton b = (ToggleButton) button;
                values[b.slot] = b.value;
            }
        }
        this.property.setValue(values);
        ConfigurationManager.save();
    }

    private void drawCenteredString(final String text, final int x, final int y, final int color) {
        this.drawCenteredString(this.fontRendererObj, text, x, y - (this.fontRendererObj.FONT_HEIGHT / 2), color);
    }

    private static final class ToggleButton extends GuiButton {
        private final int slot;
        private boolean value;

        public ToggleButton(final int slot, final boolean value, final int x, final int y, final int widthIn, final int heightIn) {
            super(slot, x, y, widthIn, heightIn, null);
            this.slot = slot;
            this.value = value;
            this.setDisplayString();
        }

        @Override
        public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
            final boolean clicked = super.mousePressed(mc, mouseX, mouseY);
            if (clicked) {
                this.value = !this.value;
                this.setDisplayString();
            }
            return clicked;
        }

        private void setDisplayString() {
            this.displayString = this.value ? EnumChatFormatting.GREEN + "true" : EnumChatFormatting.RED + "false";
        }
    }
}
