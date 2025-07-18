package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.service;

import co.aikar.commands.Locales;
import co.aikar.commands.PaperCommandManager;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.PluginTemplate;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.command.TemplateCommand;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.core.AbstractService;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.Injector;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
public class CommandService extends AbstractService {

    private final PluginTemplate plugin;
    private final Injector injector;
    private PaperCommandManager commandManager;

    @Override
    public void startup() {
        this.commandManager = new PaperCommandManager(this.plugin);
        this.commandManager.registerCommand(this.injector.getInstance(TemplateCommand.class));
        this.commandManager.usePerIssuerLocale(false, false);
        this.commandManager.getLocales().setDefaultLocale(Locales.PORTUGUESE); // Or another language
    }

    @Override
    public void shutdown() {
        this.commandManager.unregisterCommands();
    }
}
