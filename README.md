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
tiny-redis:6379> SET name Nguyen
OK

tiny-redis:6379> GET name
Nguyen

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
