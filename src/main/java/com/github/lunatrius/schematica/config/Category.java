package com.github.lunatrius.schematica.config;

import com.github.lunatrius.schematica.reference.Names;

/**
 * @author Thomas Wearmouth
 * Created on 03/10/2024
 */
public enum Category {
    GENERAL(Names.Config.Category.GENERAL),
    RENDERING(Names.Config.Category.RENDER),
    PRINTER(Names.Config.Category.PRINTER),
    SERVER(Names.Config.Category.SERVER),
    DEBUG(Names.Config.Category.DEBUG);

    private final String id;

    Category(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getTranslationKey() {
        return Names.Config.Category.LANG_PREFIX + "." + this.id;
    }
}
