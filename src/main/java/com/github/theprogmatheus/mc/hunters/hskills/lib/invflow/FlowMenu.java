package com.github.theprogmatheus.mc.hunters.hskills.lib.invflow;

import lombok.Data;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Data
public abstract class FlowMenu {

    private String title;
    private int size = 6;
    private InventoryType inventoryType = InventoryType.CHEST;
    private Consumer<ClickContext> nullConsumer;
    private final Map<Integer, FlowButton> buttonsMap = new HashMap<>();

    public abstract void init();

    public FlowButton putButton(int index, FlowButton button) {
        this.buttonsMap.put(index, button);
        return button;
    }

    public FlowButton putButton(int index, ItemStack itemStack, Consumer<ClickContext> consumer) {
        return putButton(index, FlowButton.of(itemStack, consumer));
    }

    public FlowButton addButton(FlowButton button) {
        for (int i = 0; i < (size > 9 ? size : (size * 9)); i++) {
            if (!this.buttonsMap.containsKey(i)) {
                this.buttonsMap.put(i, button);
                break;
            }
        }
        return button;
    }

    public FlowButton addButton(ItemStack itemStack, Consumer<ClickContext> consumer) {
        return addButton(FlowButton.of(itemStack, consumer));
    }

    public void onInventoryClick(InventoryClickEvent event) {

    }

    public void onInventoryOpen(InventoryOpenEvent event) {

    }

    public void onInventoryClose(InventoryCloseEvent event) {

    }

}
