package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.guibuilder;

import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@Data
public class Menu implements InventoryHolder {

    private final MenuConfig config;
    private Inventory inventory;

    Menu(MenuConfig config) {
        this.config = config;
    }


    public void show(Player player) {
        player.openInventory(this.inventory);
    }


    /**
     * Get the object's inventory.
     *
     * @return The inventory.
     */
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }


}
