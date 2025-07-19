package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.guibuilder;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

@RequiredArgsConstructor
@Data
public abstract class MenuConfig {

    /**
     * Essa clase vai "definir" o menu.
     * <p>
     * suas configurações, como tempo de atualização, posição dos íncones etc...
     */

    private final String title;
    private final InventoryType inventoryType;
    private ClickExecutor airClickExecutor;


    public void config() {

    }

    public void addButton(int slot, Button button) {

    }


    protected void configure(Inventory inventory) {

    }


}
