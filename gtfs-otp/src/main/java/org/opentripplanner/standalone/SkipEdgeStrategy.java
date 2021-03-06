package org.opentripplanner.standalone;

/**
 * Strategy interface to provide additional logic to decide if a given edge should not be considered
 * for traversal.
 *
 * @author bdferris
 */
public interface SkipEdgeStrategy {
    
    /**
     * @param origin the origin vertex
     * @param target the target vertex, may be null in an undirected search
     * @param current the current vertex
     * @param edge the current edge to potentially be skipped
     * @param spt the shortest path tree
     * @param traverseOptions the traverse options
     * @return true if the given edge should not be considered for traversal
     */
    public boolean shouldSkipEdge(Vertex origin, Vertex target, State current, Edge edge, ShortestPathTree spt,
            RoutingRequest traverseOptions);
}
