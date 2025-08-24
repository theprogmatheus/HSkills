package com.github.theprogmatheus.mc.hunters.hskills.database.entity;

import com.github.theprogmatheus.mc.hunters.hskills.api.SkillType;

import java.util.Map;
import java.util.UUID;

public record PlayerDataEntity(UUID id, Map<SkillType, Integer> skillLevels, int level, double exp, int upgradePoints) {
}