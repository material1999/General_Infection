package Writer;

import com.opencsv.CSVWriter;

import java.util.HashSet;
import java.util.TreeSet;

public class Write {

	public static void WriteProbabilities(CSVWriter writer, int from, int to, double p) {
        String[] data1 = {Integer.toString(from), Integer.toString(to), Double.toString(p)};
        writer.writeNext(data1);
	}

    public static void WriteCommunities(CSVWriter writer, HashSet<TreeSet<Integer>> comm_nodes) {
	    for (TreeSet<Integer> tsi : comm_nodes) {
	        String temp = tsi.toString();
	        temp = temp.strip();
	        temp = temp.substring(1, temp.length() - 1);
	        temp = temp.replace(",", "");
            writer.writeNext(new String[] {temp});
        }
    }

}
