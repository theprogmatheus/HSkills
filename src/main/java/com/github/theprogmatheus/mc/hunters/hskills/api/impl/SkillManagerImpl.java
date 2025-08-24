package com.github.theprogmatheus.mc.hunters.hskills.api.impl;

import com.github.theprogmatheus.mc.hunters.hskills.api.Skill;
import com.github.theprogmatheus.mc.hunters.hskills.api.SkillHandler;
import com.github.theprogmatheus.mc.hunters.hskills.api.SkillManager;
import com.github.theprogmatheus.mc.hunters.hskills.api.SkillType;
import com.github.theprogmatheus.mc.hunters.hskills.api.impl.skills.RepairSkillImpl;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class SkillManagerImpl implements SkillManager {

    private final JavaPlugin plugin;
    private final Map<SkillType, Skill> skills = Maps.newHashMap();

    public SkillManagerImpl(JavaPlugin plugin) {
        this.plugin = plugin;
        registerSkill(new RepairSkillImpl());
    }

    @Override
    public void registerSkill(Skill skill) {
        this.skills.put(skill.getType(), skill);
        for (SkillHandler<? extends Event> handler : skill.getHandlers()) {
            Bukkit.getPluginManager().registerEvents(handler, plugin);
        }
    }

    @Override
    public Optional<Skill> getSkill(SkillType type) {
        return Optional.ofNullable(this.skills.get(type));
    }

    @Override
    public Collection<Skill> getAllSkills() {
        return ImmutableList.copyOf(skills.values());
    }
}
