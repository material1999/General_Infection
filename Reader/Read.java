package Reader;

import java.io.FileReader;
import java.io.IOException;
import com.opencsv.CSVReader;

import Graph.*;

public class Read {

	public static Network ReadCsv(String filepath) {
		Network network=new Network();
		CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(filepath),';');
            String[] line;
            reader.readNext();
            while ((line = reader.readNext()) != null) {
            	if(!(line[0].equals(line[1]))) {
                network.addEdge(new Edge(new Node(line[0]),new Node(line[1]),Double.parseDouble(line[2])));
            	}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return network;
	}

}
