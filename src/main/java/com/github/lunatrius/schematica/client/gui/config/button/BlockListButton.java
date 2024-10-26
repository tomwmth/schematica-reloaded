package com.github.lunatrius.schematica.client.gui.config.button;

import com.github.lunatrius.schematica.config.property.BlockListProperty;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Thomas Wearmouth
 * Created on 27/10/2024
 */
public final class BlockListButton extends PropertyTextField<BlockListProperty> {
    private static final FMLControlledNamespacedRegistry<Block> BLOCK_REGISTRY = GameData.getBlockRegistry();

    public BlockListButton(final BlockListProperty property, final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final FontRenderer fontRenderer) {
        super(property, buttonId, x, y, widthIn, heightIn, fontRenderer);
        this.setDisplayString();
    }

    @Override
    protected void writeProperty() {
        final String value = this.textField.getText();
        final List<Block> list = new ArrayList<>();
        if (!value.isEmpty()) {
            for (final String id : value.split(", ")) {
                final Block resolved = BLOCK_REGISTRY.getObject(new ResourceLocation(id));
                if (resolved != null) {
                    list.add(resolved);
                }
            }
        }
        this.property.setValue(list);
    }

    private void setDisplayString() {
        final List<Block> value = this.property.getValue();
        final String text = value.stream()
                .map(Block::getRegistryName)
                .collect(Collectors.joining(", "));
        this.textField.setText(text);
    }
}
