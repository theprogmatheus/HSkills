package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.guibuilder;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class MenuFactory {


    private final Plugin plugin;
    private MenuListener menuListener;

    public Menu createMenu(String title) {
        return createMenu(title, 6);
    }

    public Menu createMenu(String title, int rows) {
        return createMenu(title, rows, null);
    }

    public Menu createMenu(String title, int rows, ClickExecutor airClickExecutor) {
        checkListener();

        var menu = new Menu();
        var inventory = Bukkit.createInventory(menu, rows * 9, title);
        menu.setInventory(inventory);
        menu.setAirClickExecutor(airClickExecutor);

        return menu;
    }

    public Menu createMenu(InventoryType inventoryType) {
        checkListener();

        var menu = new Menu();
        var inventory = Bukkit.createInventory(menu, inventoryType);
        menu.setInventory(inventory);

        return menu;
    }

    public Menu createMenu(String title, InventoryType inventoryType) {
        return createMenu(title, inventoryType, null);
    }

    public Menu createMenu(String title, InventoryType inventoryType, ClickExecutor airClickExecutor) {
        checkListener();

        var menu = new Menu();
        var inventory = Bukkit.createInventory(menu, inventoryType, title);
        menu.setInventory(inventory);
        menu.setAirClickExecutor(airClickExecutor);

        return menu;
    }


    private void checkListener() {
        if (menuListener == null)
            Bukkit.getPluginManager().registerEvents(this.menuListener = new MenuListener(), this.plugin);
    }


}
