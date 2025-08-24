package com.github.theprogmatheus.mc.hunters.hskills.listener;

import com.github.theprogmatheus.mc.hunters.hskills.api.HSkillsAPI;
import com.github.theprogmatheus.mc.hunters.hskills.api.PlayerData;
import com.github.theprogmatheus.mc.hunters.hskills.event.PlayerGainExpEvent;
import com.github.theprogmatheus.mc.hunters.hskills.event.PlayerLevelUpEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.meta.FireworkMeta;

public class PlayerSkillsListener implements Listener {


    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;
        Player player = event.getPlayer();
        HSkillsAPI.getPlayerDataManager()
                .getOrCreatePlayerData(player).addExp(3);
    }

    @EventHandler
    public void onPlayerGainExp(PlayerGainExpEvent event) {
        PlayerData playerData = event.getPlayerData();
        Player player = Bukkit.getPlayer(playerData.getId());
        if (player != null) {

            int xpGained = (int) event.getExpGained();
            int currentXp = (int) playerData.getExp();
            int expToNextLevel = (int) playerData.getXpToNextLevel();
            int currentLevel = playerData.getLevel();


            TextComponent message = new TextComponent(
                    String.format("§a+%sXP §7%s/%s §8[%s]",
                            xpGained,
                            currentXp,
                            expToNextLevel,
                            currentLevel
                    )
            );

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
        }
    }

    @EventHandler
    public void onPlayerLevelUp(PlayerLevelUpEvent event) {
        PlayerData playerData = event.getPlayerData();
        Player player = Bukkit.getPlayer(playerData.getId());
        if (player != null) {
            player.sendMessage("§aParabéns! você upou para o nível %s".formatted(event.getNewLevel()));
            fireWork(player);
        }
    }

    private void fireWork(Player player) {
        // Obtenha a localização do jogador
        Location loc = player.getLocation();

        // Crie uma entidade de Firework
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, org.bukkit.entity.EntityType.FIREWORK);

        // Obtenha o FireworkMeta para configurar o fogo de artifício
        FireworkMeta fwm = fw.getFireworkMeta();

        // Crie um efeito de fogo de artifício com cores aleatórias
        FireworkEffect effect = FireworkEffect.builder()
                .flicker(true)
                .withColor(Color.RED, Color.AQUA) // Você pode adicionar quantas cores quiser
                .withFade(Color.WHITE, Color.ORANGE) // Cores que o fogo de artifício irá "desbotar"
                .with(FireworkEffect.Type.BALL) // Tipo do fogo (BALL, STAR, BURST, etc.)
                .trail(true)
                .build();

        // Adicione o efeito ao FireworkMeta
        fwm.addEffect(effect);

        // Defina a força (poder) do fogo de artifício.
        // Isso determina a altura que ele irá explodir.
        fwm.setPower(1);

        // Aplique a meta configurada ao fogo de artifício
        fw.setFireworkMeta(fwm);
    }


}
