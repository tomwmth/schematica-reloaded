package com.github.lunatrius.schematica.client.gui.config;

import com.github.lunatrius.core.client.gui.GuiScreenBase;
import com.github.lunatrius.schematica.client.gui.config.button.*;
import com.github.lunatrius.schematica.config.Category;
import com.github.lunatrius.schematica.config.Configuration;
import com.github.lunatrius.schematica.config.ConfigurationManager;
import com.github.lunatrius.schematica.config.property.*;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thomas Wearmouth
 * Created on 04/10/2024
 */
public final class GuiConfiguration extends GuiScreenBase {
    private static final int CATEGORY_BUTTON_WIDTH = 64;
    private static final int CATEGORY_BUTTON_HEIGHT = 20;
    private static final int CATEGORY_BUTTON_PADDING = 8;
    private static final int HEADER_HEIGHT = Math.round(CATEGORY_BUTTON_HEIGHT * 1.75F);
    private static final int PROPERTY_HEIGHT = 18;
    private static final int PROPERTY_PADDING = 4;
    private static final int PROPERTY_LABEL_WIDTH = 120;
    private static final int HOVER_OVERLAY_WIDTH = 200;
    private static final int TINT_COLOR = 0x5F000000;

    private Category selectedCategory = Category.GENERAL;
    private Map<Integer, Property<?>> propertyMap;

    public GuiConfiguration(final GuiScreen guiScreen) {
        super(guiScreen);
    }

    @Override
    public void initGui() {
        super.initGui();

        final int centerX = this.width / 2, centerY = this.height / 2;

        final Category[] categories = Category.values();
        final int totalCategoryLength = categories.length * (CATEGORY_BUTTON_WIDTH + CATEGORY_BUTTON_PADDING);
        int categoryX = centerX - (totalCategoryLength / 2);
        final int categoryY = (HEADER_HEIGHT - CATEGORY_BUTTON_HEIGHT) / 2;
        for (int i = 0; i < categories.length; i++) {
            final Category category = categories[i];
            final GuiButton button = new GuiButton(i, categoryX, categoryY, CATEGORY_BUTTON_WIDTH, CATEGORY_BUTTON_HEIGHT, I18n.format(category.getTranslationKey()));
            button.enabled = category != this.selectedCategory;
            categoryX += CATEGORY_BUTTON_WIDTH + CATEGORY_BUTTON_PADDING;
            this.buttonList.add(button);
        }

        this.propertyMap = new HashMap<>();
        final List<Property<?>> properties = Configuration.properties.get(this.selectedCategory);
        final int propertyX = centerX - (totalCategoryLength / 2);
        int propertyY = HEADER_HEIGHT + PROPERTY_PADDING;
        for (int i = 0; i < properties.size(); i++) {
            final Property<?> property = properties.get(i);
            final int id = categories.length + i;

            final GuiLabel label = new GuiLabel(this.fontRendererObj, id, propertyX + 1, propertyY + 1, PROPERTY_LABEL_WIDTH, PROPERTY_HEIGHT, 0xFFFFFF);
            label.func_175202_a(property.getTranslationKey());

            GuiButton button = null;
            if (property instanceof BooleanProperty) {
                button = new BooleanButton((BooleanProperty) property, id, propertyX + PROPERTY_LABEL_WIDTH, propertyY, totalCategoryLength - PROPERTY_LABEL_WIDTH - PROPERTY_PADDING, PROPERTY_HEIGHT);
            }
            else if (property instanceof IntegerProperty) {
                button = new IntegerSlider((IntegerProperty) property, id, propertyX + PROPERTY_LABEL_WIDTH, propertyY, totalCategoryLength - PROPERTY_LABEL_WIDTH - PROPERTY_PADDING, PROPERTY_HEIGHT);
            }
            else if (property instanceof DoubleProperty) {
                button = new DoubleSlider((DoubleProperty) property, id, propertyX + PROPERTY_LABEL_WIDTH, propertyY, totalCategoryLength - PROPERTY_LABEL_WIDTH - PROPERTY_PADDING, PROPERTY_HEIGHT);
            }
            else if (property instanceof DirectoryProperty) {
                button = new DirectoryButton((DirectoryProperty) property, id, propertyX + PROPERTY_LABEL_WIDTH, propertyY, totalCategoryLength - PROPERTY_LABEL_WIDTH - PROPERTY_PADDING, PROPERTY_HEIGHT, this.fontRendererObj);
            }
            else if (property instanceof BlockListProperty) {
                button = new BlockListButton((BlockListProperty) property, id, propertyX + PROPERTY_LABEL_WIDTH, propertyY, totalCategoryLength - PROPERTY_LABEL_WIDTH - PROPERTY_PADDING, PROPERTY_HEIGHT, this.fontRendererObj);
            }

            if (button != null) {
                propertyY += PROPERTY_HEIGHT + PROPERTY_PADDING;

                this.buttonList.add(button);
                this.labelList.add(label);
                this.propertyMap.put(id, property);
                if (button instanceof PropertyTextField) {
                    final PropertyTextField<?> b = (PropertyTextField<?>) button;
                    this.textFields.add(b.textField);
                }
            }
        }
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.drawGradientRect(0, 0, this.width, HEADER_HEIGHT, TINT_COLOR, TINT_COLOR);

        super.drawScreen(mouseX, mouseY, partialTicks);

        for (final GuiLabel label : this.labelList) {
            final int labelX = label.field_146162_g, labelY = label.field_146174_h;
            if (mouseX >= labelX && mouseX <= labelX + PROPERTY_LABEL_WIDTH &&
                mouseY >= labelY && mouseY <= labelY + PROPERTY_HEIGHT) {
                final Property<?> property = this.propertyMap.get(label.field_175204_i);
                if (property != null) {
                    final List<String> hoverLines = Lists.newArrayList(
                            EnumChatFormatting.GRAY + I18n.format(property.getTranslationKey() + ".tooltip"),
                            "",
                            EnumChatFormatting.YELLOW + "Default value: " + EnumChatFormatting.ITALIC + property.getDefaultValue().toString()
                    );
                    GuiUtils.drawHoveringText(hoverLines, mouseX, mouseY, this.width, this.height, HOVER_OVERLAY_WIDTH, this.fontRendererObj);
                }
            }
        }
    }

    @Override
    protected void actionPerformed(final GuiButton button) {
        final int id = button.id;
        if (button.enabled) {
            final Category[] categories = Category.values();
            if (id >= 0 && id < categories.length) {
                this.selectedCategory = categories[id];
                this.initGui();
            }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        ConfigurationManager.save();
    }
}
