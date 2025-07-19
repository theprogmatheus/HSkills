package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lang;

public enum MessageKey {

    PREFIX("prefix");

    private final String path;

    private MessageKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
