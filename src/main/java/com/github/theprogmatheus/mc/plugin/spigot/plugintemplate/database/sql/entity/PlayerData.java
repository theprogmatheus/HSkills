package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.database.sql.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;

@DatabaseTable(tableName = "plugintemplate_playerdata")
@Data
public class PlayerData {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(canBeNull = false)
    private String name;

}
