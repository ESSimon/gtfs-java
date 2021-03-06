package org.opentripplanner.standalone;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * A vertex in the graph. Each vertex has a longitude/latitude location, as well as a set of
 * incoming and outgoing edges.
 */
public abstract class Vertex implements Serializable, Cloneable {
    private static final long serialVersionUID = MavenVersion.VERSION.getUID();
    
    private static final Logger LOG = LoggerFactory.getLogger(Vertex.class);
    
    private static int maxIndex = 0;
    
    private int index;

    private int groupIndex = -1;
    
    /* short debugging name */
    private final String label;

    /* Longer human-readable name for the client */
    private String name;
    
    private final double x;
    
    private final double y;

    private double distanceToNearestTransitStop = 0;
    
    private transient Set<Edge> incoming = new CopyOnWriteArraySet<Edge>();
    
    private transient Set<Edge> outgoing = new CopyOnWriteArraySet<Edge>();
    
    /* PUBLIC CONSTRUCTORS */
    
    public Vertex(Graph g, String label, double x, double y) {
        this.label = label;
        this.x = x;
        this.y = y;
        this.index = maxIndex++;
        // null graph means temporary vertex
        if (g != null) {
            g.addVertex(this);
        }
        this.name = "(no name provided)";
    }
    
    protected Vertex(Graph g, String label, double x, double y, String name) {
        this(g, label, x, y);
        this.name = name;
    }
    
