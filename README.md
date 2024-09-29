# Graph Algorithms in Clojure

This project implements essential graph algorithms such as Dijkstra's algorithm and calculates graph properties like eccentricity, radius, and diameter for a directed weighted graph.

## Core Functions

- **`make-graph`**: Generates a random graph with `N` vertices and `S` edges.
- **`shortest-path`**: Finds the shortest path between two vertices, returning a list of nodes or `nil` if no path exists.
- **`eccentricity`**: Calculates the eccentricity of a vertex (the greatest distance from that vertex to any other reachable vertex).
- **`radius`**: Returns the smallest eccentricity across all vertices.
- **`diameter`**: Returns the largest eccentricity across all vertices. Note: If no path exists between vertices, the diameter may be infinite.

## Development Setup with Docker

To spin up the development environment, you can use Docker. Below are the steps:

1. Run the following Docker command in the project directory:
 - **For macOS:** `docker run -it -v $PWD:/app clojure bash`
 - **For Linux:** `docker run -it -v $(pwd):/app clojure bash`
2. Once inside the container, start the Clojure REPL with:
```
cd /app
lein repl
```
Now you can run the functions defined in the project.

## Example Usage
```
(def random-graph (make-graph 10 10))

(println (generate-graphviz random-graph))

(shortest-path random-graph (first (keys random-graph)) (last (keys random-graph))) ; => List of nodes or `nil`

(eccentricity random-graph (first (keys random-graph))) ; => Eccentricity for the first vertex

(radius random-graph) ; => Minimal eccentricity

(diameter random-graph) ; => Maximal eccentricity
```

## Visualizing the Graph
To visualize the generated graph, print the Graphviz code using generate-graphviz and paste the output into [Graphviz Online](https://dreampuf.github.io/GraphvizOnline)

Example:
`(println (generate-graphviz random-graph))`

## Notes
- If no path exists between two vertices, the result for shortest-path will be nil, and the diameter could be indefinite (infinite).
- Use a higher sparseness number to ensure better connectivity in the graph.

