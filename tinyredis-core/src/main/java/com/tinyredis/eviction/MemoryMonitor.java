package com.tinyredis.eviction;

public class MemoryMonitor {
    private final long maxMemoryBytes;

    public MemoryMonitor(long maxMemoryBytes) {
        if (maxMemoryBytes <= 0) {
            throw new IllegalArgumentException("maxMemory must be positive");
        }

        this.maxMemoryBytes = maxMemoryBytes;
    }

    public long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();

        return runtime.totalMemory() - runtime.freeMemory();
    }

    public long getMaxMemory() {
        return maxMemoryBytes;
    }

    public boolean isOverLimit() {
        return getUsedMemory() >= maxMemoryBytes;
    }

    public double getUsageRatio() {
        return (double) getUsedMemory() / maxMemoryBytes;
    }
}
