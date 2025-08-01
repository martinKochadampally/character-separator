package edu.iastate.cs311;

public interface WeightedGraph<T> extends Graph<T> {
    public boolean addEdge(T u, T v, int weight);

    @Override
    public default boolean addEdge(T u, T v) {
        return addEdge(u, v, 1);
    }
}
