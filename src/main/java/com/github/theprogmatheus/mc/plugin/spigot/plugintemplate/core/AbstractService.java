package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.core;


import lombok.Data;

@Data
public abstract class AbstractService {

    private int startupPriority;
    private int shutdownPriority;

    public abstract void startup();

    public abstract void shutdown();

}
