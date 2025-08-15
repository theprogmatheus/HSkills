package com.github.theprogmatheus.mc.hunters.hskills.command;

import lombok.Data;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

@Data
public abstract class AbstractCommand implements CommandExecutor, TabCompleter {

    private final String name;
    private final String permission;
    private final String usage;
    private final String[] alias;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }

}
