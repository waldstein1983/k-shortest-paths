package edu.ufl.cise.bsmock.graph;

/**
 * The Edge class implements standard properties and methods for a weighted edge in a directed graph.
 * <p>
 * Created by Brandon Smock on 6/19/15.
 */
public class Edge implements Cloneable {
    private String fromNode;
    private String toNode;
    private double weight;
    private double capacity;
    private double originWeight;

    public Edge() {
        this.fromNode = null;
        this.toNode = null;
        this.weight = Double.MAX_VALUE;
        this.originWeight = weight;
    }

    public Edge(String fromNode, String toNode, double weight) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.weight = weight;
        this.originWeight = weight;
    }

    public Edge(String fromNode, String toNode, double weight, double capacity) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.weight = weight;
        this.originWeight = weight;
        this.capacity = capacity;
    }

    public String getId(){
        return fromNode + "-" + toNode;
    }

    public String getFromNode() {
        return fromNode;
    }

    public void setFromNode(String fromNode) {
        this.fromNode = fromNode;
    }

    public String getToNode() {
        return toNode;
    }

    public void setToNode(String toNode) {
        this.toNode = toNode;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public Edge clone() {
        return new Edge(fromNode, toNode, weight);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(fromNode);
        sb.append(",");
        sb.append(toNode);
        sb.append("){");
        sb.append(weight);
        sb.append("}");

        return sb.toString();
    }

    public boolean equals(Edge edge2) {
        if (hasSameEndpoints(edge2) && weight == edge2.getWeight())
            return true;

        return false;
    }

    public boolean hasSameEndpoints(Edge edge2) {
        if (fromNode.equals(edge2.getFromNode()) && toNode.equals(edge2.getToNode()))
            return true;

        return false;
    }

    public double getOriginWeight() {
        return originWeight;
    }

    public void setOriginWeight(double originWeight) {
        this.originWeight = originWeight;
    }
}
