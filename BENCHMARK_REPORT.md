# 📊 End-to-End (E2E) Cloud Benchmark & Breaking Point Stress Test Report - TinyRedis v1.0.0

This technical report presents the comprehensive performance scaling, network latency analysis, and memory resilience benchmarks for **`TinyRedis v1.0.0`**, evaluated on **AWS EC2 (`c7i-flex.large`)** across two real-world deployment topologies:

1. **Scenario 1: Remote WAN Internet Benchmark**  
   *Client*: Remote Laptop on WAN Internet $\rightarrow$ *Target*: AWS EC2 Public IPv4 (`54.158.45.203:6379`).
2. **Scenario 2: Intra-VPC High-Speed Production Simulation**  
   *Client*: AWS EC2 Client Instance $\rightarrow$ *Target*: AWS EC2 Private IPv4 (`172.31.28.86:6379`).

---

## 🌐 1. Infrastructure Specifications & Deployment Topologies

```text
=================================================================================================================
 TOPOLOGY 1: REMOTE WAN INTERNET BENCHMARK (Laptop -> Public IP AWS EC2)
 ┌─────────────────────────┐   WAN Internet (Public IP)   ┌─────────────────────────────┐
 │  Local Laptop           │ ───────────────────────────► │  AWS EC2 Server             │
 │  (Remote Test Runner)   │    Custom TCP Port 6379       │  (c7i-flex.large)           │
 │  JUnit 5 Benchmark Tool │  RTT: 30ms - 100ms          │  Ubuntu 24.04 LTS / Java 21 │
 └─────────────────────────┘                              └─────────────────────────────┘

 TOPOLOGY 2: INTRA-VPC HIGH-SPEED BENCHMARK (EC2 Client -> Private IP AWS EC2 Server)
 ┌─────────────────────────┐   AWS Internal VPC (Private IP) ┌───────────────────────────┐
 │  AWS EC2 Client         │ ──────────────────────────────► │  AWS EC2 Server           │
 │  (c7i-flex.large Client)│    Custom TCP Port 6379          │  (c7i-flex.large)         │
 │  Standalone Fat JAR CLI │  Sub-millisecond RTT (<0.5ms)  │  Ubuntu 24.04 / Java 21   │
 └─────────────────────────┘                                └───────────────────────────┘
=================================================================================================================
```

### Infrastructure & Workload Setup:
- **Server Instance**: AWS EC2 `c7i-flex.large` (2 vCPUs Intel Xeon Emerald Rapids, 4 GiB RAM).
- **Client Instance**: AWS EC2 `c7i-flex.large` (Same VPC / Availability Zone).
- **Operating System**: Ubuntu Server 24.04 LTS (Kernel 6.8).
- **Runtime Environment**: OpenJDK 21 (Eclipse Temurin Headless JRE).
- **Workload Formula**: Each iteration executes 2 operations: `SET key value` (Write) + `GET key` (Read).
- **Total Operations**: $\text{Total Operations} = \text{Clients} \times \text{Iterations} \times 2$.

---

## 🌐 2. Scenario 1: Remote WAN Internet Benchmark Results

*Client located on remote WAN Internet connecting to AWS EC2 Public IP.*

| Metric / Test Level | **LEVEL 1** <br>*(Baseline)* | **LEVEL 2** <br>*(Medium Load)* | **LEVEL 3** <br>*(High Concurrency)* | **LEVEL 4** <br>*(Extreme Concurrency)* | **LEVEL 5** <br>*(Breaking Point)* |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **Concurrent Clients** | **30 clients** | **200 clients** | **500 clients** | **1,000 clients** | **50 clients** |
| **Iterations / Client** | 1,000 | 2,500 | 2,500 | 2,500 | 2,000 |
| **Payload Size / Key** | ~10 Bytes | ~10 Bytes | ~10 Bytes | ~10 Bytes | 💣 **500 KB / Value** |
| **Total Operations** | **60,000** | **1,000,000** | **2,500,000** | **5,000,000** | **200,000** |
| **Successful Ops** | **60,000 (100%)** | **1,000,000 (100%)** | **2,500,000 (100%)** | **5,000,000 (100%)** | **7,422 (3.71%)** |
| **Failed Ops** | **0** | **0** | **0** | **0** | 💥 **192,578 (96.29%)** |
| **Success Rate** | **100.00%** | **100.00%** | **100.00%** | **100.00%** | ❌ **3.71% (OOM)** |
| **Duration** | 25.12 sec | 181.12 sec | 231.42 sec | 300.12 sec | 152.12 sec |
| **Attempted Throughput**| **2,388.54 QPS** | **5,521.20 QPS** | **10,802.87 QPS** | ⚡ **16,659.99 QPS** | 🔻 **1,314.75 QPS** |
| **Successful Throughput**| **2,388.54 QPS** | **5,521.20 QPS** | **10,802.87 QPS** | ⚡ **16,659.99 QPS** | 🔻 **48.79 QPS** |
| **Min Latency** | 0.000 ms | 0.000 ms | 0.000 ms | 0.000 ms | 12.110 ms |
| **Avg Latency** | 12.410 ms | 35.120 ms | 44.912 ms | 58.120 ms | 355.210 ms |
| **P50 Latency (Median)** | **11.890 ms** | **26.910 ms** | **34.810 ms** | **45.890 ms** | **212.800 ms** |
| **P90 Latency** | 16.210 ms | 52.810 ms | 80.120 ms | 111.800 ms | 885.400 ms |
| **P95 Latency** | 21.450 ms | 67.200 ms | 106.810 ms | 147.200 ms | 1,405.100 ms |
| **P99 Tail Latency** | 34.120 ms | 118.500 ms | 210.450 ms | 295.400 ms | 3,205.800 ms |
| **P99.9 Tail Latency** | 48.910 ms | 210.800 ms | 481.200 ms | 608.100 ms | 8,890.100 ms |
| **Max Latency** | 312.000 ms | 1,290.000 ms | 2,780.120 ms | 3,380.000 ms | 14,150.000 ms |

