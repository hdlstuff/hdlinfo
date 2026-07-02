# Project Understanding

This document captures the current shared understanding of `hdlinfo` so we can discuss changes from the same baseline.

## What This Repository Does

`hdlinfo` defines a JSON-based metadata format for describing hardware module interfaces. Files use the `.hdlinfo.json` extension and describe:

- a hardware module name
- scalar or bus ports such as clocks, resets, data, and interrupts
- higher-level protocol interfaces such as AXI4 and AXI4-Stream
- extensible typed arguments attached to modules, ports, and interfaces

The intended consumers are code generators or integration tools that need a uniform description of HDL module boundaries without parsing HDL source directly.

## Core Data Model

The central schema is:

- `Module`: top-level object with `name`, `ports`, `interfaces`, and free-form typed `args`
- `Port`: named signal with direction, kind, sensitivity, bus range, frequency, associated clock/reset, and typed `args`
- `Interface`: named protocol-level endpoint with role, kind, associated clock/reset, and typed `args`
- `TypedObject`: wrapper for extensible values stored as `{ "typeName": ..., "obj": ... }`, plus `"null"` for empty values

The string values in JSON are intentionally simple and stable. Examples include port directions like `input` and `output`, port kinds like `clock`, `reset`, `data`, and `interrupt`, and interface roles like `master`, `slave`, `producer`, and `consumer`.

## Language Bindings

The repository currently provides two implementations of the same general format:

- Python package under `python/src/hdlinfo`
- Scala package under `scala/src/main/scala/hdlinfo`

The Python binding uses `dataclasses`, `dataclasses-json`, and a registry for typed extension objects. It exposes helpers for JSON/dict conversion and module file I/O.

The Scala binding uses Circe encoders/decoders and a registry for typed extension objects. It models the same top-level concepts with case classes and string-backed wrapper types.

## Built-In Protocol Support

The repository includes protocol configuration helpers for:

- AMBA AXI4: `axi4.Config`
- AMBA AXI4-Stream: `axi4s.Config`
- Ready/valid interface kind naming via `readyValid$<bundleName>`

Protocol config objects are usually placed in an interface `args` map under the key `config`.

## Important Current Nuances

The Python and Scala bindings are similar but not perfectly identical today:

- AMBA protocol configs should track the matching Chext/Chisel definitions. Python AXI4S keeps `hasReady` as an HDLINFO extension.
- Python AXI4 and AXI4-Stream configs expose derived `signals` lists; Scala protocol configs expose fewer derived helpers.
- Python accepts unknown typed object names by warning and decoding as `dict`; Scala treats unknown typed object names as decode failures.
- Default associated clock/reset values differ in places: Python defaults to empty strings, while Scala often defaults to `"clock"` and `"reset"`.

These differences may be intentional, transitional, or areas to align.

## Packaging And Tests

Python packaging is defined by `python/setup.py`, with dependency `dataclasses-json` and Python `>=3.9`.

Scala packaging is defined by `scala/build.sbt`, targeting Scala `2.13.12` and Circe `0.14.1`.

Current tests/examples are lightweight:

- Python: `python/test/test.py` reads and writes example `.hdlinfo.json` files.
- Scala: `scala/src/test/scala/hdlinfo/test1.scala` constructs and prints a sample module as JSON.

## Discussion Points

- Should the JSON schema be formally documented or versioned?
- Which implementation is canonical when Python and Scala behavior differ?
- Should protocol configs be fully aligned between Python and Scala?
- Should unknown typed objects be preserved, rejected, or decoded as dictionaries consistently?
- Should defaults for `associatedClock` and `associatedReset` be standardized?
- Should there be round-trip compatibility tests between Python-generated and Scala-generated JSON?
