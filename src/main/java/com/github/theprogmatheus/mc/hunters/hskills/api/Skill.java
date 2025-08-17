package com.github.theprogmatheus.mc.hunters.hskills.api;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Skill {

    REPAIR(1),
    GATHERING(2),
    ALCHEMY(3),
    ATTACK(4),
    DEFENSE(5),
    FISHING(6);

    private final int id;

    Skill(int id) {
        this.id = id;
    }

    public static Skill fromId(int id) {
        return Arrays.stream(values())
                .filter(skill -> skill.id == id)
                .findAny()
                .orElse(null);
    }
}