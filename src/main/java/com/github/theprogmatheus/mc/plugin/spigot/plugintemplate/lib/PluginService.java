package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib;


import lombok.Data;

@Data
public abstract class PluginService {

    private int startupPriority;
    private int shutdownPriority;

    public abstract void startup();

    public abstract void shutdown();

}
