package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.flatstorage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageHeader implements StorageIO {

    public static final int HEADER_VERSION = 1;
    public static final int HEADER_SCHEMA_VERSION = 1;
    public static final int HEADER_RESERVED_LENGTH = 1024;

    private int version;
    private int schemaVersion;
    private long creationTimestamp;
    private StorageIndexInfo indexInfo;
    private long schemasOffset;

    @Override
    public void read(DataInput input) throws IOException {
        this.version = input.readInt();
        this.schemaVersion = input.readInt();
        this.creationTimestamp = input.readLong();
        this.indexInfo = new StorageIndexInfo(input.readLong(), input.readInt());
        this.schemasOffset = input.readLong();
    }

    @Override
    public void write(DataOutput output) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {

            dataOutputStream.writeInt(this.version);
            dataOutputStream.writeInt(this.schemaVersion);
            dataOutputStream.writeLong(this.creationTimestamp);
            dataOutputStream.writeLong(this.indexInfo.getOffset());
            dataOutputStream.writeInt(this.indexInfo.getCount());
            dataOutputStream.writeLong(this.schemasOffset);

            byte[] headerBytes = byteArrayOutputStream.toByteArray();

            if (headerBytes.length > HEADER_RESERVED_LENGTH)
                throw new IOException("The header size exceeded the maximum allowed size. size=%s, max_size=%s"
                        .formatted(headerBytes.length, HEADER_RESERVED_LENGTH));

            output.write(headerBytes);
            int remaining = HEADER_RESERVED_LENGTH - headerBytes.length;
            for (int i = 0; i < remaining; i++)
                output.writeByte(0);
        }
    }

    @Data
    static class StorageIndexInfo {
        private final long offset;
        private final int count;
    }
}
