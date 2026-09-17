# Changelog

All notable changes to the **TinyRedis** project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-09-17

### Added
- **Multi-threaded TCP Server**: Native TCP socket server handling concurrent client connections in `tinyredis-server`.
- **RESP Protocol Engine**: Full support for Redis Serialization Protocol (`+OK`, `$length`, `:integer`, `$-1` null) in `tinyredis-protocol`.
- **Thread-safe KeyValueStore Engine**: High-performance in-memory key-value store powered by `ConcurrentHashMap` in `tinyredis-core`.
- **Interactive CLI Client**: Branded command-line terminal client (`tiny-redis:6379>`) in `tinyredis-cli`.
- **Core Commands**: Support for `SET`, `GET`, `DEL`, and `EXISTS` commands.
- **Automated Test Suite**: Full unit tests, End-to-End integration tests (`TinyRedisEndToEndTest`, `TinyRedisCliEndToEndTest`), and High-Concurrency Stress Test suite (`TinyRedisConcurrencyStressTest`).
- **Documentation**: Comprehensive `README.md` with architecture diagram, quick start guide, supported commands table, and version roadmap.
