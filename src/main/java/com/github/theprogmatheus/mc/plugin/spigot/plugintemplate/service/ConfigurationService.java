package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.service;

import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.PluginTemplate;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.config.ConfigurationManager;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.config.env.Config;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.PluginService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.logging.Logger;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
@Getter
public class ConfigurationService extends PluginService {

    private final PluginTemplate plugin;
    private final Logger logger;
    private ConfigurationManager configurationManager;


    /**
     * Register your configs here
     */
    private ConfigurationManager configureAllConfigs() {
        return this.configurationManager
                .addConfigurationClass(Config.class);
    }

    @Override
    public void startup() {
        this.configurationManager = new ConfigurationManager(logger, plugin.getDataFolder());
        configureAllConfigs()
                .mapConfigurationClasses();
    }
}
