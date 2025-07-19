package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.config.ConfigurationManager.LOG_FORMAT;

@RequiredArgsConstructor
@Getter
public class ConfigurationFile extends YamlConfiguration {


    private final Logger logger;
    private final File file;
    private final String resourcePath;


    public ConfigurationFile createIfNotExistsAndLoad() {
        return createIfNotExists().load();
    }

    public ConfigurationFile createIfNotExists() {
        if (this.file.exists())
            return this;

        var parent = this.file.getParentFile();
        if (parent != null && !parent.exists())
            parent.mkdirs();

        var classLoader = getClass().getClassLoader();
        var resourceName = getResourceName();

        try (var resourceStream = classLoader.getResourceAsStream(resourceName)) {
            if (resourceStream == null)
                throw new FileNotFoundException("Resource %s not found in the JarFile.".formatted(resourceName));


            Files.copy(resourceStream, this.file.toPath());
        } catch (Exception e) {
            log("Failed to create file: %s".formatted(this.file.getPath()), e);
        }
        return this;
    }

    public boolean existsDefaultResource() {
        return getClass().getClassLoader().getResource(getResourceName()) != null;
    }


    public String getResourceName() {
        return this.resourcePath == null || this.resourcePath.isBlank() ? this.file.getName() : this.resourcePath;
    }

    public ConfigurationFile load() {
        var filePath = this.file.getPath();

        log("Loading configuration file: %s".formatted(filePath));
        try {
            this.load(this.file);
            log("Configuration file loaded successfully: %s".formatted(filePath));
        } catch (IOException | InvalidConfigurationException e) {
            log("Failed to load this file: %s".formatted(filePath), e);
        }

        return this;
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
