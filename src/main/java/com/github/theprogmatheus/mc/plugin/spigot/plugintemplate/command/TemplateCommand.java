package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.PluginTemplate;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
@CommandAlias("template")
public class TemplateCommand extends BaseCommand {

    private final PluginTemplate plugin;

    @Default
    void onDefault(Player player) {
        player.sendMessage("§aTestado com sucesso. Plugin: " + plugin.getName());
    }

}
