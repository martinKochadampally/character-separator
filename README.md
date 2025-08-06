# Character Separator

> **Notice:** This project was developed as part of a **homework assignment** for CS 3110 at Iowa State University.

This project implements an algorithm to detect horizontal and vertical whitespace boundaries in bitmap images of text. It uses a **weighted directed graph** representation of image pixels and applies **Dijkstra’s shortest path algorithm** to determine continuous whitespace rows and columns.

---

## Overview

- **Input**: Bitmap image (`.bmp`) containing black and white pixels representing text.
- **Process**:
  - Convert image pixels into a graph (`WeightedAdjacencyList`) where each pixel is a vertex.
  - Assign edge weights (low weight for white-to-white transitions, high weight for others).
  - Use Dijkstra’s algorithm twice (horizontal and vertical passes) to identify uninterrupted whitespace.
- **Output**: Two lists:
  - Whitespace **rows**
  - Whitespace **columns**

---

## Features

- **Graph Representation**:
  - Custom `WeightedAdjacencyList` implementation using nested `HashMap<T, HashMap<T, Long>>`.
  - Supports vertices, edges, and shortest path queries.

- **Dijkstra’s Algorithm**:
  - Finds shortest paths efficiently using a priority queue (`O((V+E) log V)`).

- **Bitmap Processing**:
  - Reads `.bmp` images and converts them into 2D RGB arrays for graph construction.

- **Test Images**:
  - Includes multiple `.bmp` test images (e.g., `all_white.bmp`, `concentric_black_squares.bmp`) for validation.

---

## Project Structure

```
.
├── README.md
├── pom.xml
└── src/main/java/edu/iastate/cs311
    ├── BitmapProcessor.java        # Converts BMP images into RGB matrices
    ├── CharacterSeparator.java     # Main whitespace detection logic
    ├── WeightedAdjacencyList.java  # Graph representation
    ├── WeightedGraph.java          # Graph interface
    ├── Graph.java                  # Additional graph utilities
    ├── Pair.java                    # Simple pair class for coordinates
    ├── Hw4Tests.java               # Test cases
    └── resources/                  # Test images
```

---

## How It Works

1. **Load Image**  
   `BitmapProcessor` reads a `.bmp` file and outputs a `rows x columns` matrix of RGB values.

2. **Graph Construction**  
   Each pixel becomes a vertex. Edges connect 4-neighboring pixels (up, down, left, right) with weights:
   - `1` if both pixels are white
   - `100` otherwise

3. **Run Dijkstra**  
   - Run once horizontally (left dummy node → right edge)
   - Run once vertically (top dummy node → bottom edge)

4. **Extract Whitespace**  
   Rows/columns where the shortest path equals the image width/height are considered whitespace.

---

## Dependencies

- Java 17+ (or version required by your course)
- Maven (for build management)

---

## Building & Running

### Compile
```bash
mvn compile
```

### Run Tests
```bash
mvn test
```

### Example Usage
Place your `.bmp` file in `resources/` and call:
```java
Pair<List<Integer>, List<Integer>> whitespace = CharacterSeparator.findSeparationWeighted("path/to/image.bmp");
```

---

## Resources

Test images are included in `src/main/java/edu/iastate/cs311/resources/`:
- `all_white.bmp`
- `all_black.bmp`
- `every_5th_row_white.bmp`
- `concentric_black_squares.bmp`
- And more.

---

## Author

**Martin Kochadampally**  
CS 3110 - Iowa State University
