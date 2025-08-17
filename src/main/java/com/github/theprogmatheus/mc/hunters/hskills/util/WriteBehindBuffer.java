package com.github.theprogmatheus.mc.hunters.hskills.util;

import lombok.Getter;

import java.io.Closeable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Acts as a **write-behind buffer**, batching persistence operations (such as puts and deletes) in memory
 * before sending them to a persistence system. This is ideal for scenarios where data persistence is a
 * costly or slow operation, such as accessing a database or a file system.
 * <p>
 * The buffer optimizes performance by decoupling the main application logic from I/O operations and
 * ensures data integrity even in the event of a persistence failure.
 *
 * <p>The class implements the {@link Closeable} interface, allowing it to be used in a
 * {@code try-with-resources} block to ensure a safe shutdown.
 *
 * @param <K> The type of the key used to identify the actions.
 * @param <V> The type of the value associated with a 'put' action.
 */
@Getter
public class WriteBehindBuffer<K, V> implements Closeable {

    private final Logger logger;
    private final int flushInterval;
    private final int flushTimeout;
    private final int mapLimit;
    private final Function<Map<K, FlushAction<V>>, Boolean> flushFunction;
    private final AtomicReference<Map<K, FlushAction<V>>> mapRef;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean flushing;

    /**
     * Creates a new instance of the WriteBehindBuffer.
     *
     * @param logger        The logger instance to record events and errors.
     * @param flushInterval The fixed interval, in seconds, for flush execution.
     * @param flushTimeout  The maximum time, in seconds, the flushFunction can run before a timeout occurs.
     * @param mapLimit      The maximum number of in-memory operations. When this limit is reached, a flush is scheduled immediately.
     * @param flushFunction The persistence function that will be executed in a batch. It receives a Map
     *                      of operations and should return {@code true} on success or {@code false} on failure.
     */
    public WriteBehindBuffer(
            Logger logger,
            int flushInterval,
            int flushTimeout,
            int mapLimit,
            Function<Map<K, FlushAction<V>>, Boolean> flushFunction
    ) {
        this.logger = logger;
        this.flushInterval = flushInterval;
        this.flushTimeout = flushTimeout;
        this.mapLimit = mapLimit;
        this.flushFunction = flushFunction;
        this.mapRef = new AtomicReference<>(new ConcurrentHashMap<>());
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.flushing = new AtomicBoolean(false);
        this.scheduler.scheduleWithFixedDelay(this::flush, this.flushInterval, this.flushInterval, TimeUnit.SECONDS);
    }

    /**
     * Adds a 'put' (insert/update) operation to the buffer.
     * If a previous 'delete' for the same key existed in the buffer, this operation overrides it.
     *
     * @param key   The key of the object to be persisted.
     * @param value The value of the object.
     * @throws NullPointerException if the key or value is {@code null}.
     */
    public void put(K key, V value) {
        Objects.requireNonNull(key, "The key can't be null.");
        Objects.requireNonNull(value, "The value can't be null.");
        this.mapRef.get().put(key, new FlushAction.Put<>(value));
        this.checkFlush();
    }

    /**
     * Adds a 'delete' (removal) operation to the buffer.
     * If a previous 'put' for the same key exists in the buffer, this operation overrides it.
     *
     * @param key The key of the object to be removed.
     * @throws NullPointerException if the key is {@code null}.
     */
    public void delete(K key) {
        Objects.requireNonNull(key, "The key can't be null.");
        this.mapRef.get().put(key, new FlushAction.Delete<>());
        this.checkFlush();
    }

    /**
     * Checks if the map size has reached the limit and, if so, schedules a flush operation.
     */
    private void checkFlush() {
        if (this.mapRef.get().size() >= this.mapLimit)
            this.scheduler.execute(this::flush);
    }

    /**
     * Processes all pending operations in the buffer, sending them in a batch to the {@code flushFunction}.
     * This method is executed periodically by the scheduler and when the {@code mapLimit} is reached.
     */
    public void flush() {
        if (this.mapRef.get().isEmpty())
            return;

        if (!this.flushing.compareAndSet(false, true))
            return;

        var mapToFlush = this.mapRef.getAndSet(new ConcurrentHashMap<>());
        try {
            Boolean success = CompletableFuture.supplyAsync(() -> flushFunction.apply(mapToFlush))
                    .get(this.flushTimeout, TimeUnit.SECONDS);

            if (!success)
                throw new RuntimeException("The flush function returned false, which indicates that the flush was not performed.");
        } catch (Exception e) {
            this.mapRef.updateAndGet(current -> {
                mapToFlush.forEach(current::putIfAbsent);
                return current;
            });
            this.logger.log(Level.SEVERE, "An error occurred while trying to flush.", e);
        } finally {
            this.flushing.set(false);
        }
    }

    /**
     * Gracefully shuts down the buffer, ensuring all pending operations are processed
     * before releasing resources.
     */
    private void shutdown() {
        try {
            this.scheduler.shutdown();
            if (!this.scheduler.awaitTermination(this.flushTimeout, TimeUnit.SECONDS))
                this.scheduler.shutdownNow();
        } catch (InterruptedException ignored) {
            this.scheduler.shutdownNow();
        }
        flush();
    }

    /**
     * An implementation of the {@link Closeable} interface. Initiates the shutdown process
     * of the buffer, ensuring all pending operations are processed before resources are released.
     * This method is automatically called when the class is used in a try-with-resources block.
     */
    @Override
    public void close() {
        this.shutdown();
    }

    /**
     * A sealed interface representing the actions that can be performed in the buffer.
     */
    public sealed interface FlushAction<V> {
        /**
         * Represents a 'put' (insert/update) operation.
         *
         * @param value The value to be persisted.
         * @param <V>   The type of the value.
         */
        record Put<V>(V value) implements FlushAction<V> {
        }

        /**
         * Represents a 'delete' (removal) operation.
         *
         * @param <V> The type of the value (not used in this record).
         */
        record Delete<V>() implements FlushAction<V> {
        }
    }
}