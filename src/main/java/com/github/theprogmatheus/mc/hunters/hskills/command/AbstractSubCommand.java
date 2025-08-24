package com.github.theprogmatheus.mc.hunters.hskills.command;

import lombok.Getter;

@Getter
public class AbstractSubCommand extends AbstractCommand {

    private final AbstractCommand parent;

    public AbstractSubCommand(AbstractCommand parent, String name, String usage, String... aliases) {
        super(
                name,
                parent.getPermission().concat(".").concat(name),
                parent.getUsage().concat(" ").concat(usage),
                aliases
        );
        this.parent = parent;
    }
}