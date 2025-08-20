package com.github.theprogmatheus.mc.hunters.hskills.event;

import com.github.theprogmatheus.mc.hunters.hskills.api.PlayerData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerGainExpEvent extends CustomEvent {

    private final PlayerData playerData;
    private final double expGained;

}