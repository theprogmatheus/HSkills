package com.github.theprogmatheus.mc.hunters.hskills.command;

import com.github.theprogmatheus.mc.hunters.hskills.api.HSkillsAPI;
import com.github.theprogmatheus.mc.hunters.hskills.api.PlayerData;
import com.github.theprogmatheus.mc.hunters.hskills.api.PlayerDataManager;
import com.github.theprogmatheus.mc.hunters.hskills.api.SkillType;
import com.github.theprogmatheus.mc.hunters.hskills.lib.invflow.example.MenuTest;
import com.github.theprogmatheus.mc.hunters.hskills.service.APIService;
import com.github.theprogmatheus.mc.hunters.hskills.service.MainService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UpgradeCommand extends AbstractCommand {


    public UpgradeCommand() {
        super("skillup", "skillup.cmd", "skillup <skill>", new String[]{"upgrade"});
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {

            MainService.getService(APIService.class).getInvFlow().open(player, MenuTest.class);

            if (args.length < 1)
                return false;

            SkillType skillType = null;
            try {
                skillType = SkillType.valueOf(args[0].toUpperCase());
            } catch (Exception ignored) {

            }

            if (skillType == null) {
                player.sendMessage("§cSkill não encontrada.");
                return true;
            }


            PlayerDataManager playerDataManager = HSkillsAPI.getPlayerDataManager();
            PlayerData data = playerDataManager.getPlayerData(player);

            int skillLevel = data.getSkillLevel(skillType);

            int newSkillLevel = data.upgradeSkill(skillType);

            if (skillLevel == newSkillLevel) {
                player.sendMessage("§cNão foi possível evoluir a skill.");
            } else {
                player.sendMessage("§aSkill evoluida com sucesso para o nível: %s".formatted(newSkillLevel));
            }

        }
        return true;
    }
}
