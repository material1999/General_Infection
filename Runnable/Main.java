package Runnable;

import Graph.Edge;
import Graph.Network;
import Graph.Node;
import Graph.States;
import Reader.Read;
import Writer.Write;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.TreeSet;

public class Main {

    static String networkFilePath;
    static Network network;
    static int nodeNumber;
    static CSVWriter writer;
    static String resultPath;
    static double rand, mean;
    static int s, edges;

    static HashSet<TreeSet<Integer>> K = new HashSet() {
        @Override
        public String toString() {
            String ret = "";
            for (Object o: this) {
                ret += o + " ";
            }
            ret = ret.strip();
            return ret;
        }
    };
    static HashSet<TreeSet<Integer>> helper = new HashSet() {
        @Override
        public String toString() {
            String ret = "";
            for (Object o: this) {
                ret += o + " ";
            }
            ret = ret.strip();
            return ret;
        }
    };
    static HashSet<TreeSet<Integer>> J = new HashSet() {
        @Override
        public String toString() {
            String ret = "";
            for (Object o: this) {
                ret += o + " ";
            }
            ret = ret.strip();
            return ret;
        }
    };
    static TreeSet<Integer> L, deleteTemp;

    public static void infectionApproximation(CSVWriter writer) {
        for (int j = 1; j <= nodeNumber; j++) {
            for (Edge edge1: network.getNode(Integer.toString(j)).getOutlist()) {
                Write.WriteProbabilities(writer, Integer.parseInt(edge1.getOut().getId()), Integer.parseInt(edge1.getIn().getId()), edge1.getWeight());
                for (Edge edge2: edge1.getIn().getOutlist()) {
                    if (edge1.getOut() != edge2.getIn()) {
                        Write.WriteProbabilities(writer, Integer.parseInt(edge1.getOut().getId()), Integer.parseInt(edge2.getIn().getId()), edge1.getWeight() * edge2.getWeight());
                    }
                }
            }
        }
    }

    public static void infectionSimulation(CSVWriter writer) {
        for (int j = 1; j <= nodeNumber; j++) {
            network.deleteFv();
            for (int k = 1; k <= Parameters.sampleSize; k++) {
                network.notVisited();
                for (Edge edge1: network.getNode(Integer.toString(j)).getOutlist()) {
                    rand = Math.random();
                    if (edge1.getWeight() > rand) {
                        edge1.getIn().addFv();
                        edge1.getIn().setState(States.VISITED);
                        for (Edge edge2: edge1.getIn().getOutlist()) {
                            rand = Math.random();
                            if (edge2.getIn().getState() == States.NOTVISITED && edge2.getIn() != edge1.getOut() && edge2.getWeight() > rand) {
                                edge2.getIn().addFv();
                                edge2.getIn().setState(States.VISITED);
                            }
                        }
                    }
                }
            }
            for (Node node: network.getNodes()) {
                if (node.getFv() > 0) {
                    node.finalizefv(Parameters.sampleSize);
                    Write.WriteProbabilities(writer, j, Integer.parseInt(node.getId()), node.getFv());
                }
            }
        }
    }

    public static void addVertexIteration() {
        helper.clear();
        for (TreeSet<Integer> A : K) {

            //csak a szomszédokon végigmenni (neighbour)

            for (Node n : network.getNodes()) {
                L = new TreeSet<>();
                L.addAll(A);
                if (isConnected(L, Integer.parseInt(n.getId()))) {
                    L.add(Integer.parseInt(n.getId()));
                    //System.out.println("A: " + A);
                    //System.out.println("mean A: " + meanEdgeWeight(A));
                    //System.out.println("L: " + L);
                    //System.out.println("mean L: " + meanEdgeWeight(L));
                    if (meanEdgeWeight(L) > mean && !K.contains(L)) {
                        helper.add(L);
                        //System.out.println("added");
                    }
                    //System.out.println();
                }
            }
        }
    }

    public static void deleteSubset() {
        J.clear();
        J.addAll(helper);
        helper.clear();
        for (TreeSet<Integer> A : J) {
            int match;
            for (TreeSet<Integer> a : K) {
                deleteTemp = new TreeSet<>();
                match = 0;
                for (Integer integer : a) {
                    if (A.contains(integer)) {
                        match++;
                    }
                }
                if (match == a.size()) {
                    deleteTemp.addAll(a);
                    helper.add(deleteTemp);
                }
            }
        }
    }

    public static void communityFinder() {
        for (Node n : network.getNodes()) {
            TreeSet<Integer> tsi = new TreeSet<>();
            tsi.add(Integer.parseInt(n.getId()));
            K.add(tsi);
        }
        for (s = 2; s <= Parameters.maxCommunitySize; s++) {
            System.out.println("community size: " + s + " ...");
            addVertexIteration();
            deleteSubset();
            K.addAll(J);
            K.removeAll(helper);
        }
        Write.WriteCommunities(writer, K);
    }


    public static double meanEdgeWeight(TreeSet<Integer> vertices) {
        double meanWeight = 0;
        edges = 0;
        for (Integer v1 : vertices) {
            for (Integer v2 : vertices) {
                if (network.getEdge(v1, v2) != null) {
                    meanWeight += network.getEdge(v1, v2).getWeight();
                    edges++;
                }
            }
        }
        //meanWeight /= vertices.size();
        //if (edges > 0) meanWeight /= edges;
        if (edges < vertices.size()) return 0;
        meanWeight /= edges;
        return meanWeight;
    }

    public static double networkMeanEdgeWeight() {
        double meanWeight = 0;
        for (Node n1 : network.getNodes()) {
            for (Node n2 : network.getNodes()) {
                if (network.getEdge(Integer.parseInt(n1.getId()), Integer.parseInt(n2.getId())) != null) {
                    meanWeight += network.getEdge(Integer.parseInt(n1.getId()), Integer.parseInt(n2.getId())).getWeight();
                }
            }
        }
        meanWeight /= network.getEdges().size();
        return meanWeight;
    }

    public static boolean isConnected(TreeSet<Integer> vertices, Integer newVertex) {
        int counter = 0;
        for (Integer v : vertices) {
            if (network.getEdge(newVertex, v) != null) {
                counter++;
            }
        }
        int threshold = (int)Math.ceil(vertices.size()/2.0);
        if (vertices.size() == 1 && counter == 0) return false; else return counter >= threshold;
    }

    public static void main(String[] args) throws IOException {
        //for (int i = 1; i <= Parameters.fileCount; i++)
        for (int i = 0; i <= 0; i++) {
            networkFilePath = Parameters.networksFolder + i + "/edgeweighted.csv";
            network = Read.ReadCsv(networkFilePath);
            nodeNumber = network.getNodes().size();

            resultPath = "results/sim_inf/sim_inf_" + i + ".csv";

            ////////// Infection Simulation //////////
            /*
            writer = new CSVWriter(new FileWriter(resultPath), ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            writer.writeNext(new String[]{"V1,V2,edgeweight"}, true);
            infectionSimulation(writer);
            writer.close();
            System.out.println("siminf done");
            */

            ////////// Community Detection //////////
            network = Read.ReadCsv(resultPath);
            resultPath = "results/sim_com/sim_com_" + i + ".csv";
            writer = new CSVWriter(new FileWriter(resultPath), ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            mean = networkMeanEdgeWeight();
            communityFinder();
            writer.close();
        }
    }

}