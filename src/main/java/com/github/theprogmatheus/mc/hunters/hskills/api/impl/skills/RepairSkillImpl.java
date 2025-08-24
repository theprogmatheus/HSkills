package com.github.theprogmatheus.mc.hunters.hskills.api.impl.skills;

import com.github.theprogmatheus.mc.hunters.hskills.api.HSkillsAPI;
import com.github.theprogmatheus.mc.hunters.hskills.api.SkillType;
import com.github.theprogmatheus.mc.hunters.hskills.api.skills.RepairSkill;
import com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;


public class RepairSkillImpl implements RepairSkill {

    private final Map<Integer, Consumer<PlayerInteractEvent>> levels =
            ImmutableMap.<Integer, Consumer<PlayerInteractEvent>>builder()
                    .put(0, this::handleLevel0)
                    .put(1, this::handleLevel1)
                    .put(2, this::handleLevel2)
                    .put(3, this::handleLevel3)
                    .put(4, this::handleLevel4)
                    .put(5, this::handleLevel5)
                    .put(6, this::handleLevel6)
                    .build();

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK
                && event.getClickedBlock() != null
                && Material.GRINDSTONE.equals(event.getClickedBlock().getType())) {
            event.setCancelled(true);

            int skillLevel = HSkillsAPI.getPlayerDataManager()
                    .getPlayerData(event.getPlayer())
                    .getSkillLevel(SkillType.REPAIR);

            this.levels.getOrDefault(skillLevel, this::handleLevel0).accept(event);
        }
    }

    private enum ToolType {

        GOLD(Material.GOLD_INGOT, Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE,
                Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS),
        IRON(Material.IRON_INGOT, Material.IRON_HELMET, Material.IRON_CHESTPLATE,
                Material.IRON_LEGGINGS, Material.IRON_BOOTS),
        DIAMOND(Material.DIAMOND, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,
                Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS),
        NETHERITE(Material.NETHERITE_INGOT, Material.NETHERITE_HELMET,
                Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS);

        private final Material repairMaterial;
        private final List<Material> armorPieces;

        ToolType(Material repairMaterial, Material... armorPieces) {
            this.repairMaterial = repairMaterial;
            this.armorPieces = Arrays.asList(armorPieces);
        }

    }

    private void repair(PlayerInteractEvent event, boolean consumeMaterials, float repairAmount, ToolType... allowedTools) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (item == null || Material.AIR.equals(item.getType())) {
            player.sendMessage(ChatColor.RED + "Você precisa estar segurando um item para reparar!");
            return;
        }

        if (item.getItemMeta() instanceof Damageable damageable) {

            ToolType toolType = Arrays.stream(allowedTools)
                    .filter(type -> type.armorPieces.contains(item.getType()))
                    .findFirst()
                    .orElse(null);

            if (toolType == null) {
                player.sendMessage(ChatColor.RED + "Você ainda não pode reparar este tipo de item!");
                return;
            }

            if (damageable.getDamage() == 0) {
                player.sendMessage(ChatColor.YELLOW + "Este item já está com durabilidade máxima!");
                return;
            }

            if (consumeMaterials) {
                Material repairMat = toolType.repairMaterial;
                if (repairMat != null) {
                    if (!player.getInventory().containsAtLeast(new ItemStack(repairMat), 1)) {
                        player.sendMessage(ChatColor.RED + "Você precisa de " + repairMat.name() + " para reparar este item!");
                        return;
                    }
                    player.getInventory().removeItem(new ItemStack(repairMat, 1));
                }
            }

            repairAmount *= item.getType().getMaxDurability();

            int damage = (int) (damageable.getDamage() - repairAmount);
            if (damage < 0)
                damage = 0;

            damageable.setDamage(damage);
            item.setItemMeta(damageable);


            // dar xp para reparação.
            HSkillsAPI.getPlayerDataManager().getPlayerData(player).addExp(35);

            player.sendMessage(ChatColor.GREEN + "Seu item foi reparado com sucesso!");
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GRINDSTONE_USE, 1f, 1f);
        }
    }


    private void handleLevel0(PlayerInteractEvent event) {
        event.getPlayer().sendMessage(ChatColor.RED + "Você ainda não desbloqueou a habilidade de Reparação!");
    }

    private void handleLevel1(PlayerInteractEvent event) {
        repair(event, true, 0.25f, ToolType.GOLD);
    }

    private void handleLevel2(PlayerInteractEvent event) {
        repair(event, true, 0.25f, ToolType.GOLD, ToolType.IRON);
    }

    private void handleLevel3(PlayerInteractEvent event) {
        repair(event, true, 0.25f, ToolType.GOLD, ToolType.IRON,
                ToolType.DIAMOND);
    }

    private void handleLevel4(PlayerInteractEvent event) {
        repair(event, true, 0.25f, ToolType.GOLD, ToolType.IRON,
                ToolType.DIAMOND, ToolType.NETHERITE);
    }

    private void handleLevel5(PlayerInteractEvent event) {
        repair(event, false, 0.25f, ToolType.GOLD, ToolType.IRON,
                ToolType.DIAMOND, ToolType.NETHERITE);
    }

    private void handleLevel6(PlayerInteractEvent event) {
        /*
         * Nível 6 -  Custo 14 - Restauração Superior - 1% de chance de restaurar 100% da durabilidade e adicionar
         * (ALGO QUE AINDA NÃO PENSEI, AUMENTAR A DURABILIDADE DA ARMADURA, AUMENTAR A PROTEÇÃO,
         * ALGO QUE COMPENSE GASTAR O TANTO DE NIVEL QUE PRECISA)
         */

        float repairAmount = 0.25f;
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        if (rand.nextInt(0, 100) <= 1) {
            repairAmount = 1f;
        }

        repair(event, false, repairAmount, ToolType.GOLD, ToolType.IRON,
                ToolType.DIAMOND, ToolType.NETHERITE);
    }

}