    /* PUBLIC METHODS */
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(this.getLabel());
        if (this.getCoordinate() != null) {
            sb.append(" lat,lng=").append(this.getCoordinate().y);
            sb.append(",").append(this.getCoordinate().x);
        }
        sb.append(">");
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        return this.index;
    }
    
    /* FIELD ACCESSOR METHODS : READ/WRITE */
    
    public void addOutgoing(Edge ee) {
        if (this.outgoing.contains(ee)) {
            LOG.error("repeatedly added edge {} to vertex {}", ee, this);
        } else {
            this.outgoing.add(ee);
        }
    }
    
    public boolean removeOutgoing(Edge ee) {
        if (!this.outgoing.contains(ee)) {
            LOG.error("Removing edge which isn't connected to this vertex");
        }
        boolean removed = this.outgoing.remove(ee);
        if (this.outgoing.contains(ee)) {
            LOG.error("edge {} still in edgelist of {} after removed. there must have been multiple copies.");
        }
        return removed;
    }
    
    /** Get a collection containing all the edges leading from this vertex to other vertices. */
    public Collection<Edge> getOutgoing() {
        return this.outgoing;
    }
    
    public void addIncoming(Edge ee) {
        if (this.incoming.contains(ee)) {
            LOG.error("repeatedly added edge {} to vertex {}", ee, this);
        } else {
            this.incoming.add(ee);
        }
    }
    
    public boolean removeIncoming(Edge ee) {
        if (!this.incoming.contains(ee)) {
            LOG.error("Removing edge which isn't connected to this vertex");
        }
        boolean removed = this.incoming.remove(ee);
        if (this.incoming.contains(ee)) {
            LOG.error("edge {} still in edgelist of {} after removed. there must have been multiple copies.");
        }
        return removed;
    }
    
    /** Get a collection containing all the edges leading from other vertices to this vertex. */
    public Collection<Edge> getIncoming() {
        return this.incoming;
    }
    
    @XmlTransient
    public int getDegreeOut() {
        return this.outgoing.size();
    }
    
    @XmlTransient
    public int getDegreeIn() {
        return this.incoming.size();
    }

    // TODO: this is a candidate for no-arg message-passing style
    public void setDistanceToNearestTransitStop(double distance) {
        this.distanceToNearestTransitStop = distance;
    }
    
    /** Get the distance from this vertex to the closest transit stop in meters. */
    public double getDistanceToNearestTransitStop() {
        return this.distanceToNearestTransitStop;
    }
    
    /** Get the longitude of the vertex */
    public double getX() {
        return this.x;
    }
    
    /** Get the latitude of the vertex */
    public double getY() {
        return this.y;
    }
    
    /** Get the longitude of the vertex */
    public double getLon() {
        return this.x;
    }
    
    /** Get the latitude of the vertex */
    public double getLat() {
        return this.y;
    }
    
    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }
    
    @XmlTransient
    public int getGroupIndex() {
        return this.groupIndex;
    }
    
    /** If this vertex is located on only one street, get that street's name. */
    public String getName() {
        return this.name;
    }
    
    public void setStreetName(String name) {
        this.name = name;
    }
    
    /* FIELD ACCESSOR METHODS : READ ONLY */
    
    /** Every vertex has a label which is globally unique. */
    public String getLabel() {
        return this.label;
    }
    
    @XmlTransient
    public Coordinate getCoordinate() {
        return new Coordinate(getX(), getY());
    }
    
    /** Get the bearing, in degrees, between this vertex and another coordinate. */
    public double azimuthTo(Coordinate other) {
        return DirectionUtils.getAzimuth(getCoordinate(), other);
    }
    
    /** Get the bearing, in degrees, between this vertex and another. */
    public double azimuthTo(Vertex other) {
        return azimuthTo(other.getCoordinate());
    }
    
    /** Get this vertex's unique index, that can serve as a hashcode or an index into a table */
    @XmlTransient
    public int getIndex() {
        return this.index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public static int getMaxIndex() {
        return maxIndex;
    }
    
    /* SERIALIZATION METHODS */
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        // edge lists are transient
        out.defaultWriteObject();
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.incoming = new CopyOnWriteArraySet<Edge>();
        this.outgoing = new CopyOnWriteArraySet<Edge>();
        this.index = maxIndex++;
    }
    
    /* UTILITY METHODS FOR SEARCHING, GRAPH BUILDING, AND GENERATING WALKSTEPS */
    
    @XmlTransient
    public List<Edge> getOutgoingStreetEdges() {
        List<Edge> result = new ArrayList<Edge>();
        for (Edge out : this.getOutgoing()) {
            if (!(out instanceof StreetEdge)) {
                continue;
            }
            result.add(out);
        }
        return result;
    }
    
    /**
     * Clear this vertex's outgoing and incoming edge lists, and remove all the edges they contained
     * from this vertex's neighbors.
     */
    public void removeAllEdges() {
        for (Edge e : this.outgoing) {
            Vertex target = e.getToVertex();
            if (target != null) {
                target.removeIncoming(e);
            }
        }
        for (Edge e : this.incoming) {
            Vertex source = e.getFromVertex();
            if (source != null) {
                source.removeOutgoing(e);
            }
        }
        this.incoming = new CopyOnWriteArraySet<Edge>();
        this.outgoing = new CopyOnWriteArraySet<Edge>();
    }
    
    /* GRAPH COHERENCY AND TYPE CHECKING */
    
    // Parameterized Class<? extends Edge) gets ugly fast here
    @SuppressWarnings("unchecked")
    private static final ValidEdgeTypes VALID_EDGE_TYPES = new ValidEdgeTypes(Edge.class);
    
    @XmlTransient
    public ValidEdgeTypes getValidOutgoingEdgeTypes() {
        return VALID_EDGE_TYPES;
    }
    
    @XmlTransient
    public ValidEdgeTypes getValidIncomingEdgeTypes() {
        return VALID_EDGE_TYPES;
    }
    
    /**
     * Check that all of this Vertex's incoming and outgoing edges are of the proper types. This may
     * not be necessary if edge constructor types are strictly specified and addOutgoing is
     * protected
     */
    public boolean edgeTypesValid() {
        ValidEdgeTypes validOutgoingTypes = getValidOutgoingEdgeTypes();
        for (Edge e : getOutgoing()) {
            if (!validOutgoingTypes.isValid(e)) { return false; }
        }
        ValidEdgeTypes validIncomingTypes = getValidIncomingEdgeTypes();
        for (Edge e : getIncoming()) {
            if (!validIncomingTypes.isValid(e)) { return false; }
        }
        return true;
    }
    
    public static final class ValidEdgeTypes {
        private final Class<? extends Edge>[] classes;
        
        // varargs constructor:
        // a loophole in the law against arrays/collections of parameterized generics
        public ValidEdgeTypes(Class<? extends Edge>... classes) {
            this.classes = classes;
        }
        
        public boolean isValid(Edge e) {
            for (Class<? extends Edge> c : this.classes) {
                if (c.isInstance(e)) { return true; }
            }
            return false;
        }
    }
    
    /**
     * Clean up before garbage collection. Usually this method does nothing, but temporary vertices
     * must provide a method to remove their associated temporary edges from adjacent vertices' edge
     * lists, usually by simply calling detach() on them.
     * 
     * @return the number of edges affected by the cleanup.
     */
    public int removeTemporaryEdges() {
        // do nothing, signal 0 other objects affected
        return 0;
    }
}
