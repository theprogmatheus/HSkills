package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.service;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.Locales;
import co.aikar.commands.PaperCommandManager;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.PluginTemplate;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.command.TemplateCommand;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.PluginService;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.Injector;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
public class CommandService extends PluginService {

    private final PluginTemplate plugin;
    private final Injector injector;
    private PaperCommandManager commandManager;

    /**
     * Register your commands here
     */
    private void registerAllCommands() {
        registerCommand(TemplateCommand.class);
    }

    private void unregisterAllCommands() {
        this.commandManager.unregisterCommands();
    }


    @Override
    public void startup() {
        this.commandManager = new PaperCommandManager(this.plugin);
        this.commandManager.usePerIssuerLocale(false, false);
        this.commandManager.getLocales().setDefaultLocale(Locales.PORTUGUESE); // Or another language

        registerAllCommands();
    }

    @Override
    public void shutdown() {
        unregisterAllCommands();
    }

    private void registerCommand(Class<? extends BaseCommand> commandClass) {
        this.commandManager.registerCommand(this.injector.getInstance(commandClass));
    }
}
