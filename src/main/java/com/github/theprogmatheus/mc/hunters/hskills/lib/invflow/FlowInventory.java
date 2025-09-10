package com.github.theprogmatheus.mc.hunters.hskills.lib.invflow;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@Getter
public class FlowInventory implements InventoryHolder {

    private final FlowMenu menu;
    private final Inventory inventory;

    public FlowInventory(FlowMenu menu) {
        this.menu = menu;

        int size = menu.getSize();
        if (size < 9) {
            size *= 9;
        }

        if (menu.getInventoryType() != InventoryType.CHEST) {
            if (menu.getTitle() != null) {
                this.inventory = Bukkit.createInventory(this, menu.getInventoryType(), menu.getTitle());
            } else {
                this.inventory = Bukkit.createInventory(this, menu.getInventoryType());
            }
        } else {
            if (menu.getTitle() != null) {
                this.inventory = Bukkit.createInventory(this, size, menu.getTitle());
            } else {
                this.inventory = Bukkit.createInventory(this, size);
            }
        }
    }


    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public void openTo(Player player) {
        player.openInventory(this.inventory);
    }

}
