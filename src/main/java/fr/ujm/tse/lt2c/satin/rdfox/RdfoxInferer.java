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
import uk.ac.ox.cs.JRDFox.store.DataStore;
import fr.ujm.tse.lt2c.satin.BenchmarkUtils;
import fr.ujm.tse.lt2c.satin.datamodel.AbstractInferer;
import fr.ujm.tse.lt2c.satin.datamodel.BenchResult;

public class RdfoxInferer extends AbstractInferer {

	/**
	 * @param datasetfile
	 */
	public RdfoxInferer(final File datasetfile, String fragment) {
		super(datasetfile, fragment);
	}

	private static Logger LOGGER = Logger.getLogger(RdfoxParser.class);

	@Override
	public BenchResult inferClosureFromModel() {
		File fragFile = RdfoxUtils.getFragmentFile(this.getFragment());

		long startTime = 0;
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		// OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI
		// .create(JRDFoxDemo.class.getResource("data/univ-bench.owl")));

		OWLOntology ontology = null;
		DataStore store = null;
		long before = 0;
		long after = 0;
		try {
			// Load schema data
			ontology = manager
					.loadOntologyFromOntologyDocument(IRI.create(RdfoxUtils.getOntologyFile(this.getDatasetfile())));
			store = new DataStore(DataStore.StoreType.ParallelSimpleNN, true);
			// quick and dirty
			int nbThreads = 8;
			System.out.println("Setting the number of threads to " + nbThreads + "...");
			store.setNumberOfThreads(nbThreads);
			System.out.println("Importing RDF data...");
			if (this.getDatasetfile().getName().toLowerCase().contains("yago")) {
				LOGGER.info("Special YAGO Case");
				store.importFiles(new File[] { new File("/home/satin/inferray/rdfox/yago.ttl")});
			} else {
				store.importFiles(new File[] { this.getDatasetfile() });
			}
			before = store.getTriplesCount();
			System.out.println("Number of tuples after import: " + before);

			// Prefixes prefixes = Prefixes.DEFAULT_IMMUTABLE_INSTANCE;
			// System.out
			// .println("Retrieving all properties before materialisation.");
			// TupleIterator tupleIterator = store.compileQuery(
			// "SELECT DISTINCT ?y WHERE{ ?x ?y ?z }", prefixes,
			// new Parameters());
			try {
				System.out.println("Adding the ontology to the store...");
				store.importRulesFromOntology(ontology);

				System.out.println("Importing rules from a file...");
				store.importFiles(new File[] { fragFile });

				System.out.println("================================\nTriples before materialization : "
						+ store.getTriplesCount() + " \n================================");
				startTime = System.nanoTime();
				System.out.println("Apply the rules in the store against the current facts...");
				store.applyReasoning();
				// //
				after = store.getTriplesCount();
				System.out.println("Number of tuples after materialization: " + after);
			} catch (JRDFStoreException e) {
				throw new RuntimeErrorException(new Error(e.getMessage()));
			} finally {
				// tupleIterator.dispose();
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
		return new BenchResult((System.nanoTime() - startTime) / BenchmarkUtils.NANO_TO_MILISEC_RATIO,
				false);
	}
}
