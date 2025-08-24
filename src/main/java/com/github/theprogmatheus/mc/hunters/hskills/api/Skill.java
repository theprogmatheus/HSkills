package com.github.theprogmatheus.mc.hunters.hskills.api;

import org.bukkit.event.Listener;

public interface Skill extends Listener {

    SkillType getType();

}
