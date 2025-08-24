package com.github.theprogmatheus.mc.hunters.hskills.service;

import com.github.theprogmatheus.mc.hunters.hskills.lib.PluginService;
import com.github.theprogmatheus.mc.hunters.hskills.listener.PlayerJoinQuitListener;
import com.github.theprogmatheus.mc.hunters.hskills.listener.PlayerSkillsListener;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class ListenerService extends PluginService {

    private final Plugin plugin;

    @Override
    public void startup() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoinQuitListener(), this.plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerSkillsListener(), this.plugin);
    }

    @Override
    public void shutdown() {
        HandlerList.unregisterAll(this.plugin);
    }
}
