package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lang;

import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.config.ConfigurationFile;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.util.LocaleUtils;
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

import static com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.config.ConfigurationManager.LOG_FORMAT;

@RequiredArgsConstructor
@Getter
@Setter
public class MessageManager {

    private final Logger logger;
    private final File folder;
    private final String resourcePath;
    private final Locale defaultLocale;
    private final Map<String, MessageFile> langs = new ConcurrentHashMap<>();
    private boolean individualLang = true;

    public void loadLanguages() {
        for (var locale : Locale.getAvailableLocales()) {
            var lang = normalizedTag(locale);
            try {
                var file = new File(folder, "%s.yml".formatted(lang));
                var messageFile = new MessageFile(logger, file, "%s/%s.yml".formatted(resourcePath, lang));
                if (!messageFile.existsDefaultResource())
                    continue;

                langs.put(lang, (MessageFile) messageFile.createIfNotExistsAndLoad());

                log("Message lang loaded successfully: %s (%s) from %s".formatted(lang, locale.getDisplayLanguage(Locale.ENGLISH), messageFile.getResourceName()));
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
        logger.info("getPlayerLocaleByClient(%s) -> %s".formatted(player.getName(), player.getLocale()));
        var locale = LocaleUtils.getLocaleByString(player.getLocale());
        return locale == null ? getDefaultLocale() : locale;
    }

    public MessageFile getMessageFile(Player player) {
        if (!individualLang)
            return getDefaultMessageFile();
        return getMessageFile(getPlayerLocale(player));
    }

    public MessageFile getMessageFile(Locale locale) {
        var key = normalizedTag(locale);

        if (key.isEmpty())
            return getDefaultMessageFile();

        var messageFile = this.langs.get(key);
        if (messageFile != null)
            return messageFile;

        var lang = locale.getLanguage().toLowerCase();

        messageFile = langs.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("%s_".formatted(lang)))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);

        return messageFile != null ? messageFile : getDefaultMessageFile();
    }

    public MessageFile getDefaultMessageFile() {
        return this.langs.get(normalizedTag(this.defaultLocale));
    }

    public void reload(Locale locale) {
        var config = langs.get(normalizedTag(locale));
        if (config != null)
            config.load();
    }

    public void reloadAll() {
        langs.values().forEach(ConfigurationFile::load);
    }

    private String normalizedTag(Locale locale) {
        if (locale == null) return "";
        return locale.toString().toLowerCase();
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