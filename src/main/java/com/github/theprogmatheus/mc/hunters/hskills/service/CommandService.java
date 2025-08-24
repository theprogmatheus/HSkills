package com.github.theprogmatheus.mc.hunters.hskills.service;

import com.github.theprogmatheus.mc.hunters.hskills.command.UpgradeCommand;
import com.github.theprogmatheus.mc.hunters.hskills.lib.PluginService;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;


@RequiredArgsConstructor
public class CommandService extends PluginService {

    private final JavaPlugin plugin;

    @Override
    public void startup() {
        new UpgradeCommand().register(this.plugin);
    }
}