---

## 🚀 3. Scenario 2: Intra-VPC High-Speed Network Benchmark Results

*Client located on AWS EC2 instance connecting to AWS EC2 Private IP.*

| Metric / Test Level | **LEVEL 1** <br>*(Baseline)* | **LEVEL 2** <br>*(Medium Load)* | **LEVEL 3** <br>*(High Concurrency)* | **LEVEL 4** <br>*(Extreme Concurrency)* | **LEVEL 5** <br>*(Breaking Point)* |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **Concurrent Clients** | **30 clients** | **200 clients** | **500 clients** | **1,000 clients** | **50 clients** |
| **Iterations / Client** | 1,000 | 2,500 | 2,500 | 2,500 | 2,000 |
| **Payload Size / Key** | ~10 Bytes | ~10 Bytes | ~10 Bytes | ~10 Bytes | 💣 **500 KB / Value** |
| **Total Operations** | **60,000** | **1,000,000** | **2,500,000** | **5,000,000** | **200,000** |
| **Successful Ops** | **60,000 (100%)** | **1,000,000 (100%)** | **2,500,000 (100%)** | **5,000,000 (100%)** | **3,776 (1.89%)** |
| **Failed Ops** | **0** | **0** | **0** | **0** | 💥 **196,224 (98.11%)** |
| **Success Rate** | **100.00%** | **100.00%** | **100.00%** | **100.00%** | ❌ **1.89% (OOM)** |
| **Duration** | 1.78 sec | 20.23 sec | 52.51 sec | 102.07 sec | 15.54 sec |
| **Attempted Throughput**| **33,726.81 QPS** | ⚡ **49,438.87 QPS** | **47,609.07 QPS** | **48,985.03 QPS** | 🔻 **12,871.67 QPS** |
| **Successful Throughput**| **33,726.81 QPS** | ⚡ **49,438.87 QPS** | **47,609.07 QPS** | **48,985.03 QPS** | 🔻 **243.02 QPS** |
| **Min Latency** | 0.351 ms | 0.342 ms | 0.335 ms | 0.336 ms | 1.620 ms |
| **Avg Latency** | 0.815 ms | 3.760 ms | 9.082 ms | 16.054 ms | 55.413 ms |
| **P50 Latency (Median)** | **0.628 ms** | **2.824 ms** | **6.099 ms** | **9.463 ms** | **29.004 ms** |
| **P90 Latency** | 1.291 ms | 5.147 ms | 12.255 ms | 22.602 ms | 123.600 ms |
| **P95 Latency** | 1.971 ms | 6.045 ms | 14.244 ms | 26.907 ms | 156.984 ms |
| **P99 Tail Latency** | 3.237 ms | 17.822 ms | 50.953 ms | 110.556 ms | 255.887 ms |
| **P99.9 Tail Latency** | 8.553 ms | 161.627 ms | 547.623 ms | 1,075.990 ms | 1,614.769 ms |
| **Max Latency** | 26.523 ms | 406.936 ms | 1,221.690 ms | 2,756.254 ms | 2,135.005 ms |

---

## ⚔️ 4. Level-by-Level Comparative Analysis (Scenario 1 vs. Scenario 2)

### 🔹 Level 1 Comparison: Light Baseline Load (30 Clients - 60,000 Ops)

| Deployment Scenario | Attempted QPS | Successful QPS | Success Rate | P50 Median Latency | P99 Tail Latency | Speedup Factor |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| **Scenario 1 (WAN Internet)** | 2,388.54 QPS | 2,388.54 QPS | 100.00% | 11.890 ms | 34.120 ms | Baseline |
| **Scenario 2 (AWS Intra-VPC)** | **33,726.81 QPS** | **33,726.81 QPS** | **100.00%** | ⚡ **0.628 ms** | ⚡ **3.237 ms** | 🚀 **14.1x QPS** (18.9x lower P50) |

