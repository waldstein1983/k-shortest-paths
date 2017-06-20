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
            LinkedList<String> initPathEdgeIds = new LinkedList<>();
            for(Edge e : graph.getEdgeList()){
                if(e.getFromNode().equals(f.from) && e.getToNode().equals(f.to) && e.getCapacity() == 1000){
                    initPathEdgeIds.add(e.getId());
                }
            }
            Path p = new Path();
            p.setTotalCost(1000);
            p.setEdgeIds(initPathEdgeIds);
//            p.setEdgeIds();
            f.paths.add(p);
        }

        XPRB bcl = new XPRB();
        XPRBprob problem = bcl.newProb("MCF");      /* Create a new problem in BCL */
        XPRS.init();

        Map<Flow, XPRBctr> flowConstraints = new HashMap<>();
        Map<Flow, Double> flowDuals = new HashMap<>();
        Map<String, XPRBctr> edgeConstraints = new HashMap<>();
        Map<String, Double> edgeDuals = new HashMap<>();

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
            flowConstraints.put(f, problem.newCtr(expr.eql(f.volume)));
        }

        //arc capacity
        for (Edge edge : graph.getEdgeList()) {
            XPRBexpr expr = new XPRBexpr();
            for (Flow f : flows) {
                for (Path p : f.paths) {
                    if (p.getEdgeIds().contains(edge.getId())) {
//                    if (p.getEdges().contains(edge)) {
                        expr.add(x_f_p.get(f).get(p)).mul(f.volume);
                    }
                }
            }
            edgeConstraints.put(edge.getId(), problem.newCtr(expr.lEql(edge.getCapacity())));
        }

        problem.setSense(XPRB.MINIM);

//        for (p = 0; p < NProd; p++)           /* Print the solution values */
//            System.out.print(x[p].getName() + ":" + x[p].getSol() + " ");
//        System.out.println();
        boolean terminate = false;
        while(!terminate){

            problem.lpOptimize("");             /* Solve the LP-problem */
            System.out.println("Objective: " + problem.getObjVal());  /* Get objective value */
            XPRBbasis basis = problem.saveBasis();

            for (Flow f : flowConstraints.keySet()) {
                flowDuals.put(f, flowConstraints.get(f).getDual());
            }

            for (String edgeId : edgeConstraints.keySet()) {
                edgeDuals.put(edgeId, edgeConstraints.get(edgeId).getDual());
            }

            for (Edge e : graph.getEdgeList()) {
                e.setOriginWeight(e.getWeight());
                e.setWeight(e.getWeight() - edgeDuals.get(e.getId()));
            }

            boolean newPathFound = false;

            for (Flow f : flows) {
                Path sp = Dijkstra.shortestPath(graph, f.from, f.to);
                if (sp.getTotalCost() <= flowDuals.get(f)) {
                    newPathFound = true;
                    f.paths.add(sp);
                    x_f_p.get(f).put(sp, problem.newVar(0,100));
                    obj.add(x_f_p.get(f).get(sp)).mul(sp.getTotalCost());
                    flowConstraints.get(f).addTerm(x_f_p.get(f).get(sp), sp.getTotalCost());
                    for(String edgeId : sp.getEdgeIds()){
                        edgeConstraints.get(edgeId).addTerm(f.volume, x_f_p.get(f).get(sp));
                    }
                }
            }

            for (Edge e : graph.getEdgeList()) {
                e.setWeight(e.getOriginWeight());
            }

            if(!newPathFound)
                terminate = true;
            else{
                problem.loadMat();                    /* Reload the problem */
                problem.loadBasis(basis);             /* Load the saved basis */
            }
        }
    }

}
