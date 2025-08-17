package com.github.theprogmatheus.mc.hunters.hskills.api;

/**
 * @param skill
 * @param multiplier
 * @param duration
 */
public record Booster(Skill skill, double multiplier, long duration) {
}