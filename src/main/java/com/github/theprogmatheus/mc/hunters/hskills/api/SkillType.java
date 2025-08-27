package com.github.theprogmatheus.mc.hunters.hskills.api;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SkillType {

    REPAIR(1, 1, 2, 3, 4, 8, 14),
    GATHERING(2),
    ALCHEMY(3),
    ATTACK(4),
    DEFENSE(5),
    FISHING(6);

    private final int id;
    private final int[] upgradeCosts;

    SkillType(int id, int... upgradeCosts) {
        this.id = id;
        this.upgradeCosts = upgradeCosts;
    }

    public static SkillType fromId(int id) {
        return Arrays.stream(values())
                .filter(skill -> skill.id == id)
                .findAny()
                .orElse(null);
    }
}