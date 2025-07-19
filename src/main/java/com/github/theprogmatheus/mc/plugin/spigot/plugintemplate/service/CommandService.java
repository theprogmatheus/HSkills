package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.service;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.Locales;
import co.aikar.commands.PaperCommandManager;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.PluginTemplate;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.command.TemplateCommand;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.config.env.Config;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.Injector;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.PluginService;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.util.LocaleUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Locale;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
@Getter
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
        this.commandManager.usePerIssuerLocale(Config.LANG_INDIVIDUAL.getValue(), false);
        this.commandManager.getLocales().setDefaultLocale(getDefaultLocale());
        registerAllCommands();
    }

    private Locale getDefaultLocale() {
        var defaultLocale = LocaleUtils.getLocaleByString(Config.LANG_DEFAULT.getValue());
        if (defaultLocale == null)
            defaultLocale = Locales.ENGLISH;
        return defaultLocale;
    }

    @Override
    public void shutdown() {
        unregisterAllCommands();
    }

    private void registerCommand(Class<? extends BaseCommand> commandClass) {
        this.commandManager.registerCommand(this.injector.getInstance(commandClass));
    }
}
