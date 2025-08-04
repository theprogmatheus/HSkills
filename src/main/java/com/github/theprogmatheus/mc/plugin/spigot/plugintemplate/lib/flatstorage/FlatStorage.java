package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.flatstorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

@Getter
public class FlatStorage<O> implements Closeable {

    private final Storage storage;
    private final Class<O> typeClass;
    private final StorageSchema<O> schema;
    private final ObjectMapper objectMapper;

    public FlatStorage(File file, Class<O> typeClass) {
        this.typeClass = typeClass;
        this.schema = new StorageSchema<>(this.typeClass);
        this.storage = new Storage(file, this.schema);
        this.objectMapper = new ObjectMapper(new MessagePackFactory());
    }

    public File getFile() {
        return this.storage.getFile();
    }

    public void put(long id, O value) {
        this.storage.put(id, toStorageData(value));
    }

    public O get(long id) {
        return fromStorageData(this.storage.get(id));
    }

    public List<O> getAll() {
        return this.storage.getAll().stream().map(this::fromStorageData).toList();
    }

    public boolean remove(long id) {
        return this.storage.remove(id);
    }

    public boolean contains(long id) {
        return this.storage.contains(id);
    }

    public <T> T executeBatchWrite(Supplier<T> supplier) {
        return this.storage.executeBatchWrite(supplier);
    }

    public void executeBatchWrite(Runnable runnable) {
        this.storage.executeBatchWrite(runnable);
    }

    public <T> T executeBatchRead(Supplier<T> supplier) {
        return this.storage.executeBatchRead(supplier);
    }

    public void executeBatchRead(Runnable runnable) {
        this.storage.executeBatchRead(runnable);
    }

    protected StorageData toStorageData(O object) {
        try {
            if (object == null)
                return null;
            return new StorageData(0, true, this.objectMapper.writeValueAsBytes(object));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected O fromStorageData(StorageData data) {
        try {
            if (data == null)
                return null;
            return this.objectMapper.readValue(data.getPayload(), this.typeClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void close() throws IOException {
        this.storage.persistMetadata();
        this.storage.compact();
    }
}