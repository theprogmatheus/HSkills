package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.config.env;

import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.config.Configuration;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.config.ConfigurationHolder;

/**
 * Here your set all config values from auto map
 */

@Configuration("config.yml")
public class Config {


    public static final ConfigurationHolder<String> CONFIG_VERSION = new ConfigurationHolder<>("config-version", String.class);

    public static final ConfigurationHolder<String> LANG_DEFAULT = new ConfigurationHolder<>("lang.default", String.class);
    public static final ConfigurationHolder<Boolean> LANG_INDIVIDUAL = new ConfigurationHolder<>("lang.individual", Boolean.class);


    public static final ConfigurationHolder<String> DATABASE_TYPE = new ConfigurationHolder<>("database.type", String.class);
    public static final ConfigurationHolder<String> DATABASE_PREFIX = new ConfigurationHolder<>("database.prefix", String.class);
    public static final ConfigurationHolder<String> DATABASE_HOST = new ConfigurationHolder<>("database.host", String.class);
    public static final ConfigurationHolder<String> DATABASE_DBNAME = new ConfigurationHolder<>("database.dbname", String.class);
    public static final ConfigurationHolder<String> DATABASE_USER = new ConfigurationHolder<>("database.user", String.class);
    public static final ConfigurationHolder<String> DATABASE_PASS = new ConfigurationHolder<>("database.pass", String.class);
}
