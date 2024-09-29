package com.github.lunatrius.schematica.reference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Reference {
    public static final String MOD_ID = "${modId}";
    public static final String MOD_NAME = "${modName}";
    public static final String MOD_VERSION = "${modVersion}";
    public static final String FORGE_VERSION = "${forgeVersion}";
    public static final String MINECRAFT_VERSION = "${minecraftVersion}";
    public static final String PROXY_SERVER = "com.github.lunatrius.schematica.proxy.ServerProxy";
    public static final String PROXY_CLIENT = "com.github.lunatrius.schematica.proxy.ClientProxy";
    public static final String GUI_FACTORY = "com.github.lunatrius.schematica.client.gui.config.GuiFactory";

    public static Logger logger = LogManager.getLogger(Reference.MOD_ID);
}
