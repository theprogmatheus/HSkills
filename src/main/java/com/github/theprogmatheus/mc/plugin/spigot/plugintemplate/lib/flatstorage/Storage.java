package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.flatstorage;

import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Getter
class Storage {

    private final File file;
    private final ReentrantReadWriteLock lock;
    private RandomAccessFile randomAccessFile;
    private StorageHeader header;
    private StorageIndex index;
    private boolean loaded;

    public Storage(File file) {
        this.file = file;
        this.lock = new ReentrantReadWriteLock();
    }


    public void put(long id, StorageData value) {
        Objects.requireNonNull(value, "The value can't be null.");

        if (this.randomAccessFile == null)
            executeBatchWrite(() -> _put(id, value));
        else
            _put(id, value);
    }


    private void _put(long id, StorageData value) {
        try {
            Long offset = this.index.getOffsetById(id);
            if (offset != null)
                markAsRemoved(offset);

            offset = this.randomAccessFile.length();

            this.randomAccessFile.seek(offset);
            value.write(this.randomAccessFile);
            this.index.add(id, offset);

            value.setOffset(offset);
        } catch (IOException e) {
            throw new RuntimeException("Unable to put %s.".formatted(id), e);
        }
    }

    public StorageData get(long id) {
        if (this.randomAccessFile == null)
            return executeBatchRead(() -> _get(id));
        else return _get(id);
    }

    private StorageData _get(long id) {
        try {
            Long offset = this.index.getOffsetById(id);
            if (offset == null)
                return null;

            this.randomAccessFile.seek(offset);

            StorageData storageData = new StorageData();
            storageData.setOffset(offset);
            storageData.read(this.randomAccessFile);

            return storageData;
        } catch (IOException e) {
            throw new RuntimeException("Unable to get %s.".formatted(id), e);
        }
    }

    public List<StorageData> getAll() {
        if (this.randomAccessFile == null)
            return this.executeBatchRead(this::_getAll);
        else return _getAll();
    }

    private List<StorageData> _getAll() {
        return this.index.getIdOffsetMap().entrySet().stream()
                .map(entry -> {
                    try {
                        this.randomAccessFile.seek(entry.getValue());
                        StorageData storageData = new StorageData();
                        storageData.setOffset(entry.getValue());
                        storageData.read(this.randomAccessFile);
                        return storageData;
                    } catch (IOException e) {
                        throw new RuntimeException("Unable to get %s.".formatted(entry.getKey()), e);
                    }
                })
                .collect(Collectors.toList());
    }

    public boolean remove(long id) {
        if (this.randomAccessFile == null)
            return executeBatchWrite(() -> _remove(id));
        else
            return _remove(id);
    }

    private boolean _remove(long id) {
        try {
            Long offset = this.index.getOffsetById(id);
            if (offset == null)
                return false;

            markAsRemoved(offset);
            this.index.removeId(id);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Unable to remove %s.".formatted(id), e);
        }
    }

    public boolean contains(long id) {
        if (this.randomAccessFile == null)
            return executeBatchRead(() -> _contains(id));
        else return _contains(id);
    }

    private boolean _contains(long id) {
        return this.index.getIdOffsetMap().containsKey(id);
    }

    private void markAsRemoved(long offset) throws IOException {
        this.randomAccessFile.seek(offset);
        this.randomAccessFile.writeBoolean(false);
    }

    public void saveIndex() {
        executeBatchWrite(() -> {
            try {
                if (this.index == null || this.index.isEmpty())
                    return;

                long oldOffset = this.index.getOffset();

                if (oldOffset > 0)
                    markAsRemoved(oldOffset);

                long offset = this.randomAccessFile.length();
                this.index.setOffset(offset);
                this.randomAccessFile.seek(offset);
                this.index.write(this.randomAccessFile);

                this.header.setIndexInfo(new StorageHeader.StorageIndexInfo(offset, this.index.count()));
                this.randomAccessFile.seek(0);
                this.header.write(this.randomAccessFile);

            } catch (IOException e) {
                throw new RuntimeException("Unable to persist index.", e);
            }
        });
    }

