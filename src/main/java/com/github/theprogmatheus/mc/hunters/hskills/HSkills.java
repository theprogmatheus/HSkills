package com.github.theprogmatheus.mc.hunters.hskills;

import com.github.theprogmatheus.mc.hunters.hskills.service.MainService;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class HSkills extends JavaPlugin {

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
