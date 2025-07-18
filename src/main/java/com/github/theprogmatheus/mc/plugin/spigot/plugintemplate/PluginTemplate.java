package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate;

import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.Injector;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.service.MainService;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

@Getter
public class PluginTemplate extends JavaPlugin {

    private Injector injector;
    private MainService mainService;

    @Override
    public void onLoad() {
        this.injector = new Injector();
    }

    @Override
    public void onEnable() {
        this.registerInjectorSingletons();
        this.mainService = this.injector.getInstance(MainService.class);
        this.mainService.startup();
    }

    @Override
    public void onDisable() {
        this.mainService.shutdown();
    }

    private void registerInjectorSingletons() {
        this.injector.registerSingleton(new Class[]{JavaPlugin.class, Plugin.class, PluginTemplate.class}, this);
        this.injector.registerSingleton(Logger.class, getLogger());
    }
}
