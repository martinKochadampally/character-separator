package edu.iastate.cs311;

import java.util.*;

/**
 * WeightedAdjacencyList represents a directed weighted graph using a nested HashMap.
 * The outer map holds vertices as keys, and the inner maps represent edges and weights.
 *
 * @author martink5
 * @param <T> the vertex type
 */
public class WeightedAdjacencyList<T> implements WeightedGraph<T> {
    // Instance Variables
    /**
     * Maps each vertex to a map of its neighbors and the edge weights.
     */
    private HashMap<T, HashMap<T, Long>> adjacencyList;

    /**
     * Constructs a WeightedAdjacencyList from a list of vertices.
     * @param vertices the list of vertices to add to the graph.
     */
    public WeightedAdjacencyList(List<T> vertices) {
        adjacencyList = new HashMap<>();
        for (T vertex : vertices) {
            this.addVertex(vertex);
        }
    }


    /**
     * Adds the directed edge (u,v) to the graph. If the edge is already present, it should not be modified.
     * @param u The source vertex.
     * @param v The target vertex.
     * @param weight The weight of the edge (u,v).
     * @return True if the edge was added to the graph, false if 1) either u or v are not in the graph 2) the edge was already present.
     */
    @Override
    public boolean addEdge(T u, T v, int weight) {
        if (!hasVertex(u) || !hasVertex(v) || hasEdge(u, v) || weight < 0){
            return false;
        }
        else {
            adjacencyList.get(u).put(v, (long)weight);
            return true;
        }
    }

    /**
     * @param vertex A vertex to add to the graph.
     * @return False vertex was already in the graph, true otherwise.
     */
    @Override
    public boolean addVertex(T vertex) {
        if (hasVertex(vertex) || vertex == null) {
            return false;
        }
        else {
            HashMap<T, Long> edges = new HashMap<>();
            adjacencyList.put(vertex, edges);
            return true;
        }
    }

    /**
     * @return |V|
     */
    @Override
    public int getVertexCount() {
        return adjacencyList.size();
    }

    /**
     * @param v The name of a vertex.
     * @return True if v is in the graph, false otherwise.
     */
    @Override
    public boolean hasVertex(T v) {
        if (v == null) {
            return false;
        }
        return adjacencyList.containsKey(v);
    }

    /**
     * @return An Iterable of V.
     */
    @Override
    public Iterable<T> getVertices() {
        return adjacencyList.keySet(); // Returns a set of all the verticies in the graph.
    }

    /**
     * @return |E|
     */
    @Override
    public int getEdgeCount() {
        // Gets list of Verticies and makes and iterator for it.
        Iterable<T> V = getVertices();
        Iterator it = V.iterator();
        // Sets the number of edges to 0 for starting the sum.
        int num_edges = 0;

        while (it.hasNext()) {
            // Gets each vertex and checks how many edges it has.
            HashMap<T, Long> vertex = adjacencyList.get(it.next());
            if (vertex != null && !vertex.isEmpty()){
                num_edges += vertex.size();
            }
        }
        
        return num_edges;
    }

    /**
     * @param u The source of the edge.
     * @param v The target of the edge.
     * @return True if (u,v) is in the graph, false otherwise.
     */
    @Override
    public boolean hasEdge(T u, T v) {
        if (u == null || v == null) {
            return false;
        }
        if (adjacencyList.containsKey(u)){
            return adjacencyList.get(u).containsKey(v);
        }
        return false;
    }

    /**
     * @param u A vertex.
     * @return The neighbors of u in the weighted graph.
     */
    @Override
    public Iterable<T> getNeighbors(T u) {
        return adjacencyList.get(u).keySet();
    }

    /**
     * @param u
     * @param v
     * @return If there is an edge between u and v returns true and else returns false.
     */
    @Override
    public boolean areNeighbors(T u, T v) {
        return hasEdge(u,v);
    }

    /**
     * Uses Dijkstra's algorithm to find the (length of the) shortest path from s to all other reachable vertices in the graph.
     * If the graph contains negative edge weights, the algorithm should terminate, but the return value is undefined.
     * @param s The source vertex.
     * @return A Mapping from all reachable vertices to their distance from s. Unreachable vertices should NOT be included in the Map.
     */
    @Override
    public Map<T, Long> getShortestPaths(T s) {
        //If s is not in this Weighted Ajacency List
        if (!hasVertex(s))
            return Map.of();

        // Instantiating Min Heap: T will store the vertex and Long will store the distance. 
        PriorityQueue<Pair<T, Long>> minHeap = new PriorityQueue<>(new Comparator<Pair<T, Long>>(){
            @Override
            public int compare(Pair<T, Long> p1, Pair<T, Long> p2){
                return Long.compare(p1.getSecond(), p2.getSecond());
            }
        });
        // Stores visited nodes.
        HashSet<T> visited = new HashSet<>();
        // Stores distances
        Map<T, Long> shortestPathsMap = new HashMap<>();

        // Set distance for s as 0;
        minHeap.add(new Pair<>(s, 0L));
        shortestPathsMap.put(s, 0L);

        // While loop while the minHeap isn't empty
        while (!minHeap.isEmpty()) { 
            Pair<T, Long> current = minHeap.poll();
            T currVertex = current.getFirst();
            long currDist = current.getSecond();

            // Checking if this node is already explored, allowing us account for duplicates in the heap.
            if (visited.contains(currVertex)) 
                continue;
            visited.add(currVertex);

            // Looks at the neigbours of the given node and adds it to the queue. If it is already in there, it will add
            // a duplicate with the above function.
            Iterable<T> neighbors = this.getNeighbors(currVertex);
            for (T w : neighbors) {
                Long newDist = currDist + adjacencyList.get(currVertex).get(w);

                // Relaxing Nodes.
                if (!shortestPathsMap.containsKey(w) || shortestPathsMap.get(w) > newDist) {
                    shortestPathsMap.put(w, newDist);
                    minHeap.add(new Pair<>(w, newDist));
                }

            }
            
        }
        // Return Map with values and distances. DO NOT need to remove unreachable nodes, because they where never added in the first place.
        return shortestPathsMap;
    }
}