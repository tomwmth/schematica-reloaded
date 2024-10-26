package com.github.lunatrius.schematica.config.property;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.GameData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Wearmouth
 * Created on 02/10/2024
 */
public final class BlockListProperty extends Property<List<Block>> {
    private static final FMLControlledNamespacedRegistry<Block> BLOCK_REGISTRY = GameData.getBlockRegistry();

    public BlockListProperty(final String key, final List<Block> defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public JsonElement serialize() {
        final JsonArray arr = new JsonArray();
        for (final Block block : this.getValue()) {
            arr.add(new JsonPrimitive(block.getRegistryName()));
        }
        return arr;
    }

    @Override
    public void deserialize(final JsonObject object) {
        final JsonArray arr = object.getAsJsonArray(this.getKey());
        final List<Block> list = new ArrayList<>();
        for (final JsonElement element : arr) {
            final String id = element.getAsString();
            final Block block = BLOCK_REGISTRY.getObject(new ResourceLocation(id));
            list.add(block);
        }
        this.setValue(list);
    }
}
