package com.github.lunatrius.schematica.handler.client;

import com.github.lunatrius.schematica.client.gui.control.GuiSchematicControl;
import com.github.lunatrius.schematica.client.gui.load.GuiSchematicLoad;
import com.github.lunatrius.schematica.client.gui.save.GuiSchematicSave;
import com.github.lunatrius.schematica.client.printer.SchematicPrinter;
import com.github.lunatrius.schematica.client.renderer.RenderSchematic;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.client.world.SchematicWorld.LayerMode;
import com.github.lunatrius.schematica.config.Configuration;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.reference.Names;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.*;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class InputHandler {
    public static final InputHandler INSTANCE = new InputHandler();

    private static final KeyBinding KEY_BINDING_LOAD = new KeyBinding(Names.Keys.LOAD, Keyboard.KEY_DIVIDE, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_SAVE = new KeyBinding(Names.Keys.SAVE, Keyboard.KEY_MULTIPLY, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_CONTROL = new KeyBinding(Names.Keys.CONTROL, Keyboard.KEY_SUBTRACT, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_LAYER_INC = new KeyBinding(Names.Keys.LAYER_INC, Keyboard.KEY_NONE, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_LAYER_DEC = new KeyBinding(Names.Keys.LAYER_DEC, Keyboard.KEY_NONE, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_LAYER_TOGGLE = new KeyBinding(Names.Keys.LAYER_TOGGLE, Keyboard.KEY_NONE, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_RENDER_TOGGLE = new KeyBinding(Names.Keys.RENDER_TOGGLE, Keyboard.KEY_NONE, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_PRINTER_TOGGLE = new KeyBinding(Names.Keys.PRINTER_TOGGLE, Keyboard.KEY_NONE, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_MOVE_HERE = new KeyBinding(Names.Keys.MOVE_HERE, Keyboard.KEY_NONE, Names.Keys.CATEGORY);

    public static final KeyBinding[] KEY_BINDINGS = new KeyBinding[] {
            KEY_BINDING_LOAD,
            KEY_BINDING_SAVE,
            KEY_BINDING_CONTROL,
            KEY_BINDING_LAYER_INC,
            KEY_BINDING_LAYER_DEC,
            KEY_BINDING_LAYER_TOGGLE,
            KEY_BINDING_RENDER_TOGGLE,
            KEY_BINDING_PRINTER_TOGGLE,
            KEY_BINDING_MOVE_HERE,
    };

    private final Minecraft minecraft = Minecraft.getMinecraft();

    private InputHandler() {}

    @SubscribeEvent
    public void onKeyInput(final InputEvent event) {
        if (this.minecraft.currentScreen == null) {
            final SchematicWorld schematic = ClientProxy.schematic;

            if (KEY_BINDING_LOAD.isPressed()) {
                this.minecraft.displayGuiScreen(new GuiSchematicLoad(this.minecraft.currentScreen));
            }

            if (KEY_BINDING_SAVE.isPressed()) {
                this.minecraft.displayGuiScreen(new GuiSchematicSave(this.minecraft.currentScreen));
            }

            if (KEY_BINDING_CONTROL.isPressed()) {
                this.minecraft.displayGuiScreen(new GuiSchematicControl(this.minecraft.currentScreen));
            }

            if (KEY_BINDING_LAYER_INC.isPressed()) {
                if (schematic != null && schematic.layerMode != LayerMode.ALL) {
                    schematic.renderingLayer = MathHelper.clamp_int(schematic.renderingLayer + 1, 0, schematic.getHeight() - 1);
                    RenderSchematic.INSTANCE.refresh();
                }
            }

            if (KEY_BINDING_LAYER_DEC.isPressed()) {
                if (schematic != null && schematic.layerMode != LayerMode.ALL) {
                    schematic.renderingLayer = MathHelper.clamp_int(schematic.renderingLayer - 1, 0, schematic.getHeight() - 1);
                    RenderSchematic.INSTANCE.refresh();
                }
            }

            if (KEY_BINDING_LAYER_TOGGLE.isPressed()) {
                if (schematic != null) {
                    schematic.layerMode = LayerMode.next(schematic.layerMode);
                    RenderSchematic.INSTANCE.refresh();
                }
            }

            if (KEY_BINDING_RENDER_TOGGLE.isPressed()) {
                if (schematic != null) {
                    schematic.isRendering = !schematic.isRendering;
                    RenderSchematic.INSTANCE.refresh();
                }
            }

            if (KEY_BINDING_PRINTER_TOGGLE.isPressed()) {
                if (schematic != null) {
                    final boolean printing = SchematicPrinter.INSTANCE.togglePrinting();
                    this.minecraft.thePlayer.addChatMessage(new ChatComponentTranslation(Names.Messages.TOGGLE_PRINTER, I18n.format(printing ? Names.Gui.ON : Names.Gui.OFF)));
                }
            }

            if (KEY_BINDING_MOVE_HERE.isPressed()) {
                if (schematic != null) {
                    ClientProxy.moveSchematicToPlayer(schematic);
                    RenderSchematic.INSTANCE.refresh();
                }
            }

            if (Configuration.general.arrowKeyMove.getValue() && schematic != null && Keyboard.getEventKeyState()) {
                final EnumFacing facing = this.minecraft.thePlayer.getHorizontalFacing();
                final boolean sneaking = this.minecraft.thePlayer.isSneaking();
                this.handleArrowKey(Keyboard.getEventKey(), facing, sneaking, schematic);
            }

            if (this.minecraft.gameSettings.keyBindPickBlock.isKeyDown() && GuiScreen.isShiftKeyDown()) {
                if (schematic != null && schematic.isRendering) {
                    final boolean success = pickBlock(schematic, ClientProxy.objectMouseOver);
                    if (success) {
                        this.minecraft.gameSettings.keyBindPickBlock.isPressed();
                    }
                }
            }
        }
    }

    private void handleArrowKey(final int key, final EnumFacing facing, final boolean sneaking, final SchematicWorld schematic) {
        EnumFacing moveDirection = null;
        switch (key) {
            case Keyboard.KEY_UP:
                moveDirection = sneaking ? EnumFacing.UP : facing;
                break;
            case Keyboard.KEY_DOWN:
                moveDirection = sneaking ? EnumFacing.DOWN : facing.getOpposite();
                break;
            case Keyboard.KEY_LEFT:
                moveDirection = facing.rotateYCCW();
                break;
            case Keyboard.KEY_RIGHT:
                moveDirection = facing.rotateY();
                break;
        }

        if (moveDirection != null) {
            schematic.position.offset(moveDirection);
            RenderSchematic.INSTANCE.refresh();
            SchematicPrinter.INSTANCE.refresh();
        }
    }

    private boolean pickBlock(final SchematicWorld schematic, final MovingObjectPosition objectMouseOver) {
        // Minecraft.func_147112_ai
        if (objectMouseOver != null && objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.MISS) {
            final EntityPlayerSP player = this.minecraft.thePlayer;
            if (ForgeHooks.onPickBlock(objectMouseOver, player, schematic)) {
                if (player.capabilities.isCreativeMode) {
                    // 9 needs to be changed back to 10 if targeting a version with offhand
                    final int slot = player.inventoryContainer.inventorySlots.size() - 9 + player.inventory.currentItem;
                    this.minecraft.playerController.sendSlotPacket(player.inventory.getStackInSlot(player.inventory.currentItem), slot);
                    return true;
                }
            }
        }
        return false;
    }
}