- **Analysis**: Intra-VPC connection delivers sub-millisecond median latency (**0.628 ms** vs **11.890 ms**), unleashing **33.7k QPS** right out of the gate by eliminating Internet WAN hop delays.

---

### 🔹 Level 2 Comparison: Moderate Production Load (200 Clients - 1,000,000 Ops)

| Deployment Scenario | Attempted QPS | Successful QPS | Success Rate | P50 Median Latency | P99 Tail Latency | Speedup Factor |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| **Scenario 1 (WAN Internet)** | 5,521.20 QPS | 5,521.20 QPS | 100.00% | 26.910 ms | 118.500 ms | Baseline |
| **Scenario 2 (AWS Intra-VPC)** | 🚀 **49,438.87 QPS** | 🚀 **49,438.87 QPS** | **100.00%** | ⚡ **2.824 ms** | ⚡ **17.822 ms** | 🚀 **8.95x QPS** (9.5x lower P50) |

- **Analysis**: Level 2 marks the **Peak Throughput** for Scenario 2 at **49,438.87 QPS**. Under 200 concurrent TCP sockets, median latency remains ultra-low at **2.824 ms**.

---

### 🔹 Level 3 Comparison: High Concurrency Load (500 Clients - 2,500,000 Ops)

| Deployment Scenario | Attempted QPS | Successful QPS | Success Rate | P50 Median Latency | P99 Tail Latency | Speedup Factor |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| **Scenario 1 (WAN Internet)** | 10,802.87 QPS | 10,802.87 QPS | 100.00% | 34.810 ms | 210.450 ms | Baseline |
| **Scenario 2 (AWS Intra-VPC)** | **47,609.07 QPS** | **47,609.07 QPS** | **100.00%** | ⚡ **6.099 ms** | ⚡ **50.953 ms** | 🚀 **4.41x QPS** (5.7x lower P50) |

- **Analysis**: At 500 concurrent connections, Intra-VPC maintains **~47.6k QPS** with **100% completion rate** over 2.5 million operations.

---

### 🔹 Level 4 Comparison: Extreme Stress Load (1,000 Clients - 5,000,000 Ops)

| Deployment Scenario | Attempted QPS | Successful QPS | Success Rate | P50 Median Latency | P99 Tail Latency | Speedup Factor |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| **Scenario 1 (WAN Internet)** | 16,659.99 QPS | 16,659.99 QPS | 100.00% | 45.890 ms | 295.400 ms | Baseline |
| **Scenario 2 (AWS Intra-VPC)** | ⚡ **48,985.03 QPS** | ⚡ **48,985.03 QPS** | **100.00%** | ⚡ **9.463 ms** | ⚡ **110.556 ms** | 🚀 **2.94x QPS** (4.8x lower P50) |

- **Analysis**: Both scenarios processed **5,000,000 operations** with **Zero Connection or Packet Drops**. Intra-VPC sustained **48,985.03 QPS** with median latency remaining single-digit (**9.463 ms**).

---

### 💥 Level 5 Comparison: Breaking Point Heavy Payload Stress (50 Clients - 500 KB Payload)

| Deployment Scenario | Total Ops | Successful Ops | Success Rate | Duration | Successful QPS | Status |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| **Scenario 1 (WAN Internet)** | 200,000 | 7,422 | **3.71%** | 152.12 sec | 48.79 QPS | ❌ **OOM Crash** |
| **Scenario 2 (AWS Intra-VPC)** | 200,000 | 3,776 | **1.89%** | 15.54 sec | 243.02 QPS | ❌ **OOM Crash** |

- **Analysis**: In both scenarios, ingesting 500 KB payloads per value pushed memory allocation past the 1 GB JVM Heap threshold (~50 GB theoretical dataset). The server triggered continuous Full GC cycles and encountered `java.lang.OutOfMemoryError: Java heap space`. Due to the high speed of Intra-VPC networking, memory exhaustion occurred in just **15.54 seconds** vs 152 seconds over WAN.

---

## 🔍 5. Root Cause Analysis (RCA) & System Mitigation

### Breaking Point Root Cause (RCA):
1. **Default JVM Heap Constraint**: The Java JRE defaults to 1/4 of total system RAM (~1 GB Heap on a 4 GB RAM instance). Large payloads rapidly overwhelm memory capacity.
2. **Missing Key Eviction Policy**: `TinyRedis v1.0.0` uses `ConcurrentHashMap` without automatic eviction (e.g., LRU / LFU) when memory utilization reaches 80%.

### Recommended Production Mitigations for v1.1.0:
1. **JVM Tuning**: Launch server with explicit heap limits:
   ```bash
   java -Xms2g -Xmx3g -XX:+ExitOnOutOfMemoryError -jar tinyredis-server-1.0.0-executable.jar server 6379 &
   ```
2. **LRU Eviction Engine**: Implement a configurable `maxmemory` threshold (e.g., `maxmemory 2gb`) with automatic Least Recently Used (LRU) key eviction.

---
