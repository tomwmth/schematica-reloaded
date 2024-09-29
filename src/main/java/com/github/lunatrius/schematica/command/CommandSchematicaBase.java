package com.github.lunatrius.schematica.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class CommandSchematicaBase extends CommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(final ICommandSender sender) {
        // TODO: add logic for the client side when ready
        return super.canCommandSenderUseCommand(sender) || (sender instanceof EntityPlayerMP && getRequiredPermissionLevel() <= 0);
    }

    protected <T extends IChatComponent> T withStyle(final T component, final EnumChatFormatting formatting, @Nullable final String command) {
        final ChatStyle style = new ChatStyle();
        style.setColor(formatting);

        if (command != null) {
            style.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        }

        component.setChatStyle(style);

        return component;
    }
}
