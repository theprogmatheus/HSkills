package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.listener.lang;

import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.service.CommandService;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PlayerJoinLangConfig implements Listener {

    private final CommandService commandService;
    private final MessageService messageService;

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        commandService.getCommandManager()
                .setIssuerLocale(player, messageService.getMessageManager().getPlayerLocale(player));
    }

}
