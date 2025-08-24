package com.github.theprogmatheus.mc.hunters.hskills.event;

import com.github.theprogmatheus.mc.hunters.hskills.api.PlayerData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.HandlerList;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerGainExpEvent extends CustomEvent {

    private final PlayerData playerData;
    private final double expGained;


    public static HandlerList getHandlerList() {
        return CustomEvent.handlerLists
                .computeIfAbsent(PlayerGainExpEvent.class, key -> new HandlerList());
    }
}