package com.github.lunatrius.schematica.command;

import com.github.lunatrius.core.util.FileUtils;
import com.github.lunatrius.schematica.Schematica;
import com.github.lunatrius.schematica.reference.Names;
import com.github.lunatrius.schematica.reference.Reference;
import com.github.lunatrius.schematica.util.FileFilterSchematic;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.LinkedList;

@ParametersAreNonnullByDefault
public class CommandSchematicaList extends CommandSchematicaBase {
    private static final FileFilterSchematic FILE_FILTER_SCHEMATIC = new FileFilterSchematic(false);

    @Override
    public String getCommandName() {
        return Names.Command.List.NAME;
    }

    @Override
    public String getCommandUsage(final ICommandSender sender) {
        return Names.Command.List.Message.USAGE;
    }

    @Override
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer)) {
            throw new CommandException(Names.Command.Save.Message.PLAYERS_ONLY);
        }

        int page = 0;
        try {
            if (args.length > 0) {
                page = Integer.parseInt(args[0]) - 1;
                if (page < 0) {
                    page = 0;
                }
            }
        } catch (final NumberFormatException e) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        final EntityPlayer player = (EntityPlayer) sender;
        final int pageSize = 9; //maximum number of lines available without opening chat.
        final int pageStart = page * pageSize;
        final int pageEnd = pageStart + pageSize;
        int currentFile = 0;

        final LinkedList<IChatComponent> componentsToSend = new LinkedList<IChatComponent>();

        final File schematicDirectory = Schematica.proxy.getPlayerSchematicDirectory(player, true);
        if (schematicDirectory == null) {
            Reference.logger.warn("Unable to determine the schematic directory for player {}", player);
            throw new CommandException(Names.Command.Save.Message.PLAYER_SCHEMATIC_DIR_UNAVAILABLE);
        }

        if (!schematicDirectory.exists()) {
            if (!schematicDirectory.mkdirs()) {
                Reference.logger.warn("Could not create player schematic directory {}", schematicDirectory.getAbsolutePath());
                throw new CommandException(Names.Command.Save.Message.PLAYER_SCHEMATIC_DIR_UNAVAILABLE);
            }
        }

        final File[] files = schematicDirectory.listFiles(FILE_FILTER_SCHEMATIC);
        for (final File path : files) {
            if (currentFile >= pageStart && currentFile < pageEnd) {
                final String fileName = path.getName();

                final IChatComponent chatComponent = new ChatComponentText(String.format("%2d (%s): %s [", currentFile + 1, FileUtils.humanReadableByteCount(path.length()), FilenameUtils.removeExtension(fileName)));

                final String removeCommand = String.format("/%s %s", Names.Command.Remove.NAME, fileName);
                final IChatComponent removeLink = withStyle(new ChatComponentTranslation(Names.Command.List.Message.REMOVE), EnumChatFormatting.RED, removeCommand);
                chatComponent.appendSibling(removeLink);
                chatComponent.appendText("][");

                final String downloadCommand = String.format("/%s %s", Names.Command.Download.NAME, fileName);
                final IChatComponent downloadLink = withStyle(new ChatComponentTranslation(Names.Command.List.Message.DOWNLOAD), EnumChatFormatting.GREEN, downloadCommand);
                chatComponent.appendSibling(downloadLink);
                chatComponent.appendText("]");

                componentsToSend.add(chatComponent);
            }
            ++currentFile;
        }

        if (currentFile == 0) {
            sender.addChatMessage(new ChatComponentTranslation(Names.Command.List.Message.NO_SCHEMATICS));
            return;
        }

        final int totalPages = (currentFile - 1) / pageSize;
        if (page > totalPages) {
            throw new CommandException(Names.Command.List.Message.NO_SUCH_PAGE);
        }

        sender.addChatMessage(withStyle(new ChatComponentTranslation(Names.Command.List.Message.PAGE_HEADER, page + 1, totalPages + 1), EnumChatFormatting.DARK_GREEN, null));
        for (final IChatComponent chatComponent : componentsToSend) {
            sender.addChatMessage(chatComponent);
        }
    }
}
