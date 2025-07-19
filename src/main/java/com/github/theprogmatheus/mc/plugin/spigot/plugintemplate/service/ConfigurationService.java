package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.service;

import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.PluginTemplate;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.config.ConfigurationFile;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.config.ConfigurationManager;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.PluginService;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.logging.Logger;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigurationService extends PluginService {

    private final PluginTemplate plugin;
    private final Logger logger;
    private ConfigurationManager configurationManager;


    /**
     * Register your configs here
     */
    private void registerAllConfigs() {
        registerConfig("config.yml");
    }

    @Override
    public void startup() {
        this.configurationManager = new ConfigurationManager(logger, plugin.getDataFolder());
        registerAllConfigs();
    }

    private void registerConfig(String configPath) {
        this.configurationManager.register(configPath);
    }

    public ConfigurationFile getConfig(String configName) {
        return this.configurationManager.get(configName);
    }


}
