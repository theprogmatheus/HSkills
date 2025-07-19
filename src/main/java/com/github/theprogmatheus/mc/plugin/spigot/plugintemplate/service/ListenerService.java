package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.service;

import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.PluginTemplate;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.core.AbstractService;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.Injector;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.listener.TemplateListener;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ListenerService extends AbstractService {


    private final PluginTemplate plugin;
    private final Injector injector;
    private PluginManager pluginManager;

    /**
     * Register your listeners here
     */
    private void registerAllListeners() {
        registerListener(TemplateListener.class);
    }

    private void unregisterAllListeners() {
        HandlerList.unregisterAll(this.plugin);
    }


    @Override
    public void startup() {
        this.pluginManager = Bukkit.getPluginManager();
        registerAllListeners();
    }

    @Override
    public void shutdown() {
        unregisterAllListeners();
    }

    private void registerListener(Class<? extends Listener> listenerClass) {
        this.pluginManager.registerEvents(this.injector.getInstance(listenerClass), this.plugin);
    }
}
