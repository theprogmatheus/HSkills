package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.flatstorage;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class StorageIndex extends StorageData {

    protected final Map<Long, Long> map;

    @Override
    public void read(DataInput input) throws IOException {
        super.read(input);

        Map<Long, Long> indexMap = new HashMap<>();
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(getPayload());
             DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)) {

            int size = dataInputStream.readInt();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    long key = dataInputStream.readLong();
                    long value = dataInputStream.readLong();

                    indexMap.put(key, value);
                }
            }
        }

        this.map.clear();
        this.map.putAll(indexMap);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        byte[] payload;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {


            dataOutputStream.writeInt(this.map.size());
            if (!this.map.isEmpty()) {
                for (Map.Entry<Long, Long> entry : map.entrySet()) {
                    dataOutputStream.writeLong(entry.getKey());
                    dataOutputStream.writeLong(entry.getValue());
                }
            }

            payload = byteArrayOutputStream.toByteArray();
        }

        super.setAlive(true);
        super.setPayload(payload);
        super.write(output);
    }

}