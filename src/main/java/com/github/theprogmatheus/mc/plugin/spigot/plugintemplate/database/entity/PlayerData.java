package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.database.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "plugintemplate_playerdata")
public class PlayerData {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(canBeNull = false)
    private String name;

}
