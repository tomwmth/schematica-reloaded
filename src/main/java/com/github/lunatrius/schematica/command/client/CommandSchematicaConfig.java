package com.github.lunatrius.schematica.command.client;

import com.github.lunatrius.core.handler.DelayedGuiDisplayTicker;
import com.github.lunatrius.schematica.client.gui.config.GuiFactory;
import com.github.lunatrius.schematica.command.CommandSchematicaBase;
import com.github.lunatrius.schematica.reference.Names;
import net.minecraft.command.ICommandSender;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author Thomas Wearmouth
 * Created on 30/09/2024
 */
@ParametersAreNonnullByDefault
public final class CommandSchematicaConfig extends CommandSchematicaBase {
    @Override
    public String getCommandName() {
        return Names.Command.Config.NAME;
    }

    @Override
    public String getCommandUsage(final ICommandSender sender) {
        return Names.Command.Config.Message.USAGE;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        DelayedGuiDisplayTicker.create(new GuiFactory.GuiModConfig(null), 1);
    }
}
