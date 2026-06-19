# MagicBeams

[![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)](https://www.java.com/)
[![Course](https://img.shields.io/badge/Course-ADA-4A90D9)](https://www.fct.unl.pt/)
[![University](https://img.shields.io/badge/University-NOVA%20FCT-004F9E)](https://www.fct.unl.pt/)

> Academic project for the **Análise e Desenho de Algoritmos** (Algorithm Analysis and Design) course at NOVA FCT · 2025/2026.

## Overview

In an enchanted kingdom, a rectangular grid of runes channels magic beams — streams of energy with a fixed direction and length that keep the city alive. A stabilising corridor of consecutive columns must be completely cleared to prevent a catastrophic collapse. Freeing a beam causes it to travel forever in its direction, so any beam in its path must be freed first, creating a chain of dependencies.

This project determines **which beams must be freed and in what exact order**, modelling the dependency structure as a directed graph and applying topological sorting to find a valid freeing sequence. Whenever multiple beams can be freed simultaneously, the one with the lowest identifier is always chosen first.

## Problem

A rune grid has **R** rows and **C** columns. Each magic beam occupies one or more cells starting from a given position and extending in one of four cardinal directions (N, S, E, W). To open a corridor spanning **N** consecutive columns (starting at column **L**), every beam that crosses those columns must be freed.

When a beam is freed it travels to infinity — any other beam in its path blocks it and must be freed first. The goal is to find a valid topological order for all beams that need to be freed.

**Possible outcomes:**

| Output | Meaning |
|--------|---------|
| `False alarm` | No beams occupy the chosen columns — nothing to do |
| `Disaster` | A circular dependency makes it impossible to clear the corridor |
| `b₁ b₂ … bₙ` | Space-separated beam identifiers in the order they should be freed |

### Example

```
     0  1  2  3  4  5  6  7
  0                 6
  1     ↥        ↧        ↥
  2           1  ↦              4
  3     5        3  ↤  2
  4           ↧
```

In this 5×8 grid with chosen columns 1, 2, 3 — beams 1, 3, 4, 5, and 6 must be freed (beam 2 can stay). Beam 4 must be freed before beam 1, and beam 1 before beam 6. The correct freeing order is: **3 4 1 5 6**.

## Algorithm

The solution models the problem as a **Directed Acyclic Graph (DAG)** and resolves it with a modified topological sort.

| Step | Description |
|------|-------------|
| **1. Grid population** | Each beam marks its occupied cells in a 2D integer matrix, storing the beam's identifier |
| **2. Relevant beam discovery** | Scan only the corridor columns to find beams that must be freed |
| **3. Dependency graph construction** | For each relevant beam, follow its direction across the entire grid to find beams that block it, adding a directed edge from blocker → blocked |
| **4. Topological sort** | Process beams with no remaining dependencies using a min-priority queue, ensuring the lowest-id beam is always freed first |
| **5. Cycle detection** | If the sort does not exhaust all relevant beams, a circular dependency exists → output `Disaster` |

The graph is built lazily — only the subgraph reachable from corridor beams is constructed, keeping both time and memory proportional to the actual dependency chain rather than the total number of beams.

## Project Structure

```
MagicBeams/
├── Main.java          # Entry point: parses input, drives test cases, formats output
└── MagicBeams.java    # Core logic: grid representation, graph construction, topological sort
```

## Getting Started

### Prerequisites

- Java 17 or higher

### Compile

```bash
javac Main.java MagicBeams.java
```

### Run

Provide input via stdin (file redirect or pipe):

```bash
java Main < input.txt
```

### Input Format

```
T                          ← number of test cases
R C                        ← grid dimensions (rows × columns)
N L                        ← corridor: N columns wide, starting at column L
B                          ← number of beams
r₁ c₁ l₁ d₁               ← beam 1: start row, start col, length, direction (N/S/E/W)
...
rB cB lB dB                ← beam B
```

### Sample Input / Output

**Input:**
```
2
5 8
3 1
6
2 2 2 E
3 7 4 W
3 3 2 S
2 4 2 N
3 1 3 N
0 2 2 S
10 10
1 4
4
5 6 2 W
6 3 3 N
3 3 2 E
2 5 3 S
```

**Output:**
```
3 4 1 5 6
Disaster
```

## Constraints

| Parameter | Range | Description |
|-----------|-------|-------------|
| T | 1 – 12 | Number of test cases |
| R, C | 2 – 200 | Grid dimensions |
| N | 1 – 10 | Corridor width (number of chosen columns) |
| B | 1 – 10 060 | Number of magic beams |
| l | 1 – 20 | Length of each beam (cells occupied) |

## Authors

- **Lourenço Beato** — 68461
- **Tomás Sousa** — 68302

---

*NOVA FCT · Análise e Desenho de Algoritmos · 2025/2026*
