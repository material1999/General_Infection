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
import java.util.TreeSet;

public class Main {

    static String networkFilePath;
    static Network network;
    static int nodeNumber;
    static CSVWriter writer;
    static String resultPath;
    static double rand;
    static int level, max, m, b;
    static int minlevel = 1;
    static boolean intersect = false;
    static TreeSet<Integer> comm_nodes = new TreeSet() {
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
    static TreeSet<Integer> H = new TreeSet() {
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
    static TreeSet<Integer> B = new TreeSet() {
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

    public static void cliqueCommunityFinder() {
        for (int j = 1; j <= nodeNumber; j++) {
            level = 0;
            H.add(j);
            for (Node n : network.getNode(Integer.toString(j)).getOutneighbour()) {
                B.add(Integer.parseInt(n.getId()));
            }
            //System.out.println(H);
            System.out.println(B);
            cliqueFinder(H, B);
        }
    }

    public static void cliqueFinder(TreeSet<Integer> H, TreeSet<Integer> B) {
        max = 1;
        level++;
        m = H.last();
        //System.out.println(m);
        //System.out.println(B);
        for (Integer b: B) {
            max = 0;
            if (b > m) {
                H.add(b);
                TreeSet<Integer> helperSet = new TreeSet<>();
                TreeSet<Integer> intersectSet = new TreeSet<>();
                intersect = false;
                for (Node n: network.getNode(Integer.toString(b)).getOutneighbour()) {
                    helperSet.add(Integer.parseInt(n.getId()));
                    if (B.contains(Integer.parseInt(n.getId()))) {
                        intersect = true;
                        intersectSet.add(Integer.parseInt(n.getId()));
                    }
                }
                if (intersect) {
                    cliqueFinder(H, intersectSet);
                    if (level > minlevel) {
                        H.remove(b);
                        level--;
                        return;
                    }
                } else {
                    Write.WriteCommunities(writer, H);
                    H.remove(b);
                }
            }
        }
        if (max == 1) {
            Write.WriteCommunities(writer, H);
        }
        level--;
    }

    public static void main(String[] args) throws IOException {
        //for (int i = 1; i <= Parameters.fileCount; i++)
        for (int i = 0; i <= 0; i++) {
            networkFilePath = Parameters.networksFolder + i + "/edgeweighted.csv";
            network = Read.ReadCsv(networkFilePath);
            nodeNumber = network.getNodes().size();
            H.clear();
            B.clear();
            comm_nodes.clear();

            resultPath = "results/sim_inf/sim_inf_" + i + ".csv";
            writer = new CSVWriter(new FileWriter(resultPath), ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            infectionSimulation(writer);
            writer.close();

            resultPath = "results/sim_com/sim_com_" + i + ".csv";
            writer = new CSVWriter(new FileWriter(resultPath), ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            cliqueCommunityFinder();
            writer.close();
        }
    }

}