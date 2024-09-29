package com.github.lunatrius.schematica.command;

import com.github.lunatrius.core.util.FileUtils;
import com.github.lunatrius.schematica.Schematica;
import com.github.lunatrius.schematica.reference.Names;
import com.github.lunatrius.schematica.reference.Reference;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.Arrays;

@ParametersAreNonnullByDefault
public class CommandSchematicaRemove extends CommandSchematicaBase {
    @Override
    public String getCommandName() {
        return Names.Command.Remove.NAME;
    }

    @Override
    public String getCommandUsage(final ICommandSender sender) {
        return Names.Command.Remove.Message.USAGE;
    }

    @Override
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length < 1) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        if (!(sender instanceof EntityPlayer)) {
            throw new CommandException(Names.Command.Remove.Message.PLAYERS_ONLY);
        }

        final EntityPlayer player = (EntityPlayer) sender;

        boolean delete = false;
        String name = String.join(" ", args);

        if (args.length > 1) {
            //check if the last parameter is a hash, which constitutes a confirmation.
            final String potentialNameHash = args[args.length - 1];
            if (potentialNameHash.length() == 32) {
                //We probably have a match.
                final String[] a = Arrays.copyOfRange(args, 0, args.length - 1);
                //The name then should be everything except the last element
                name = String.join(" ", a);

                final String hash = Hashing.md5().hashString(name, Charsets.UTF_8).toString();

                if (potentialNameHash.equals(hash)) {
                    delete = true;
                }
            }
        }

        final File schematicDirectory = Schematica.proxy.getPlayerSchematicDirectory(player, true);
        final File file = new File(schematicDirectory, name);
        if (!FileUtils.contains(schematicDirectory, file)) {
            Reference.logger.error("{} has tried to download the file {}", player.getName(), name);
            throw new CommandException(Names.Command.Remove.Message.SCHEMATIC_NOT_FOUND);
        }

        if (file.exists()) {
            if (delete) {
                if (file.delete()) {
                    sender.addChatMessage(new ChatComponentTranslation(Names.Command.Remove.Message.SCHEMATIC_REMOVED, name));
                } else {
                    throw new CommandException(Names.Command.Remove.Message.SCHEMATIC_NOT_FOUND);
                }
            } else {
                final String hash = Hashing.md5().hashString(name, Charsets.UTF_8).toString();
                final String confirmCommand = String.format("/%s %s %s", Names.Command.Remove.NAME, name, hash);
                final IChatComponent chatComponent = new ChatComponentTranslation(Names.Command.Remove.Message.ARE_YOU_SURE_START, name);
                chatComponent.appendSibling(new ChatComponentText(" ["));
                chatComponent.appendSibling(withStyle(new ChatComponentTranslation(Names.Command.Remove.Message.YES), EnumChatFormatting.RED, confirmCommand));
                chatComponent.appendSibling(new ChatComponentText("]"));

                sender.addChatMessage(chatComponent);
            }
        } else {
            throw new CommandException(Names.Command.Remove.Message.SCHEMATIC_NOT_FOUND);
        }
    }
}
