package com.github.theprogmatheus.mc.hunters.hskills.lib.invflow;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

@Getter
public class ClickContext extends Context {

    private final InventoryClickEvent event;
    private final ClickType clickType;

    public ClickContext(Player player, InventoryClickEvent event) {
        super(player);
        this.event = event;
        this.clickType = event.getClick();
    }
}
