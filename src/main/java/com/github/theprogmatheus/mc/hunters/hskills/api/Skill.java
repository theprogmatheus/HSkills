package com.github.theprogmatheus.mc.hunters.hskills.api;

import com.google.common.collect.ImmutableList;
import org.bukkit.event.Event;

public interface Skill {

    SkillType getType();

    ImmutableList<SkillHandler<? extends Event>> getHandlers();
}
