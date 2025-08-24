package com.github.theprogmatheus.mc.hunters.hskills.command;

import com.github.theprogmatheus.mc.hunters.hskills.service.MainService;
import com.github.theprogmatheus.mc.hunters.hskills.service.MessageService;
import lombok.Data;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public abstract class AbstractCommand implements CommandExecutor, TabCompleter {

    private final String name;
    private final String permission;
    private final String usage;
    private final String[] aliases;
    private final Map<String, AbstractSubCommand> subCommands = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length >= 1) {
            AbstractSubCommand subCommand = this.subCommands.get(args[0].toLowerCase());
            if (subCommand != null) {
                if (sender.hasPermission(subCommand.getPermission())) {
                    return subCommand.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    sender.sendMessage("§cYou are not allowed to do that.");
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1)
            return this.subCommands.keySet().stream().filter(key -> key.startsWith(args[0].toLowerCase())).toList();

        if (args.length > 1) {
            AbstractSubCommand subCommand = this.subCommands.get(args[0].toLowerCase());
            if (subCommand != null)
                return subCommand.onTabComplete(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
        }
        return List.of();
    }

    public void addSubCommand(AbstractSubCommand subCommand) {
        this.subCommands.putIfAbsent(subCommand.getName().toLowerCase(), subCommand);
        for (String alias : subCommand.getAliases()) {
            this.subCommands.putIfAbsent(alias.toLowerCase(), subCommand);
        }
    }

    public void register(JavaPlugin plugin) {
        PluginCommand command = plugin.getCommand(this.name);
        if (command != null) {
            command.setPermission(this.permission);
            command.setUsage(this.usage);
            command.setAliases(Arrays.asList(this.aliases));
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
    }

    public MessageService getMessageService() {
        return MainService.getService(MessageService.class);
    }
}
