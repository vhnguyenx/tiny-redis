# TinyRedis

**TinyRedis** is an in-memory, key-value database built from scratch in pure **Java 21**. It serves as a learning and portfolio project designed to demonstrate core computer science principles, software architecture, data structures, concurrency, persistence, and performance benchmarking without relying on external storage frameworks.

---

## 🎯 Project Goal

The objective of TinyRedis is to construct a lightweight, high-performance, Redis-inspired database using **Java standard libraries only**.

Key concepts demonstrated in this project:
- **Java Fundamentals & Clean Architecture**
- **Custom Data Structures** (Min-Heap, Doubly Linked List, LRU Cache)
- **Networking & Transport** (TCP Sockets, Connection Management)
- **Protocol Parsing** (Custom Redis-like Text/Binary Protocol)
- **Concurrency & Multithreading** (Thread Pools, Concurrent Memory Access)
- **TTL & Expiration** (Min-Heap Priority Queue Expiration Engine)
- **Eviction Policies** (LRU Memory Bound Management)
- **Persistence & Recovery** (Write-Ahead Log, In-Memory Snapshotting)
- **Benchmarking** (JMH Microbenchmarks)

---

## 🏗 High-Level Architecture

```
TinyRedis
├── Transport Layer      (TCP Server & Client Socket Handlers)
├── Protocol Layer       (Command Deserializer & Response Serializer)
├── Core Engine          (KeyValue Store & State Coordinator)
├── Data Structures      (Custom MinHeap, DoublyLinkedList, LruCache)
├── Expiration           (Expiration Manager & Timed Cleanups)
├── Eviction             (LRU Eviction Engine)
├── Persistence          (Write-Ahead Log & Snapshot Manager)
└── CLI                  (Interactive Terminal Client)
```

### Package Layout

```
src/
├── main/
│   └── java/
│       └── com/
│           └── tinyredis/
│               ├── server/         # TCP server, connection handling
│               ├── protocol/       # Protocol parsing & command models
│               ├── core/           # Memory engine & KV store
│               ├── datastructure/  # Custom data structures (MinHeap, DoublyLinkedList, LRU)
│               ├── expiration/     # TTL & Key Expiration engine
│               ├── persistence/    # WAL & Snapshot persistence
│               └── cli/            # CLI client entry point
├── test/
│   └── java/
│       └── com/
│           └── tinyredis/          # Unit and Integration Tests (JUnit 5)
└── benchmark/
    └── java/
        └── com/
            └── tinyredis/          # Performance Microbenchmarks (JMH)
```

---

## 💡 Design Principles

1. **Pure Java Core**: Standard Java 21 standard library only; no Spring, Netty, Guava, or third-party storage libraries.
2. **Decoupled Architecture**: Storage engine and protocol parser are isolated from the networking layer for independent testability.
3. **Manual Data Structure Implementations**: Core structures (Min-Heap, LRU Doubly Linked List) are coded manually for educational clarity.
4. **Composition over Inheritance**: Modular components composed together with clean responsibilities.
5. **No Premature Abstraction**: Minimal, focused classes without empty interfaces or excessive design patterns.

---

## 🛠 Building and Testing

### Prerequisites
- **Java 21** or higher
- **Maven 3.9+**

### Build
```bash
mvn clean compile
```

### Run Unit Tests
```bash
mvn test
```

### Run Server (Placeholder)
```bash
mvn exec:java -Dexec.mainClass="com.tinyredis.server.RedisServer"
```
