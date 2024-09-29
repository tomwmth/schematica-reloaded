package com.github.lunatrius.core.exceptions;

import net.minecraft.client.resources.I18n;

public class LocalizedException extends Exception {
    public LocalizedException(final String format) {
        super(I18n.format(format));
    }

    public LocalizedException(final String format, final Object... arguments) {
        super(I18n.format(format, arguments));
    }
}
