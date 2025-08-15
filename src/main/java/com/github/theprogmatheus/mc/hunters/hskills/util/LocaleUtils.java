package com.github.theprogmatheus.mc.hunters.hskills.util;

import java.util.Locale;

public class LocaleUtils {

    public static Locale getLocaleByString(String localeString) {
        try {
            var localeStringParts = localeString.split("_");
            return switch (localeStringParts.length) {
                case 1 -> new Locale(localeStringParts[0]);
                case 2 -> new Locale(localeStringParts[0], localeStringParts[1]);
                case 3 -> new Locale(localeStringParts[0], localeStringParts[1], localeStringParts[2]);
                default -> null;
            };
        } catch (Exception ignored) {
            return null;
        }
    }
}
