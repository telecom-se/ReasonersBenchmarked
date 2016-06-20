package fr.ujm.tse.lt2c.satin.rdfox;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ReplaceAll {

	private static String fileIn = "/home/satin/Downloads/bsbmtools-0.2/dataset_50M.nt";
	private static String fileOut = "/home/satin/Downloads/bsbmtools-0.2/dataset_50M_cleaned.nt";

	private static String replace = "\\^\\^<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/USD>";
	private static String replaceBy = "";

	public static void main(String[] args) throws FileNotFoundException, IOException {

		FileWriter fw = new FileWriter(fileOut);
		try (BufferedReader br = new BufferedReader(new FileReader(fileIn))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.replaceAll(replace, replaceBy);
				fw.write(line + "\n");
			}
			br.close();
		}
		fw.close();
	}
}
