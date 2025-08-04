package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.flatstorage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchemaSnapshot {

    private long schemaVersion;
    private String className;
    private int recordSize;
    private Map<String, SchemaSnapshotField> fields;
}
