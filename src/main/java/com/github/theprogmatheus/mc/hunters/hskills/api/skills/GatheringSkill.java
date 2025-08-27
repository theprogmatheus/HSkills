package com.github.theprogmatheus.mc.hunters.hskills.api.skills;

import com.github.theprogmatheus.mc.hunters.hskills.api.Skill;
import com.github.theprogmatheus.mc.hunters.hskills.api.SkillType;

public interface GatheringSkill extends Skill {

    @Override
    default SkillType getType() {
        return SkillType.GATHERING;
    }
}
