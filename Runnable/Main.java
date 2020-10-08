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

public class Main {

    static String networkFilePath;
    static Network network;
    static int nodeNumber;
    static CSVWriter writer;
    static String resultPath;
    static double rand;

    static int level, max, m, b, lastIndex;
    static int minlevel = 1;
    static boolean intersect = false;
    static Boolean[] H, B;

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

    /*
    public static void cliqueCommunityFinder() {
        //for (int j = 1; j <=nodeNumber; j++) {
        for (int j = 1; j <=1; j++) {
            level = 0;
            int size;
            H.clear();
            B.clear();
            H.add(j);
            for (Node n : network.getNode(Integer.toString(j)).getOutneighbour()) {
                B.add(Integer.parseInt(n.getId()));
            }
            System.out.println(H);
            System.out.println(B);
            System.out.println();
            for (Integer b: B) {
                size = 0;
                for (int h : H) {
                    ArrayList<Edge> edges = network.getEdges();
                    for (Edge edge : edges) {
                        if (Integer.parseInt(edge.getIn().getId()) == h && Integer.parseInt(edge.getOut().getId()) == b) {
                            System.out.println(edge);
                            size++;
                        }
                    }
                }
                if (size == H.size()) {
                    H.add(b);
                    System.out.println(H);
                    System.out.println(B);
                    System.out.println();
                }

            }
        }
    }
    */

    public static void cliqueCommunityFinder() {
        for (int j = 1; j <= nodeNumber; j++) {
            level = 0;
            for (int z = 0; z < H.length; z++) {
                B[z] = false;
            }
            for (int z = 0; z < H.length; z++) {
                H[z] = false;
            }
            H[j - 1] = true;
            for (Node n : network.getNode(Integer.toString(j)).getOutneighbour()) {
                B[Integer.parseInt(n.getId()) - 1] = true;
            }
            /*
            for (Boolean bool : H) {
                System.out.print(bool + " ");
            }
            System.out.println();
            for (Boolean bool : B) {
                System.out.print(bool + " ");
            }
            System.out.println();
            System.out.println();
            */
            cliqueFinder(H, B);
        }
    }

    public static void cliqueFinder(Boolean[] H, Boolean[] B) {
        max = 1;
        level++;
        for (int y = 0; y < nodeNumber; y++) {
            if (H[y]) lastIndex = y;
        }
        m = lastIndex + 1;
        //System.out.println("m: " + m);
        for (int x = 0; x < B.length; x++) {
            max = 0;

            if (B[x] && x + 1 > m) {
                H[x] = true;
                for (Boolean bool : H) {
                    System.out.print(bool + " ");
                }
                System.out.println();
                for (Boolean bool : B) {
                    System.out.print(bool + " ");
                }
                System.out.println();
                System.out.println();
                intersect = false;
                Boolean[] intersectArray = new Boolean[nodeNumber];
                for (int z = 0; z < intersectArray.length; z++) {
                    intersectArray[z] = false;
                }
                for (Node n : network.getNode(Integer.toString(x + 1)).getOutneighbour()) {
                    if (B[Integer.parseInt(n.getId()) - 1]) {
                        intersectArray[Integer.parseInt(n.getId()) - 1] = true;
                        intersect = true;
                    }
                }
                /*
                System.out.println("m: " + m);
                System.out.println("b: " + x);
                System.out.println("H: " + H);
                System.out.println("B: " + B);
                System.out.println("intersect: " + intersectArray);
                System.out.println("level: " + level);
                System.out.println();
                */

                if (intersect) {
                    cliqueFinder(H, intersectArray);
                    if (level > minlevel) {
                        H[b] = true;
                        //System.out.println("H: " + H);
                        level--;
                        //System.out.println("level: " + level);
                        //System.out.println();
                        return;
                    }
                } else {
                    //System.out.println(intersect);
                    //System.out.println();
                    Write.WriteCommunities(writer, H);
                    System.out.println("kiirva fajlba");
                    H[b] = false;
                    System.out.println("H: " + H);
                    System.out.println();
                }
            }
        }
        level--;
    }


    public static void main(String[] args) throws IOException {
        //for (int i = 1; i <= Parameters.fileCount; i++)
        for (int i = 1081; i <= 1081; i++) {
            networkFilePath = Parameters.networksFolder + i + "/edgeweighted.csv";
            network = Read.ReadCsv(networkFilePath);
            nodeNumber = network.getNodes().size();
            H = new Boolean[nodeNumber];
            B = new Boolean[nodeNumber];
            for (int z = 0; z < H.length; z++) {
                B[z] = false;
            }
            for (int z = 0; z < H.length; z++) {
                H[z] = false;
            }

            /*
            resultPath = Parameters.simInfectionProbabilityFolder + "sim_inf_" + i + ".csv";
            writer = new CSVWriter(new FileWriter(resultPath), ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            infectionSimulation(writer);
            writer.close();
            */


            resultPath = Parameters.simCommunityFolder + "sim_com_" + i + ".csv";
            writer = new CSVWriter(new FileWriter(resultPath), ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            cliqueCommunityFinder();
            writer.close();
        }
    }

}