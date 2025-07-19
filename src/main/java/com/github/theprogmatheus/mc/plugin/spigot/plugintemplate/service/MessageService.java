package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.service;

import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.PluginTemplate;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lang.MessageKey;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lang.MessageManager;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.PluginService;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.util.ArrayUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
@Getter
public class MessageService extends PluginService {


    private final PluginTemplate plugin;
    private final Logger logger;
    private MessageManager messageManager;


    @Override
    public void startup() {
        this.messageManager = new MessageManager(
                this.logger,
                new File(plugin.getDataFolder(), "lang"),
                "lang",
                new Locale("pt", "BR")
        );
        this.messageManager.loadLanguages();
    }

    public void sendMessage(CommandSender sender, MessageKey messageKey) {
        sendMessage(sender, messageKey, new String[0]);
    }

    public void sendMessage(CommandSender sender, MessageKey messageKey, String... arrayPlaceholders) {
        sendMessage(sender, messageKey, ArrayUtils.toMap(arrayPlaceholders));
    }

    public void sendMessage(CommandSender sender, MessageKey messageKey, Map<String, String> placeholders) {
        var messageFile = this.messageManager.getDefaultMessageFile();
        if (sender instanceof Player player)
            messageFile = this.messageManager.getMessageFile(player);

        List<String> message = null;
        if (messageKey.isList())
            message = messageFile.getMessageList(messageKey, placeholders);
        else {
            var rawMessage = messageFile.getMessage(messageKey, placeholders);
            if (rawMessage != null)
                message = List.of(rawMessage);
        }

        if (message != null)
            message.forEach(sender::sendMessage);
    }
}
