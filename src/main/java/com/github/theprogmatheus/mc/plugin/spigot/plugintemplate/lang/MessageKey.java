package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lang;

public enum MessageKey {

    PREFIX("prefix"), COMMAND_TEMPLATE("command.template");

    private final String path;
    private final boolean list;


    private MessageKey(String path) {
        this(path, false);
    }

    private MessageKey(String path, boolean list) {
        this.path = path;
        this.list = list;
    }

    public String getPath() {
        return path;
    }

    public boolean isList() {
        return list;
    }
}
