package com.github.lunatrius.schematica.config.property;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author Thomas Wearmouth
 * Created on 04/10/2024
 */
public final class SwapSlotsProperty extends Property<boolean[]> {
    public final Queue<Integer> queue = new ArrayDeque<>();

    public SwapSlotsProperty(final String key, final boolean[] defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public JsonElement serialize() {
        final JsonArray arr = new JsonArray();
        for (final boolean slot : this.getValue()) {
            arr.add(new JsonPrimitive(slot));
        }
        return arr;
    }

    @Override
    public void deserialize(final JsonObject object) {
        final JsonArray arr = object.getAsJsonArray(this.getKey());
        final boolean[] slots = new boolean[9];
        for (int i = 0; i < slots.length; i++) {
            final JsonElement element = arr.get(i);
            slots[i] = element.getAsBoolean();
        }
        this.setValue(slots);
    }

    @Override
    public void setValue(boolean[] value) {
        super.setValue(value);
        this.queue.clear();
        final boolean[] slots = this.getValue();
        for (int i = 0; i < slots.length; i++) {
            if (slots[i]) {
                this.queue.offer(i);
            }
        }
    }
}
