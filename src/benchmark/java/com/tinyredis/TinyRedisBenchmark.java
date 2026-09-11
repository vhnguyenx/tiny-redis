package com.tinyredis;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.BenchmarkMode;

public class TinyRedisBenchmark {

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void initBenchmark() {
    }
}
