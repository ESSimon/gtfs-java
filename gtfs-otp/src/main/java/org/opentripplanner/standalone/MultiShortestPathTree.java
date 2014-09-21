package org.opentripplanner.standalone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiShortestPathTree extends AbstractShortestPathTree {
    
    private static final long serialVersionUID = MavenVersion.VERSION.getUID();
    
    private final Map<Vertex, List<State>> stateSets;
    
    public MultiShortestPathTree(RoutingRequest options) {
        super(options);
        this.stateSets = new IdentityHashMap<Vertex, List<State>>();
    }
    
    public Set<Vertex> getVertices() {
        return this.stateSets.keySet();
    }
    
    /****
     * {@link ShortestPathTree} Interface
     ****/
    
    @Override
    public boolean add(State newState) {
        Vertex vertex = newState.getVertex();
        List<State> states = this.stateSets.get(vertex);

        // if the vertex has no states, add one and return
        if (states == null) {
            states = new ArrayList<State>();
            this.stateSets.put(vertex, states);
            states.add(newState);
            return true;
        }

        // if the vertex has any states that dominate the new state, don't add the state
        // if the new state dominates any old states, remove them
        Iterator<State> it = states.iterator();
        while (it.hasNext()) {
            State oldState = it.next();
            // order is important, because in the case of a tie
            // we want to reject the new state
            if (dominates(oldState, newState)) { return false; }
            if (dominates(newState, oldState)) {
                it.remove();
            }
        }

        // any states remaining are codominent with the new state
        states.add(newState);
        return true;
    }
    
    public static boolean dominates(State thisState, State other) {
        if (other.weight == 0) { return false; }
        // Multi-state (bike rental, P+R) - no domination for different states
        if (thisState.isBikeRenting() != other.isBikeRenting()) { return false; }
        if (thisState.isCarParked() != other.isCarParked()) { return false; }
        
        if ((thisState.backEdge != other.getBackEdge())
                && ((thisState.backEdge instanceof PlainStreetEdge) && (!((PlainStreetEdge) thisState.backEdge)
                        .getTurnRestrictions().isEmpty()))) { return false; }
        
        if (thisState.routeSequenceSubset(other)) {
            // TODO subset is not really the right idea
            return (thisState.weight <= other.weight) && (thisState.getElapsedTimeSeconds() <= other.getElapsedTimeSeconds());
            // && this.getNumBoardings() <= other.getNumBoardings();
        }
        
        // If returning more than one result from GenericAStar, the search can be very slow
        // unless you replace the following code with:
        // return false;
        boolean walkDistanceBetter = thisState.walkDistance <= (other.getWalkDistance() * 1.05);
        double weightRatio = thisState.weight / other.weight;
        boolean weightBetter = ((weightRatio < 1.02) && ((thisState.weight - other.weight) < 30));
        boolean timeBetter = (thisState.getElapsedTimeSeconds() - other.getElapsedTimeSeconds()) <= 30;

        return walkDistanceBetter && weightBetter && timeBetter;
        // return this.weight < other.weight;
    }
    
    @Override
    public State getState(Vertex dest) {
        Collection<State> states = this.stateSets.get(dest);
        if (states == null) { return null; }
        State ret = null;
        for (State s : states) {
            if (((ret == null) || s.betterThan(ret)) && s.isFinal() && s.allPathParsersAccept()) {
                ret = s;
            }
        }
        return ret;
    }
    
    @Override
    public List<State> getStates(Vertex dest) {
        return this.stateSets.get(dest);
    }
    
    @Override
    public int getVertexCount() {
        return this.stateSets.keySet().size();
    }
    
    /**
     * Check that a state coming out of the queue is still in the Pareto-optimal set for this
     * vertex, which indicates that it has not been ruled out as a state on an optimal path. Many
     * shortest path algorithms will decrease the key of an entry in the priority queue when it is
     * updated, or remove it when it is dominated. When the Fibonacci heap was replaced with a
     * binary heap, the decrease-key operation was removed for the same reason: both improve
     * theoretical run time complexity, at the cost of high constant factors and more complex code.
     * So there can be dominated (useless) states in the queue. When they come out we want to ignore
     * them rather than spend time branching out from them.
     */
    @Override
    public boolean visit(State state) {
        boolean ret = false;
        for (State s : this.stateSets.get(state.getVertex())) {
            if (s == state) {
                ret = true;
                break;
            }
        }
        return ret;
    }
    
    public String toString() {
        return "MultiSPT(" + this.stateSets.size() + " vertices)";
    }
    
    @Override
    public Collection<State> getAllStates() {
        ArrayList<State> allStates = new ArrayList<State>();
        for (List<State> stateSet : this.stateSets.values()) {
            allStates.addAll(stateSet);
        }
        return allStates;
    }
    
}
