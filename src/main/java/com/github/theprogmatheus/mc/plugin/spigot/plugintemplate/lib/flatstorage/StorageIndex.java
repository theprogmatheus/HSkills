package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.flatstorage;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class StorageIndex extends StorageData {

    protected final Map<Long, Long> idOffsetMap;
    protected final Map<Long, Long> offsetIdMap;

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

        this.idOffsetMap.clear();
        this.idOffsetMap.putAll(indexMap);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        byte[] payload;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {


            dataOutputStream.writeInt(this.idOffsetMap.size());
            if (!this.idOffsetMap.isEmpty()) {
                for (Map.Entry<Long, Long> entry : idOffsetMap.entrySet()) {
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

    public Long getOffsetById(long id) {
        return this.idOffsetMap.get(id);
    }

    public Long getIdByOffset(long offset) {
        return this.offsetIdMap.get(offset);
    }

    public void add(long id, long offset) {
        this.idOffsetMap.put(id, offset);
        this.offsetIdMap.put(offset, id);
    }

    public void removeId(long id) {
        Long offset = this.idOffsetMap.get(id);
        if (offset == null)
            return;

        this.idOffsetMap.remove(id);
        this.offsetIdMap.remove(offset);
    }

    public boolean isEmpty() {
        return this.idOffsetMap.isEmpty();
    }

    public int count() {
        return this.idOffsetMap.size();
    }

}