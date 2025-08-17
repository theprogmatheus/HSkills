package com.github.theprogmatheus.mc.hunters.hskills.api.impl;

import com.github.theprogmatheus.mc.hunters.hskills.api.PlayerData;
import com.github.theprogmatheus.mc.hunters.hskills.api.Skill;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataImpl implements PlayerData {


    private final UUID id;
    private final Map<Skill, Integer> skillLevels;
    private double exp;
    private int upgradePoints;

    public PlayerDataImpl(UUID id) {
        this.id = id;
        this.skillLevels = new ConcurrentHashMap<>();
        this.exp = 0;
        this.upgradePoints = 0;
    }


    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public double getExp() {
        return this.exp;
    }

    @Override
    public void setExp(double exp) {
        this.exp = exp;
    }

    @Override
    public void addExp(double exp) {
        this.exp += exp;
    }

    @Override
    public void removeExp(double exp) {
        this.exp -= exp;
    }

    @Override
    public int getSkillLevel(Skill skill) {
        return this.skillLevels.getOrDefault(skill, 0);
    }

    @Override
    public void setSkillLevel(Skill skill, int level) {
        this.skillLevels.put(skill, level);
    }

    @Override
    public int getUpgradePoints() {
        return this.upgradePoints;
    }

    @Override
    public void setUpgradePoints(int upgradePoints) {
        this.upgradePoints = upgradePoints;
    }

    @Override
    public void addUpgradePoints(int upgradePoints) {
        this.upgradePoints += upgradePoints;
    }

    @Override
    public void removeUpgradePoints(int upgradePoints) {
        this.upgradePoints -= upgradePoints;
    }

    @Override
    public int upgradeSkill(Skill skill) {
        int skillLevel = getSkillLevel(skill);

        if (this.upgradePoints <= 0)
            return skillLevel;

        this.upgradePoints--;

        int newSkillLevel = skillLevel + 1;
        setSkillLevel(skill, newSkillLevel);
        return newSkillLevel;
    }
}
