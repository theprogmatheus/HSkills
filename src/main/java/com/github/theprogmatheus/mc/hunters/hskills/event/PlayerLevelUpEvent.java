package com.github.theprogmatheus.mc.hunters.hskills.event;

import com.github.theprogmatheus.mc.hunters.hskills.api.PlayerData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.HandlerList;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerLevelUpEvent extends CustomEvent {

    private final PlayerData playerData;
    private final int oldLevel;
    private final int newLevel;

    public static HandlerList getHandlerList() {
        return CustomEvent.handlerLists
                .computeIfAbsent(PlayerLevelUpEvent.class, key -> new HandlerList());
    }
}