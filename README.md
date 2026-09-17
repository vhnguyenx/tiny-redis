# TinyRedis

A lightweight Redis-like in-memory key-value server implemented in pure Java.

---

## Features

- **TCP server**: High-performance multi-threaded socket server
- **RESP protocol**: Redis Serialization Protocol parsing and encoding
- **Concurrent clients**: Safe multi-client concurrency handling
- **In-memory key-value storage**: Fast thread-safe storage engine
- **Redis-like CLI**: Interactive command-line terminal client
- **Basic commands**: `SET`, `GET`, `DEL`, `EXISTS`

---

## Architecture

![TinyRedis Architecture](docs/images/TinyRedis-Diagram-V1.jpg)

```text
TinyRedis
├── Transport Layer      (TCP Server & Client Socket Handlers)
├── Protocol Layer       (RESP Parser & Response Encoder/Decoder)
├── Core Engine          (KeyValue Store & State Coordinator)
└── CLI                  (Interactive Terminal Client)
```

---

## ⚡ Performance & Cloud Benchmark

`TinyRedis v1.0.0` was evaluated under End-to-End (E2E) cloud benchmarks on **AWS EC2 (`c7i-flex.large`)** across both WAN Internet and Intra-VPC high-speed networks.

### 🚀 Scenario 2: Intra-VPC High-Speed Benchmark (AWS Private Network)

| Test Level | Concurrent Clients | Total Operations | Success Rate | Attempted QPS | Successful QPS | Median Latency (P50) | P99 Tail Latency |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **Level 1** | 30 clients | 60,000 | **100.00%** | 33,727 QPS | 33,727 QPS | ⚡ **0.63 ms** | 3.24 ms |
| **Level 2** | 200 clients | 1,000,000 | **100.00%** | 🚀 **49,439 QPS** | 🚀 **49,439 QPS** | **2.82 ms** | 17.82 ms |
| **Level 3** | 500 clients | 2,500,000 | **100.00%** | 47,609 QPS | 47,609 QPS | 6.10 ms | 50.95 ms |
| **Level 4** | **1,000 clients** | **5,000,000** | **100.00%** | ⚡ **48,985 QPS** | ⚡ **48,985 QPS** | 9.46 ms | 110.56 ms |
| **Level 5** | 50 clients (500KB) | 200,000 | ❌ **1.89%** | 12,872 QPS | 243 QPS | 29.00 ms | 255.89 ms |

### 🌐 Scenario 1: Remote WAN Internet Benchmark (Public IP)

| Test Level | Concurrent Clients | Total Operations | Success Rate | Attempted QPS | Successful QPS | Median Latency (P50) | P99 Tail Latency |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **Level 1** | 30 clients | 60,000 | **100.00%** | 2,388 QPS | 2,388 QPS | 11.89 ms | 34.12 ms |
| **Level 2** | 200 clients | 1,000,000 | **100.00%** | 5,521 QPS | 5,521 QPS | 26.91 ms | 118.50 ms |
| **Level 3** | 500 clients | 2,500,000 | **100.00%** | 10,803 QPS | 10,803 QPS | 34.81 ms | 210.45 ms |
| **Level 4** | **1,000 clients** | **5,000,000** | **100.00%** | ⚡ **16,660 QPS** | ⚡ **16,660 QPS** | 45.89 ms | 295.40 ms |
| **Level 5** | 50 clients (500KB) | 200,000 | ❌ **3.71%** | 1,314 QPS | 49 QPS | 212.80 ms | 3,205.80 ms |

### ⚔️ Side-by-Side Comparison (Scenario 1 vs. Scenario 2)

| Test Level | WAN Peak QPS (Scen 1) | Intra-VPC Peak QPS (Scen 2) | WAN P50 Latency | Intra-VPC P50 Latency | Speedup Factor |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **Level 1 (30 Clients)** | 2,388 QPS | **33,727 QPS** | 11.89 ms | ⚡ **0.63 ms** | 🚀 **14.1x QPS** (18.9x lower P50) |
| **Level 2 (200 Clients)** | 5,521 QPS | 🚀 **49,439 QPS** | 26.91 ms | ⚡ **2.82 ms** | 🚀 **8.95x QPS** (9.5x lower P50) |
| **Level 3 (500 Clients)** | 10,803 QPS | **47,609 QPS** | 34.81 ms | ⚡ **6.10 ms** | 🚀 **4.41x QPS** (5.7x lower P50) |
| **Level 4 (1,000 Clients)**| 16,660 QPS | ⚡ **48,985 QPS** | 45.89 ms | ⚡ **9.46 ms** | 🚀 **2.94x QPS** (4.8x lower P50) |

> 📖 **Full Engineering Report**: Read the complete breaking-point analysis, RCA, and latency percentiles in [BENCHMARK_REPORT.md](BENCHMARK_REPORT.md).

---


## Quick Start

### Prerequisites
- **Java 21** or higher
- **Maven 3.9+**

### Start Server

Build the project and start the server on default port `6379`:

```powershell
# Build project
mvn clean compile

# Start server
mvn exec:java -pl tinyredis-server
```

### Start CLI

In a new terminal window, connect to the running server:

```powershell
mvn exec:java -pl tinyredis-cli
```

---

## Supported Commands

| Command  | Syntax             | Description                                     | Return Value               |
|----------|--------------------|-------------------------------------------------|----------------------------|
| `SET`    | `SET key value`    | Set key to hold string value                    | `OK`                       |
| `GET`    | `GET key`          | Get the value of key                            | Value string or `(nil)`    |
| `EXISTS` | `EXISTS key`       | Returns if key exists                           | `1` (exists) or `0`        |
| `DEL`    | `DEL key`          | Removes the specified key                       | `1` (deleted) or `0`       |

---

## Example

```text
tiny-redis:6379> SET name JohnDoe
OK

tiny-redis:6379> GET name
JohnDoe

tiny-redis:6379> EXISTS name
1

tiny-redis:6379> DEL name
1

tiny-redis:6379> GET name
(nil)

tiny-redis:6379> quit
```

---

## Project Structure

```text
tiny-redis
├── tinyredis-core        # Memory engine & thread-safe KeyValueStore
├── tinyredis-protocol    # RESP protocol parser, Command & Response models
├── tinyredis-server      # TCP socket server, multithreaded ConnectionHandler
├── tinyredis-cli         # Interactive terminal CLI client
└── docs/images           # Documentation assets and diagrams
```

---

## Roadmap

### v1.0
- Core TCP Server & multi-threaded socket handling
- RESP protocol parser and encoder/decoder
- In-memory KeyValueStore (`SET`, `GET`, `DEL`, `EXISTS`)
- Interactive CLI client (`tinyredis-cli`)

### v1.1
- Expiration (TTL & MinHeap priority queue expiration engine)
- LRU memory eviction policy

### v1.2
- Persistence (Write-Ahead Log & Snapshot Manager)

### v2.0
- Distributed cache & replication support
