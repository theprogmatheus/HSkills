package com.github.theprogmatheus.mc.hunters.hskills.lib.invflow;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Data
public class InvFlow implements Listener {

    private final JavaPlugin plugin;
    private final Map<Class<? extends FlowMenu>, FlowMenu> menus = new ConcurrentHashMap<>();

    public InvFlow with(FlowMenu flowMenu) {
        flowMenu.init();
        this.menus.put(flowMenu.getClass(), flowMenu);
        return this;
    }

    public InvFlow register() {
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
        return this;
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    public void open(Player player, Class<? extends FlowMenu> menuClass) {
        FlowMenu menu = menus.get(menuClass);
        Objects.requireNonNull(menu, "You need to register an instance of the %s menu first.".formatted(menuClass.getName()));

        createInventory(menu).openTo(player);
    }

    private FlowInventory createInventory(FlowMenu menu) {
        FlowInventory flowInventory = new FlowInventory(menu);

        Inventory inventory = flowInventory.getInventory();

        for (Map.Entry<Integer, FlowButton> entry : menu.getButtonsMap().entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItemStack());
        }

        return flowInventory;
    }


    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof FlowInventory flowInventory) {
            event.setCancelled(true);

            if (!(event.getWhoClicked() instanceof Player player))
                return;

            Consumer<ClickContext> consumer = null;
            if (inventory.equals(event.getClickedInventory())) {
                ItemStack item = event.getCurrentItem();
                if (item != null) {
                    FlowButton button = flowInventory.getMenu().getButtonsMap().get(event.getSlot());
                    if (button != null) {
                        consumer = button.getConsumer();
                    }
                }
            } else if (event.getClickedInventory() == null) {
                consumer = flowInventory.getMenu().getNullConsumer();
            }

            if (consumer != null) {
                consumer.accept(new ClickContext(player, event));
            }

            flowInventory.getMenu().onInventoryClick(event);
        }
    }

    @EventHandler
    private void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof FlowInventory flowInventory) {
            flowInventory.getMenu().onInventoryOpen(event);
        }
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof FlowInventory flowInventory) {
            flowInventory.getMenu().onInventoryClose(event);
        }
    }


}
