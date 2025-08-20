package com.github.theprogmatheus.mc.hunters.hskills.api.impl;

import com.github.theprogmatheus.mc.hunters.hskills.api.PlayerData;
import com.github.theprogmatheus.mc.hunters.hskills.api.Skill;
import com.github.theprogmatheus.mc.hunters.hskills.event.PlayerGainExpEvent;
import com.github.theprogmatheus.mc.hunters.hskills.event.PlayerLevelUpEvent;
import com.github.theprogmatheus.mc.hunters.hskills.event.PlayerLossExpEvent;
import com.github.theprogmatheus.mc.hunters.hskills.util.WriteBehindBuffer;
import lombok.Data;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class PlayerDataImpl implements PlayerData {

    private final UUID id;
    private final Map<Skill, Integer> skillLevels;
    private double exp;
    private int level;
    private int upgradePoints;
    private WriteBehindBuffer<UUID, PlayerDataImpl> writeBehindBuffer;

    public PlayerDataImpl(
            UUID id,
            Map<Skill, Integer> skillLevels,
            double exp,
            int level,
            int upgradePoints,
            WriteBehindBuffer<UUID, PlayerDataImpl> writeBehindBuffer
    ) {
        this.id = id;
        this.skillLevels = skillLevels;
        this.exp = exp;
        this.level = level;
        this.upgradePoints = upgradePoints;
        this.writeBehindBuffer = writeBehindBuffer;
    }

    public PlayerDataImpl(UUID id) {
        this.id = id;
        this.skillLevels = new ConcurrentHashMap<>();
        this.exp = 0;
        this.level = 0;
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
        double oldExp = this.exp;
        this.exp = exp;
        if (this.exp > oldExp) {
            callGainExpEvent(this.exp - oldExp);
            this.checkLevelUP();
        } else if (this.exp < oldExp) {
            callLossExpEvent(oldExp - this.exp);
        }
        this.persist();
    }

    @Override
    public void addExp(double exp) {
        setExp(this.exp + exp);
    }

    @Override
    public void removeExp(double exp) {
        setExp(this.exp - exp);
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
        this.persist();
    }

    @Override
    public void addLevel(int level) {
        setLevel(this.level + level);
    }

    @Override
    public void removeLevel(int level) {
        setLevel(this.level - level);
    }

    @Override
    public int levelUP() {
        double xpNeeded = getXpToNextLevel();
        if (this.exp >= xpNeeded) {
            this.exp -= xpNeeded;
            this.level++;
            this.upgradePoints++;
            this.persist();
            this.callLevelUPEvent(this.level - 1, this.level);
        }
        return this.level;
    }

    @Override
    public int getSkillLevel(Skill skill) {
        return this.skillLevels.getOrDefault(skill, 0);
    }

    @Override
    public void setSkillLevel(Skill skill, int level) {
        this.skillLevels.put(skill, level);
        this.persist();
    }

    @Override
    public int getUpgradePoints() {
        return this.upgradePoints;
    }

    @Override
    public void setUpgradePoints(int upgradePoints) {
        this.upgradePoints = upgradePoints;
        this.persist();
    }

    @Override
    public void addUpgradePoints(int upgradePoints) {
        this.upgradePoints += upgradePoints;
        this.persist();
    }

    @Override
    public void removeUpgradePoints(int upgradePoints) {
        this.upgradePoints -= upgradePoints;
        this.persist();
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

    public void persist() {
        if (this.writeBehindBuffer != null)
            this.writeBehindBuffer.put(this.id, this);
    }

    @Override
    public double getXpToNextLevel() {
        return defaultXPCalculator.apply(this.level);
    }

    private void checkLevelUP() {
        int oldLevel = this.level;

        double xpNeeded;
        while (this.exp >= (xpNeeded = getXpToNextLevel())) {
            this.exp -= xpNeeded;
            this.level++;
            this.upgradePoints++;
        }

        if (oldLevel < this.level) {
            this.persist();
            this.callLevelUPEvent(oldLevel, this.level);
        }
    }

    private void callGainExpEvent(double expGained) {
        PlayerGainExpEvent playerGainExpEvent = new PlayerGainExpEvent(this, expGained);
        Bukkit.getPluginManager().callEvent(playerGainExpEvent);
    }

    private void callLossExpEvent(double expLoss) {
        PlayerLossExpEvent playerLossExpEvent = new PlayerLossExpEvent(this, expLoss);
        Bukkit.getPluginManager().callEvent(playerLossExpEvent);
    }

    private void callLevelUPEvent(int oldLevel, int newLevel) {
        PlayerLevelUpEvent playerLevelUpEvent = new PlayerLevelUpEvent(this, oldLevel, newLevel);
        Bukkit.getPluginManager().callEvent(playerLevelUpEvent);
    }
}