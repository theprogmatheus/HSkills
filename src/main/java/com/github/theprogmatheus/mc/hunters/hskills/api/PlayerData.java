package com.github.theprogmatheus.mc.hunters.hskills.api;

import java.util.UUID;
import java.util.function.Function;

public interface PlayerData {

    Function<Integer, Double> defaultXPCalculator = level -> 1000 * Math.pow(1.15, level);

    /**
     * @return the player uuid
     */
    UUID getId();

    /**
     * @return the current exp
     */
    double getExp();

    /**
     * Define new exp to player
     *
     * @param exp - the new exp to define
     */
    void setExp(double exp);

    /**
     * Add exp to player
     *
     * @param exp - the exp to add
     */
    void addExp(double exp);

    /**
     * Remove exp from player
     *
     * @param exp - the exp to remove
     */
    void removeExp(double exp);

    /**
     * @return the current level
     */
    int getLevel();

    /**
     * Define new level to player
     *
     * @param level - the new level
     */
    void setLevel(int level);

    /**
     * Add level to player
     *
     * @param level - the level to add
     */
    void addLevel(int level);

    /**
     * Remove level from player
     *
     * @param level - the level to remove
     */
    void removeLevel(int level);

    /**
     * processes the player's level up
     *
     * @return the current level after process
     */
    int levelUP();

    /**
     * @param skill - the skill type
     * @return the skill level
     */
    int getSkillLevel(Skill skill);

    /**
     * Define new skill level
     *
     * @param skill - the skill type
     * @param level - the new level
     */
    void setSkillLevel(Skill skill, int level);

    /**
     * @return the current upgrade points
     */
    int getUpgradePoints();

    /**
     * Define new upgrade points to player
     *
     * @param upgradePoints - upgrade points amount
     */
    void setUpgradePoints(int upgradePoints);

    /**
     * Add upgrade points to player
     *
     * @param upgradePoints - upgrade points amount
     */
    void addUpgradePoints(int upgradePoints);

    /**
     * Remove upgrade points from player
     *
     * @param upgradePoints - upgrade points amount.
     */
    void removeUpgradePoints(int upgradePoints);

    /**
     * Upgrade skill level
     *
     * @param skill - the skill type
     * @return the new level
     */
    int upgradeSkill(Skill skill);

    /**
     * @return The exp needed to upgrade to the next level.
     */
    double getXpToNextLevel();

}