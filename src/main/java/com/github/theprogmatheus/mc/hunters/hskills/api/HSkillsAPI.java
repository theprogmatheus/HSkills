package com.github.theprogmatheus.mc.hunters.hskills.api;

import com.github.theprogmatheus.mc.hunters.hskills.service.APIService;
import com.github.theprogmatheus.mc.hunters.hskills.service.MainService;

public class HSkillsAPI {

    public static PlayerDataManager getPlayerDataManager() {
        return MainService.getService(APIService.class).getPlayerDataManager();
    }

    public static SkillManager getSkillManager() {
        return MainService.getService(APIService.class).getSkillManager();
    }

}
