package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.flatstorage;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface StorageIO {

    public abstract void read(DataInput input) throws IOException;

    public abstract void write(DataOutput output) throws IOException;

}