    public void compact() {
        lock.writeLock().lock();
        try {
            Storage storage = new Storage(new File("%s.temp".formatted(this.file.getAbsolutePath())));
            storage.executeBatchWrite(() -> {
                for (var entry : this.index.getIdOffsetMap().entrySet()) {
                    try {
                        Long id = entry.getKey();
                        Long offset = entry.getValue();

                        // Acessamos o arquivo principal APENAS AQUI dentro de um try-with-resources
                        try (RandomAccessFile originalFile = new RandomAccessFile(this.file, "r")) {
                            originalFile.seek(offset);
                            StorageData data = new StorageData();
                            data.read(originalFile);
                            if (data.isAlive()) {
                                storage._put(id, data);
                            }
                        } // O arquivo principal é fechado automaticamente aqui.
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            storage.saveIndex();

            // 3. Após a compactação e o fechamento de AMBOS os arquivos, podemos substituí-los.
            File backupFile = new File("%s.back".formatted(this.file.getAbsolutePath()));
            Files.copy(this.file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            try {
                Files.move(storage.file.toPath(), this.file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Files.deleteIfExists(backupFile.toPath());
                this.loaded = false;
                this.prepare();
            } catch (IOException exception) {
                Files.move(backupFile.toPath(), this.file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                throw new RuntimeException("Error trying to replace the current file with the compressed file.", exception);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to compact storage.", e);
        } finally {
            lock.writeLock().unlock();
        }
    }


    public void executeBatchWrite(Runnable runnable) {
        executeBatchWrite(() -> {
            runnable.run();
            return null;
        });
    }


    public <T> T executeBatchWrite(Supplier<T> supplier) {
        if (!this.prepare())
            throw new RuntimeException("Unable to prepare to run");

        lock.writeLock().lock();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(this.file, "rw")) {
            this.randomAccessFile = randomAccessFile;
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.randomAccessFile = null;
            lock.writeLock().unlock();
        }
    }

    public void executeBatchRead(Runnable runnable) {
        executeBatchRead(() -> {
            runnable.run();
            return null;
        });
    }

    public <T> T executeBatchRead(Supplier<T> supplier) {
        if (!this.prepare())
            throw new RuntimeException("Unable to prepare to run");

        lock.readLock().lock();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(this.file, "r")) {
            this.randomAccessFile = randomAccessFile;
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.randomAccessFile = null;
            lock.readLock().unlock();
        }
    }


    private boolean prepare() {
        try {
            if (this.loaded)
                return true;

            if (!checkFile())
                return false;
            return load();
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    private boolean checkFile() {
        if (this.file == null)
            return false;

        if (this.file.exists())
            return true;

        try {
            this.createNewStorageFile();
            return true;
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    private void createNewStorageFile() throws IOException {
        File parent = this.file.getParentFile();
        if (parent != null)
            parent.mkdirs();
        this.file.createNewFile();

        this.lock.writeLock().lock();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(this.file, "rw")) {
            randomAccessFile.seek(0);
            new StorageHeader(
                    StorageHeader.HEADER_VERSION,
                    StorageHeader.HEADER_SCHEMA_VERSION,
                    System.currentTimeMillis(),
                    new StorageHeader.StorageIndexInfo(StorageHeader.HEADER_RESERVED_LENGTH, 0)
            ).write(randomAccessFile);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private boolean load() throws IOException {
        this.lock.writeLock().lock();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(this.file, "rw")) {

            randomAccessFile.seek(0);
            StorageHeader storageHeader = new StorageHeader();
            storageHeader.read(randomAccessFile);
            this.header = storageHeader;

            StorageHeader.StorageIndexInfo indexInfo = this.header.getIndexInfo();
            StorageIndex storageIndex = new StorageIndex(new HashMap<>(), new HashMap<>());
            if (indexInfo.getCount() > 0) {
                randomAccessFile.seek(indexInfo.getOffset());
                storageIndex.read(randomAccessFile);
            }
            this.index = storageIndex;

            return this.loaded = true;
        } finally {
            this.lock.writeLock().unlock();
        }
    }
}
