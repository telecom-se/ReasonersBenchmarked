package fr.ujm.tse.lt2c.satin.rdfox;

import java.io.File;
import java.net.URISyntaxException;

import javax.management.RuntimeErrorException;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.ox.cs.JRDFox.JRDFStoreException;
import uk.ac.ox.cs.JRDFox.Prefixes;
import uk.ac.ox.cs.JRDFox.store.DataStore;
import uk.ac.ox.cs.JRDFox.store.Parameters;
import uk.ac.ox.cs.JRDFox.store.TupleIterator;
import fr.ujm.tse.lt2c.satin.BenchmarkUtils;
import fr.ujm.tse.lt2c.satin.datamodel.AbstractParser;
import fr.ujm.tse.lt2c.satin.datamodel.BenchResult;

public class RdfoxParser extends AbstractParser {

	/**
	 * @param datasetfile
	 */
	public RdfoxParser(final File datasetfile) {
		super(datasetfile);
	}

	private static Logger LOGGER = Logger.getLogger(RdfoxParser.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.ujm.tse.lt2c.satin.Parser#getModelFromFile(java.io.File)
	 */
	@Override
	public BenchResult getModelFromFile() {
		final long startTime = System.nanoTime();

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		// OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI
		// .create(JRDFoxDemo.class.getResource("data/univ-bench.owl")));

		OWLOntology ontology = null;
		DataStore store = null;

		try {
			ontology = manager.loadOntologyFromOntologyDocument(IRI
					.create(RdfoxUtils.getOntologyFile(this.datasetfile)));
			store = new DataStore(DataStore.StoreType.ParallelSimpleNN, true);
			// quick and dirty
			int nbThreads = 8;
			System.out.println("Setting the number of threads to " + nbThreads
					+ "...");
			store.setNumberOfThreads(nbThreads);
			System.out.println("Importing RDF data...");
			store.importFiles(new File[] { this.datasetfile });

			System.out.println("Number of tuples after import: "
					+ store.getTriplesCount());

			Prefixes prefixes = Prefixes.DEFAULT_IMMUTABLE_INSTANCE;
			System.out
					.println("Retrieving all properties before materialisation.");
			TupleIterator tupleIterator = store.compileQuery(
					"SELECT DISTINCT ?y WHERE{ ?x ?y ?z }", prefixes,
					new Parameters());
			try {
				System.out.println("Adding the ontology to the store...");
				store.importRulesFromOntology(ontology);

				// System.out.println("Importing rules from a file...");
				// store.importFiles(new File[] { new File(RdfoxParser.class
				// .getResource(this.fragmentFile).toURI()) });
				//
				// System.out
				// .println("================================\nTriples before materialization : "
				// + store.getTriplesCount()
				// + " \n================================");

				// System.out
				// .println("Apply the rules in the store against the current facts...");
				// store.applyReasoning();
				// // //
				// System.out.println("Number of tuples after materialization: "
				// + store.getTriplesCount());
				// //
				// System.out
				// .println("================================\nTriples after materialization : "
				// + store.getTriplesCount()
				// + " \n================================");
				//
				// System.out.println("Total Reasoning Time: "
				// + (System.nanoTime() - startTime) / 1000000);
				//
				// System.out
				// .println("Retrieving all properties before materialisation.");
				// countAll(prefixes, store);
			} catch (JRDFStoreException e) {
				throw new RuntimeErrorException(new Error(e.getMessage()));
			} finally {
				tupleIterator.dispose();
			}
		} catch (JRDFStoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (OWLOntologyCreationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			if (!(store == null)) {
				store.dispose();
			}
		}
		return new BenchResult((System.nanoTime() - startTime)
				/ BenchmarkUtils.NANO_TO_MILISEC_RATIO, false);
	}

	public static void countAll(Prefixes prefixes, DataStore store)
			throws JRDFStoreException {
		TupleIterator tupleIterator = store
				.compileQuery("SELECT ?x ?y ?z WHERE{ ?x ?y ?z }", prefixes,
						new Parameters());
		System.out.println();
		System.out
				.println("=======================================================================================");

		int nb = 0;
		for (long multiplicity = tupleIterator.open(); multiplicity != 0; multiplicity = tupleIterator
				.getNext()) {
			nb++;
		}
		System.out.println("Triplestore has " + nb + " triples");
	}
}
