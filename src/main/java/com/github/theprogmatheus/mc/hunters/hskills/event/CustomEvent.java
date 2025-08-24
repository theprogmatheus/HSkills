package com.github.theprogmatheus.mc.hunters.hskills.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;

public abstract class CustomEvent extends Event {

    protected static final Map<Class<?>, HandlerList> handlerLists = new HashMap<>();

    @Override
    public HandlerList getHandlers() {
        return handlerLists.computeIfAbsent(getClass(), key -> new HandlerList());
    }

}