package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.guibuilder;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class MenuFactory {


    private final Plugin plugin;
    private MenuListener menuListener;

    public Menu createMenu(MenuConfig config) {
        checkListener();

        var menu = new Menu(config);

        Inventory inventory = null;

        if (config instanceof MenuChestConfig chestConfig)
            inventory = Bukkit.createInventory(menu, chestConfig.getRows() * 9, config.getTitle());
        else
            inventory = Bukkit.createInventory(menu, config.getInventoryType(), config.getTitle());

        menu.setInventory(inventory);

        return menu;
    }


    private void checkListener() {
        if (menuListener == null)
            Bukkit.getPluginManager().registerEvents(this.menuListener = new MenuListener(), this.plugin);
    }


}
