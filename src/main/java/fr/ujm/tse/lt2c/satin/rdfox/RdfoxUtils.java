package fr.ujm.tse.lt2c.satin.rdfox;

import java.io.File;
import java.net.URISyntaxException;

import javax.management.RuntimeErrorException;

import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.Benchmark;

public class RdfoxUtils {

	private static Logger LOGGER = Logger.getLogger(RdfoxUtils.class);

	public static File getFragmentFile(String fragment) {
		switch (fragment) {
		case Benchmark.RDFSDEFAULT: {
			return new File("/home/satin/inferray/rdfox/frags/rdfsdefault-rules.txt");
		}
		case Benchmark.RDFSFULL: {
			return new File("/home/satin/inferray/rdfox/frags/rdfsfull-rules.txt");
		}
		case Benchmark.RHODF: {
			return new File("/home/satin/inferray/rdfox/frags/rhodf-rules.txt");
		}
		case Benchmark.RDFSPP: {
			return new File(
					"/home/satin/inferray/rdfox/frags/rdfs-plus-rules.txt");
		}
		default: {
			LOGGER.fatal("Unsupported fragment for RDFOx : " + fragment);
			throw new RuntimeErrorException(new Error(
					"Unsupported fragment for RDFOx"));
		}
		}
	}

	public static File getOntologyFile(File datasetfile)
			throws URISyntaxException {
		if (datasetfile.getName().contains("lubm")) {
			// return new File(RdfoxUtils.class
			// .getResource("rdfox/univ-bench.owl").toURI());
			return new File("/home/satin/inferray/rdfox/univ-bench.owl");
		} else if (datasetfile.getName().contains("dataset_")) {
			return new File("/home/satin/inferray/rdfox/bsbm.owl");
		}else if(datasetfile.getName().contains("wiki")){
			System.out.println("Using wikischema");
			return new File("/home/satin/inferray/rdfox/empty.owl");
		} else if(datasetfile.getName().contains("yago")){
			return new File("/home/satin/inferray/rdfox/empty.owl");
		} else if(datasetfile.getName().contains("wordnet")){
			return new File("/home/satin/inferray/rdfox/wordnet.owl");
		} else if(datasetfile.getName().toLowerCase().contains("subclass")){
			return new File("/home/satin/inferray/rdfox/empty.owl");
		} else {
			throw new RuntimeErrorException(new Error(
					"Unsupported dataset for RDFOx as no matching ontology file for "
							+ datasetfile.getName()));
		}
	}
}
