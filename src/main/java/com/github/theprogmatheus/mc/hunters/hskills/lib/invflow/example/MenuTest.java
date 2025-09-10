package com.github.theprogmatheus.mc.hunters.hskills.lib.invflow.example;

import com.github.theprogmatheus.mc.hunters.hskills.lib.invflow.ClickContext;
import com.github.theprogmatheus.mc.hunters.hskills.lib.invflow.FlowMenu;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class MenuTest extends FlowMenu {

    @Override
    public void init() {
        setTitle("Menu de Teste");
        setSize(6);
        setInventoryType(InventoryType.CHEST);

        addButton(new ItemStack(Material.STONE), this::onClickStone);
        setNullConsumer(this::onClickNull);
    }


    private ItemStack showRankIcon() {
        return new ItemStack(Material.DIAMOND);
    }

    private void onClickStone(ClickContext context) {
        context.getPlayer().sendMessage("§aVocê clicou na PREDA!");
    }

    private void onClickNull(ClickContext context) {

        context.getPlayer().sendMessage("§aVocê clicou em nulo.");
    }


}
