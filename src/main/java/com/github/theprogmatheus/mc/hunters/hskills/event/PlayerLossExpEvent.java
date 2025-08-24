package com.github.theprogmatheus.mc.hunters.hskills.event;

import com.github.theprogmatheus.mc.hunters.hskills.api.PlayerData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.HandlerList;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerLossExpEvent extends CustomEvent {

    private final PlayerData playerData;
    private final double expLoss;

    public static HandlerList getHandlerList() {
        return CustomEvent.handlerLists
                .computeIfAbsent(PlayerLossExpEvent.class, key -> new HandlerList());
    }

}