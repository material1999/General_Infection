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

    public static void main(String[] args) throws IOException {

        String resultPath;
        double rand;



        // Infection Approximation
        /*
        //for (int i = 1; i <= Parameters.fileCount; i++) {
        for (int i = 0; i <= 0; i++) {

            String networkFilePath = Parameters.networksFolder + i + "/edgeweighted.csv";
            Network network = Read.ReadCsv(networkFilePath);
            int nodeNumber = network.getNodes().size();
            resultPath = "results/inf_approx/inf_approx_" + i + ".csv";
            CSVWriter writer = new CSVWriter(new FileWriter(resultPath), ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            for (int j = 1; j <= nodeNumber; j++) {
                for (Edge edge1: network.getNode(Integer.toString(j)).getOutlist()) {
                    Write.WriteCsv(writer, Integer.parseInt(edge1.getOut().getId()), Integer.parseInt(edge1.getIn().getId()), edge1.getWeight());
                    for (Edge edge2: edge1.getIn().getOutlist()) {
                        if (edge1.getOut() != edge2.getIn()) {
                            Write.WriteCsv(writer, Integer.parseInt(edge1.getOut().getId()), Integer.parseInt(edge2.getIn().getId()), edge1.getWeight() * edge2.getWeight());
                        }
                    }
                }
            }

            writer.close();
        }
        */







        // Infection Simulation

        //for (int i = 1; i <= Parameters.fileCount; i++) {
        for (int i = 0; i <= 0; i++) {

            String networkFilePath = Parameters.networksFolder + i + "/edgeweighted.csv";
            Network network = Read.ReadCsv(networkFilePath);
            int nodeNumber = network.getNodes().size();
            resultPath = "results/inf_sim/inf_sim_" + i + ".csv";
            CSVWriter writer = new CSVWriter(new FileWriter(resultPath), ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            for (int j = 1; j <= nodeNumber; j++) {
                System.out.println("Node " + j + ":");
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
                        Write.WriteCsv(writer, j, Integer.parseInt(node.getId()), node.getFv());
                        System.out.println("Node " + node.getId() + ": " + node.getFv());
                    }
                }

                System.out.println();
            }

            writer.close();
        }
        

    }

}