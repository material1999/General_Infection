package Writer;

import com.opencsv.CSVWriter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

public class Write {

	public static void WriteProbabilities(CSVWriter writer, HashMap<String, Double> map) {
		String temp;
		for (Map.Entry<String, Double> entry : map.entrySet()) {
			temp = entry.getKey() + ";" + entry.getValue();
			writer.writeNext(new String[]{temp}, false);
		}
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
