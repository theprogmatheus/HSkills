package com.github.theprogmatheus.mc.hunters.hskills.lib.invflow;

import lombok.Data;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public abstract class Context {
    protected final Player player;
    protected final Map<String, Object> values = new ConcurrentHashMap<>();
}
