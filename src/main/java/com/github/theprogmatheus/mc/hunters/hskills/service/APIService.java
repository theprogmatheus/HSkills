package com.github.theprogmatheus.mc.hunters.hskills.service;

import com.github.theprogmatheus.mc.hunters.hskills.api.impl.PlayerDataManagerImpl;
import com.github.theprogmatheus.mc.hunters.hskills.api.impl.SkillManagerImpl;
import com.github.theprogmatheus.mc.hunters.hskills.lib.PluginService;
import com.github.theprogmatheus.mc.hunters.hskills.lib.invflow.InvFlow;
import com.github.theprogmatheus.mc.hunters.hskills.lib.invflow.example.MenuTest;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class APIService extends PluginService {


    private final JavaPlugin plugin;
    private PlayerDataManagerImpl playerDataManager;
    private SkillManagerImpl skillManager;
    private InvFlow invFlow;

    public APIService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void startup() {
        this.playerDataManager = new PlayerDataManagerImpl(this.plugin);
        this.skillManager = new SkillManagerImpl(this.plugin);
        this.invFlow = new InvFlow(this.plugin)
                .with(new MenuTest())
                .register();
    }

    @Override
    public void shutdown() {
        this.playerDataManager.getWriteBehindBuffer().close();
        this.invFlow.unregister();
    }
}
