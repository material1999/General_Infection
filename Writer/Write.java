package Writer;

import com.opencsv.CSVWriter;

public class Write {

	public static void WriteCsv(CSVWriter writer, int from, int to, double p) {
        String[] data1 = {Integer.toString(from), Integer.toString(to), Double.toString(p)};
        writer.writeNext(data1);
	}

}
