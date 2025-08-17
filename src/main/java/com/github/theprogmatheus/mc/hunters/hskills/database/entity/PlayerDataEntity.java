package com.github.theprogmatheus.mc.hunters.hskills.database.entity;

import com.github.theprogmatheus.mc.hunters.hskills.api.Skill;

import java.util.Map;
import java.util.UUID;

public record PlayerData(UUID id, Map<Skill, Integer> skillLevels, int level, double exp, int upgradePoints) {
}