package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.flatstorage;

import lombok.Data;

@Data
public class StorageSchemaField {

    private final String name;
    private final Class<?> type;
    private final int recordSize;
    private final boolean pointer;

}
