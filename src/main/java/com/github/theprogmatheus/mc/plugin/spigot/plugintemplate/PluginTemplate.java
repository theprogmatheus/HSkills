package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate;

import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.service.MainService;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PluginTemplate extends JavaPlugin {

    private MainService mainService;

    @Override
    public void onLoad() {
        this.mainService = new MainService(this);
    }

    @Override
    public void onEnable() {
        this.mainService.startup();
    }

    @Override
    public void onDisable() {
        this.mainService.shutdown();
    }

}
