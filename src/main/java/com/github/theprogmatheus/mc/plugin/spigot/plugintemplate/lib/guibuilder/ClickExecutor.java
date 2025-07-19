package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.guibuilder;

import org.bukkit.entity.Player;

public interface ClickExecutor {

    /**
     * Executor for player click on menu.
     *
     * @param player        - Who clicked
     * @param menu          - The current menu
     * @param clickedButton - The clicked button, can be null
     */
    public abstract void execute(Player player, Menu menu, Button clickedButton);

}
