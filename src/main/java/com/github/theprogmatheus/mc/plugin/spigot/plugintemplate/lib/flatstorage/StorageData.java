package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.flatstorage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageData implements StorageIO {

    private transient long offset;

    private boolean alive;
    private byte[] payload;

    @Override
    public void read(DataInput input) throws IOException {
        this.alive = input.readBoolean();
        this.payload = new byte[input.readInt()];

        input.readFully(this.payload);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeBoolean(this.alive);
        if (this.payload == null)
            output.writeInt(0);
        else {
            output.writeInt(this.payload.length);
            output.write(this.payload);
        }
    }
}
