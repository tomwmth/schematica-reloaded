package com.github.lunatrius.schematica.config;

import com.github.lunatrius.schematica.Schematica;
import com.github.lunatrius.schematica.config.property.*;
import com.github.lunatrius.schematica.reference.Names;
import com.github.lunatrius.schematica.util.ItemStackSortType;
import net.minecraft.block.Block;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Thomas Wearmouth
 * Created on 01/10/2024
 */
public final class Configuration {
    public static final Map<Category, List<Property<?>>> properties = new HashMap<>();

    public static final General general = new General();
    public static final Rendering rendering = new Rendering();
    public static final Printer printer = new Printer();
    public static final Server server = new Server();
    public static final Debug debug = new Debug();

    public static final class General {
        public final BlockListProperty extraAirBlocks = new BlockListProperty(Names.Config.EXTRA_AIR_BLOCKS, Collections.emptyList());
        public final DirectoryProperty schematicDirectory = new DirectoryProperty(Names.Config.SCHEMATIC_DIRECTORY, Paths.get("./schematics"));

        public final BooleanProperty arrowKeyMove = new BooleanProperty(Names.Config.ARROW_KEY_MOVE, true);
        public final BooleanProperty persistSchematic = new BooleanProperty(Names.Config.PERSIST_SCHEMATIC, true);

        public final EnumProperty<ItemStackSortType> sortType = new EnumProperty<>(Names.Config.SORT_TYPE, ItemStackSortType.NAME_ASC);

        private General() {
            registerGeneral(this.extraAirBlocks);
            registerGeneral(this.schematicDirectory);

            registerGeneral(this.arrowKeyMove);
            registerGeneral(this.persistSchematic);

            registerGeneral(this.sortType);
        }

        public boolean isExtraAirBlock(final Block block) {
            return this.extraAirBlocks.getValue().contains(block);
        }

        public Path getSchematicDirectory() {
            final Path dataDir = Schematica.proxy.getDataDirectory().toPath();
            return dataDir.resolve(this.schematicDirectory.getValue());
        }
    }

    public static final class Rendering {
        public final BooleanProperty highlight = new BooleanProperty(Names.Config.HIGHLIGHT, true);
        public final BooleanProperty highlightAir = new BooleanProperty(Names.Config.HIGHLIGHT_AIR, true);

        public final BooleanProperty enableAlpha = new BooleanProperty(Names.Config.ALPHA_ENABLED, false);
        public final DoubleProperty alphaModifier = new DoubleProperty(Names.Config.ALPHA, 1.0D, 0.1D, 1.0D, 1);

        public final DoubleProperty blockDelta = new DoubleProperty(Names.Config.BLOCK_DELTA, 0.005D, 0.0D, 0.2D, 3);
        public final IntegerProperty renderDistance = new IntegerProperty(Names.Config.RENDER_DISTANCE, 8, 2, 16, " chunks");

        private Rendering() {
            registerRendering(this.highlight);
            registerRendering(this.highlightAir);

            registerRendering(this.enableAlpha);
            registerRendering(this.alphaModifier);

            registerRendering(this.blockDelta);
            registerRendering(this.renderDistance);
        }
    }

    public static final class Printer {
        public final BooleanProperty placeInstantly = new BooleanProperty(Names.Config.PLACE_INSTANTLY, false);
        public final BooleanProperty placeAdjacent = new BooleanProperty(Names.Config.PLACE_ADJACENT, true);
        public final BooleanProperty destroyBlocks = new BooleanProperty(Names.Config.DESTROY_BLOCKS, false);
        public final BooleanProperty destroyInstantly = new BooleanProperty(Names.Config.DESTROY_INSTANTLY, false);

        public final IntegerProperty placeDelay = new IntegerProperty(Names.Config.PLACE_DELAY, 1, 0, 20, " ticks");
        public final IntegerProperty placeDistance = new IntegerProperty(Names.Config.PLACE_DISTANCE, 5, 1, 5, " blocks");
        public final IntegerProperty timeout = new IntegerProperty(Names.Config.TIMEOUT, 10, 0, 100, " ticks");

        public final BooleanProperty changeState = new BooleanProperty(Names.Config.CHANGE_STATE, true);
        public final IntegerProperty changeStateTimeout = new IntegerProperty(Names.Config.CHANGE_STATE_TIMEOUT, 5, 0, 50, " ticks");

        public final SwapSlotsProperty swapSlots = new SwapSlotsProperty(Names.Config.SWAP_SLOTS, new boolean[]{
                false, false, false, false, false, true, true, true, true
        });

        private Printer() {
            registerPrinter(this.placeInstantly);
            registerPrinter(this.placeAdjacent);
            registerPrinter(this.destroyBlocks);
            registerPrinter(this.destroyInstantly);

            registerPrinter(this.placeDelay);
            registerPrinter(this.placeDistance);
            registerPrinter(this.timeout);

            registerPrinter(this.changeState);
            registerPrinter(this.changeStateTimeout);

            registerPrinter(this.swapSlots);
        }
    }

    public static final class Server {
        public final BooleanProperty loadEnabled = new BooleanProperty(Names.Config.LOAD_ENABLED, true);
        public final BooleanProperty saveEnabled = new BooleanProperty(Names.Config.SAVE_ENABLED, true);
        public final BooleanProperty printerEnabled = new BooleanProperty(Names.Config.PRINTER_ENABLED, true);
        public final IntegerProperty playerQuota = new IntegerProperty(Names.Config.PLAYER_QUOTA_KILOBYTES, 8192, 1024, 32768, "kB");

        private Server() {
            registerServer(this.loadEnabled);
            registerServer(this.saveEnabled);
            registerServer(this.printerEnabled);
            registerServer(this.playerQuota);
        }
    }

    public static final class Debug {
        public final BooleanProperty showDebugInfo = new BooleanProperty(Names.Config.SHOW_DEBUG_INFO, true);
        public final BooleanProperty dumpBlockList = new BooleanProperty(Names.Config.DUMP_BLOCK_LIST, false);

        private Debug() {
            registerDebug(this.showDebugInfo);
            registerDebug(this.dumpBlockList);
        }
    }

    private static void registerGeneral(final Property<?> property) {
        register(Category.GENERAL, property);
    }

    private static void registerRendering(final Property<?> property) {
        register(Category.RENDERING, property);
    }

    private static void registerPrinter(final Property<?> property) {
        register(Category.PRINTER, property);
    }

    private static void registerServer(final Property<?> property) {
        register(Category.SERVER, property);
    }

    private static void registerDebug(final Property<?> property) {
        register(Category.DEBUG, property);
    }

    private static void register(final Category category, final Property<?> property) {
        properties.computeIfAbsent(category, (x) -> new ArrayList<>()).add(property);
    }
}
