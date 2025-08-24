package com.github.theprogmatheus.mc.hunters.hskills.api;

/**
 * @param skillType
 * @param multiplier
 * @param duration
 */
public record Booster(SkillType skillType, double multiplier, long duration) {
}