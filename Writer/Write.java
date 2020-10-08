package Writer;

import com.opencsv.CSVWriter;

import java.util.ArrayList;

public class Write {

	public static void WriteProbabilities(CSVWriter writer, int from, int to, double p) {
        String[] data1 = {Integer.toString(from), Integer.toString(to), Double.toString(p)};
        writer.writeNext(data1);
	}

    public static void WriteCommunities(CSVWriter writer, Boolean[] comm_nodes) {
        writer.writeNext(new String[] {comm_nodes.toString()});
    }

}
