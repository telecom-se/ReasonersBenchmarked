package fr.ujm.tse.lt2c.satin.slider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.management.RuntimeErrorException;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import fr.ujm.tse.lt2c.satin.Benchmark;
import fr.ujm.tse.lt2c.satin.BenchmarkUtils;
import fr.ujm.tse.lt2c.satin.datamodel.AbstractInferer;
import fr.ujm.tse.lt2c.satin.datamodel.BenchResult;
import fr.ujm.tse.lt2c.satin.slider.dictionary.DictionaryPrimitrivesRWLock;
import fr.ujm.tse.lt2c.satin.slider.interfaces.Parser;
import fr.ujm.tse.lt2c.satin.slider.reasoner.ReasonerStreamed;
import fr.ujm.tse.lt2c.satin.slider.rules.ReasonerProfile;
import fr.ujm.tse.lt2c.satin.slider.triplestore.VerticalPartioningTripleStoreRWLock;
import fr.ujm.tse.lt2c.satin.slider.utils.ParserImplNaive;

public class SliderInferer extends AbstractInferer {

	/**
	 * @param datasetfile
	 * @param fragment
	 */
	public SliderInferer(final File datasetfile, final String fragment) {
		super(datasetfile, fragment);
	}

	private static Logger LOGGER = Logger.getLogger(SliderInferer.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.ujm.tse.lt2c.satin.Inferer#inferClosureFromModel()
	 */
	@Override
	public BenchResult inferClosureFromModel() {

		// 1- load model from file.
		final Model model = ModelFactory.createDefaultModel();
		try {
			final InputStream is = new FileInputStream(this.getDatasetfile());
			model.read(is, null, "N-TRIPLE");
		} catch (final FileNotFoundException e) {
			LOGGER.fatal("Cannot find");
			throw new RuntimeErrorException(new Error("Invalid dataset file."));
		}

		switch (this.getFragment()) {
		case Benchmark.RDFSDEFAULT: {
			return inferStreamRDFS();
		}
		case Benchmark.RDFSFULL: {
			return inferStreamRdfsFull();
		}
		case Benchmark.RHODF: {
			return inferStreamRhoDf();
		}
		case Benchmark.RDFSPP: {
			LOGGER.warn("Unsupported fragment for Slider : "
					+ this.getFragment());
			throw new RuntimeErrorException(new Error(
					"Unsupported fragment for Slider"));
		}
		default: {
			LOGGER.fatal("Unsupported fragment for Slider : "
					+ this.getFragment());
			throw new RuntimeErrorException(new Error(
					"Unsupported fragment for Slider"));
		}
		}
	}

	private BenchResult inferStreamRdfsFull() {
		return inferFromProfile(ReasonerProfile.BRDFS);
	}

	private BenchResult inferStreamRDFS() {
		return inferFromProfile(ReasonerProfile.RDFS);
	}

	private BenchResult inferStreamRhoDf() {
		return inferFromProfile(ReasonerProfile.RHODF);
	}

	private BenchResult inferFromProfile(ReasonerProfile prof) {
		long startTime = System.nanoTime();

		VerticalPartioningTripleStoreRWLock triplestore = new VerticalPartioningTripleStoreRWLock();
		DictionaryPrimitrivesRWLock dictionary = new DictionaryPrimitrivesRWLock();
		final ReasonerStreamed reasoner = new ReasonerStreamed(triplestore,
				dictionary, prof);

		final Parser parser = new ParserImplNaive(dictionary, triplestore);
		reasoner.start();
		final int input_size = parser.parseStream(this.getDatasetfile()
				.getAbsolutePath(), reasoner);
		reasoner.closeAndWait();

		long stopTime = System.nanoTime();
		return new BenchResult(
				(stopTime - startTime) / BenchmarkUtils.NANO_TO_MILISEC_RATIO,
				false);
	}
}
