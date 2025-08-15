package com.github.theprogmatheus.mc.hunters.hskills.lib;


import lombok.Data;

@Data
public abstract class PluginService {

    private int startupPriority;
    private int shutdownPriority;

    public void startup() {
    }

    public void shutdown() {
    }

}
