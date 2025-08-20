package com.github.theprogmatheus.mc.hunters.hskills.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;

public abstract class CustomEvent extends Event {

    private static final Map<Class<?>, HandlerList> handlerLists = new HashMap<>();

    @Override
    public HandlerList getHandlers() {
        var handlerList = handlerLists.get(getClass());
        if (handlerList == null)
            handlerLists.put(getClass(), handlerList = new HandlerList());
        return handlerList;
    }

}