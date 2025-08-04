package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.flatstorage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class CachedFlatStorage<O> extends FlatStorage<O> {

    private final int cacheSize;
    private final Cache<O> cache;

    public CachedFlatStorage(File file, Class<O> typeClass, int cacheSize) {
        super(file, typeClass);
        this.cache = new Cache<>(this, this.cacheSize = cacheSize);
    }

    private CacheValue<O> readOrGetCache(long id) {
        CacheValue<O> cachedObject = this.cache.get(id);
        if (cachedObject != null)
            return cachedObject;

        this.cache.put(id, cachedObject = new CacheValue<>(id, super.get(id), false, false));
        return cachedObject;
    }

    @Override
    public void put(long id, O value) {
        CacheValue<O> cachedObject = readOrGetCache(id);
        cachedObject.setValue(value);
        cachedObject.setDirty(true);
        cachedObject.setDeleted(false);
    }

    @Override
    public O get(long id) {
        if (!contains(id))
            return null;
        return readOrGetCache(id).getValue();
    }

    @Override
    public List<O> getAll() {
        Storage storage = getStorage();
        StorageIndex index = storage.getIndex();

        Map<Long, O> items = storage.getAll()
                .stream()
                .collect(Collectors.toMap(data ->
                        index.getIdByOffset(data.getOffset()), this::fromStorageData));

        this.cache.values().forEach(cachedValue -> {
            if (!cachedValue.isDeleted()) {
                items.put(cachedValue.getId(), cachedValue.getValue());
            } else {
                items.remove(cachedValue.getId());
            }
        });
        return new ArrayList<>(items.values());
    }

    @Override
    public boolean remove(long id) {
        if (!contains(id))
            return false;

        CacheValue<O> cachedObject = readOrGetCache(id);
        cachedObject.setDeleted(true);
        cachedObject.setDirty(true);
        return cachedObject.isDeleted();
    }

    @Override
    public boolean contains(long id) {
        CacheValue<O> cachedObject = this.cache.get(id);
        if (cachedObject != null)
            return !cachedObject.isDeleted() && cachedObject.getValue() != null;
        return super.contains(id);
    }

    public void flush() {
        Storage storage = getStorage();
        storage.executeBatchWrite(() -> {
            for (CacheValue<O> cachedObject : this.cache.values()) {
                if (!cachedObject.isDirty())
                    continue;

                long id = cachedObject.getId();
                O value = cachedObject.getValue();

                if (cachedObject.isDeleted() || value == null)
                    storage.remove(id);
                else
                    storage.put(id, toStorageData(value));
            }
        });
        storage.persistMetadata();
    }


    @Override
    public void close() throws IOException {
        flush();
    }

    private class Cache<T> extends LinkedHashMap<Long, CacheValue<O>> {

        private final int cacheCapacity;
        private final CachedFlatStorage<T> storage;

        public Cache(CachedFlatStorage<T> storage, int capacity) {
            super(capacity, 0.75F, true);
            this.cacheCapacity = capacity;
            this.storage = storage;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, CacheValue<O>> eldest) {
            if (size() > cacheCapacity) {
                CacheValue<O> cacheValue = eldest.getValue();
                if (cacheValue.isDirty()) {
                    if (cacheValue.isDeleted())
                        storage.getStorage().remove(cacheValue.getId());
                    else
                        storage.getStorage().put(cacheValue.getId(), toStorageData(cacheValue.getValue()));

                    cacheValue.setDirty(false);
                }
                return true;
            }
            return false;
        }
    }

    @Data
    @AllArgsConstructor
    private class CacheValue<O> {
        private final long id;
        private O value;
        private boolean dirty;
        private boolean deleted;
    }

}
