package com.github.theprogmatheus.mc.hunters.hskills.api;

import java.util.Collection;
import java.util.Optional;

public interface SkillManager {

    void registerSkill(Skill skill);

    Optional<Skill> getSkill(SkillType type);

    Collection<Skill> getAllSkills();
}
