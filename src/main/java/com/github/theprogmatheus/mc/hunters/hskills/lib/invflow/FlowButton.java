package com.github.theprogmatheus.mc.hunters.hskills.lib.invflow;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

@Data
public class FlowButton {

    private final ItemStack itemStack;
    private final Consumer<ClickContext> consumer;
    private Function<ItemStack, ItemStack> itemRefreshFunction;

    private FlowButton(ItemStack itemStack, Consumer<ClickContext> consumer) {
        this.itemStack = itemStack;
        this.consumer = consumer;
    }

    public static FlowButton of(ItemStack itemStack, Consumer<ClickContext> consumer) {
        return new FlowButton(itemStack, consumer);
    }

}
