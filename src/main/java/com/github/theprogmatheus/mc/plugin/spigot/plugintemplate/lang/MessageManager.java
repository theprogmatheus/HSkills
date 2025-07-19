package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lang;

import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.config.ConfigurationFile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.config.ConfigurationFile.LOG_FORMAT;

@RequiredArgsConstructor
@Getter
@Setter
public class MessageManager {

    private final Logger logger;
    private final File folder;
    private final String resourcePath;
    private final Locale defaultLocale;
    private final Map<Locale, MessageFile> langs = new ConcurrentHashMap<>();
    private boolean individualLang = true;

    public void loadLanguages() {
        for (var locale : Locale.getAvailableLocales()) {
            var lang = locale.toString();

            try {
                var file = new File(folder, "%s.yml".formatted(lang));
                var messageFile = new MessageFile(logger, file, "%s/%s.yml".formatted(resourcePath, lang));

                if (!messageFile.existsDefaultResource())
                    continue;

                messageFile.createIfNotExistsAndLoad();

                langs.put(locale, messageFile);
                log("Message lang loaded successfully: %s from %s".formatted(lang, messageFile.getResourceName()));
            } catch (Exception e) {
                log("Failed to load message lang: %s. Cause: %s".formatted(lang, e.getMessage()));
            }
        }
        if (getDefaultMessageFile() == null)
            log("The default message lang cant be null.", new NullPointerException("getDefaultMessageFile() is null"));
    }

    /**
     * You can change this if you need
     */
    private Locale getPlayerLocale(Player player) {
        return getPlayerLocaleByClient(player);
    }

    private Locale getPlayerLocaleByClient(Player player) {
        try {
            var localeString = player.getLocale();
            var localeStringParts = localeString.split("_");

            return switch (localeStringParts.length) {
                case 1 -> new Locale(localeStringParts[0]);
                case 2 -> new Locale(localeStringParts[0], localeStringParts[1]);
                case 3 -> new Locale(localeStringParts[0], localeStringParts[1], localeStringParts[2]);
                default -> getDefaultLocale();
            };
        } catch (Exception ignored) {
            return getDefaultLocale();
        }
    }

    public MessageFile getMessageFile(Player player) {
        if (!individualLang)
            return getDefaultMessageFile();
        return getMessageFile(getPlayerLocale(player));
    }

    public MessageFile getMessageFile(Locale locale) {
        var messageFile = langs.get(locale);
        return messageFile == null ? getDefaultMessageFile() : messageFile;
    }

    public MessageFile getDefaultMessageFile() {
        return this.langs.get(this.defaultLocale);
    }

    public void reload(Locale locale) {
        var config = langs.get(locale);
        if (config != null)
            config.load();
    }

    public void reloadAll() {
        langs.values().forEach(ConfigurationFile::load);
    }

    private void log(String message) {
        log(message, null);
    }

    private void log(String message, Throwable cause) {
        if (cause == null)
            this.logger.info(LOG_FORMAT.formatted(message));
        else {
            this.logger.severe(LOG_FORMAT.formatted(message));
            Stream.of(cause.getStackTrace()).map(StackTraceElement::toString).forEach(line -> this.logger.severe("  at ".concat(line)));
        }
    }
}