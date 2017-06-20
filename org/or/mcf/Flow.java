package org.or.mcf;

import edu.ufl.cise.bsmock.graph.Node;
import edu.ufl.cise.bsmock.graph.util.Path;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by baohuaw on 6/20/17.
 */
public class Flow {
    String from;
    String to;
    double volume;
    List<Path> paths;

    public Flow(String from, String to, double volume) {
        this.from = from;
        this.to = to;
        this.volume = volume;
        paths = new LinkedList<>();
    }
}
