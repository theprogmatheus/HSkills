package com.github.theprogmatheus.mc.hunters.hskills.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
      // load playerdata
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // save playerdata
    }
}
