package org.or.mcf;

import com.dashoptimization.*;
import edu.ufl.cise.bsmock.graph.Edge;
import edu.ufl.cise.bsmock.graph.Graph;
import edu.ufl.cise.bsmock.graph.util.Dijkstra;
import edu.ufl.cise.bsmock.graph.util.Path;

import java.util.*;

/**
 * Created by baohuaw on 6/20/17.
 */
public class MultiCommodityFlow {
    public static void main(String[] args) throws Exception {
        String file = "org/or/mcf/tiny_network_01";
        Graph graph = new Graph(file);
        List<Flow> flows = new ArrayList<>();
        flows.add(new Flow("1", "5", 12));
        flows.add(new Flow("1", "8", 10));


        for (Flow f : flows) {
            LinkedList<Edge> initPathEdges =new LinkedList<>();
            for(Edge e : graph.getEdgeList()){
                if(e.getFromNode().equals(f.from) && e.getToNode().equals(f.to) && e.getCapacity() == 1000){
                    initPathEdges.add(e);
                }
            }
            f.paths.add(new Path(initPathEdges, 1000));
        }

        XPRB bcl = new XPRB();
        XPRBprob problem = bcl.newProb("MCF");      /* Create a new problem in BCL */
        XPRS.init();

        Map<String, XPRBctr> constraints = new HashMap<>();

//        XPRBvar[][] x_f_p = new XPRBvar[flows.size()][];
        Map<Flow, Map<Path, XPRBvar>> x_f_p = new HashMap<>();
        for (Flow f : flows) {
            if (!x_f_p.containsKey(f))
                x_f_p.put(f, new HashMap<>());

            for (Path p : f.paths) {
                x_f_p.get(f).put(p, problem.newVar(0, 100));
            }
        }

        //obj
        XPRBexpr obj = new XPRBexpr();
        for (Flow f : flows) {

            for (Path p : f.paths) {
                obj.add(x_f_p.get(f).get(p)).mul(p.getTotalCost());
            }
        }
        problem.setObj(problem.newCtr("Obj", obj));

        //flow conservation
        for (Flow f : flows) {
            XPRBexpr expr = new XPRBexpr();
            for (Path p : f.paths) {
                expr.add(x_f_p.get(f).get(p));
            }
            constraints.put("Flow Conservation", problem.newCtr(expr.eql(f.volume)));
        }

        //arc capacity
        for (Edge edge : graph.getEdgeList()) {
            XPRBexpr expr = new XPRBexpr();
            for (Flow f : flows) {
                for (Path p : f.paths) {
                    if (p.getEdges().contains(edge)) {
                        expr.add(x_f_p.get(f).get(p));
                    }
                }
            }
            constraints.put("Edge capacity", problem.newCtr(expr.lEql(edge.getCapacity())));
        }

    }

}
