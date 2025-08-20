package com.github.theprogmatheus.mc.hunters.hskills.service;

import com.github.theprogmatheus.mc.hunters.hskills.api.PlayerDataManager;
import com.github.theprogmatheus.mc.hunters.hskills.api.impl.PlayerDataManagerImpl;
import com.github.theprogmatheus.mc.hunters.hskills.lib.PluginService;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

@Getter
public class APIService extends PluginService {


    private final Plugin plugin;
    private PlayerDataManager playerDataManager;

    public APIService(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void startup() {
        this.playerDataManager = new PlayerDataManagerImpl(this.plugin);
    }
}
