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
    static double rand;

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
            for (Node n : network.getNodes()) {
                L = new TreeSet<>(A);
                L.add(Integer.parseInt(n.getId()));
                helper.add(L);
            }
        }
    }

    public static void deleteSubset() {
        helper.clear();
        for (TreeSet<Integer> A : K) {
            int match;
            for (TreeSet<Integer> a : K) {
                deleteTemp = new TreeSet<>();
                if (a != A) {
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
    }

    public static void communityFinder() {
        for (Node n : network.getNodes()) {
            TreeSet<Integer> tsi = new TreeSet<>();
            tsi.add(Integer.parseInt(n.getId()));
            K.add(tsi);
        }
        for (int s = 1; s < Parameters.maxCommunitySize; s++) {
            addVertexIteration();
            K.addAll(helper);
            deleteSubset();
            K.removeAll(helper);
        }
        Write.WriteCommunities(writer, K);
    }

    public static double meanEdgeWeight(TreeSet<Integer> vertices) {
        double meanWeight = 0;
        for (Integer v1 : vertices) {
            for (Integer v2 : vertices) {
                if (network.getEdge(v1, v2) != null) {
                    meanWeight += network.getEdge(v1, v2).getWeight();
                }
            }
        }
        meanWeight /= vertices.size();
        return  meanWeight;
    }

    public static void main(String[] args) throws IOException {
        //for (int i = 1; i <= Parameters.fileCount; i++)
        for (int i = 1081; i <= 1081; i++) {
            networkFilePath = Parameters.networksFolder + i + "/edgeweighted.csv";
            network = Read.ReadCsv(networkFilePath);
            nodeNumber = network.getNodes().size();

            resultPath = "results/sim_inf/sim_inf_" + i + ".csv";
            /*
            writer = new CSVWriter(new FileWriter(resultPath), ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            writer.writeNext(new String[]{"\"V1\";\"V2\";\"edgeweight\""});
            infectionSimulation(writer);
            writer.close();
            */

            network = Read.ReadCsv(resultPath);
            resultPath = "results/sim_com/sim_com_" + i + ".csv";
            writer = new CSVWriter(new FileWriter(resultPath), ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            communityFinder();
            writer.close();
        }
    }

}