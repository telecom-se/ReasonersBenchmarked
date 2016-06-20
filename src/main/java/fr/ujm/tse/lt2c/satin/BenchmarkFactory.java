package fr.ujm.tse.lt2c.satin;

import java.io.File;

import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.datamodel.AbstractInferer;
import fr.ujm.tse.lt2c.satin.datamodel.AbstractParser;
import fr.ujm.tse.lt2c.satin.inferray.InferrayInferer;
import fr.ujm.tse.lt2c.satin.inferray.InferrayParser;
import fr.ujm.tse.lt2c.satin.jena.JenaInferer;
import fr.ujm.tse.lt2c.satin.jena.JenaParser;
import fr.ujm.tse.lt2c.satin.owlim.OwlimInferer;
import fr.ujm.tse.lt2c.satin.owlim.OwlimParser;
import fr.ujm.tse.lt2c.satin.rdfox.RdfoxInferer;
import fr.ujm.tse.lt2c.satin.rdfox.RdfoxParser;
import fr.ujm.tse.lt2c.satin.sesame.SesameInferer;
import fr.ujm.tse.lt2c.satin.sesame.SesameParser;
import fr.ujm.tse.lt2c.satin.slider.SliderInferer;
import fr.ujm.tse.lt2c.satin.slider.SliderParser;

/**
 * @author Christophe Gravier, <christophe.gravier@univ-st-etienne.fr>
 * 
 */
public class BenchmarkFactory {

	private static final Logger LOGGER = Logger
			.getLogger(BenchmarkFactory.class);

	/**
	 * @param bench
	 * @return
	 */
	public static AbstractParser getParser(final Benchmark bench) {
		switch (bench.getReasonerConfiguration()) {
		case Benchmark.JENA: {
			return new JenaParser(new File(bench.getDatasetConfiguration()));
		}
		case Benchmark.INFERRAY: {
			return new InferrayParser(new File(bench.getDatasetConfiguration()));
		}
		case Benchmark.SESAME: {
			return new SesameParser(new File(bench.getDatasetConfiguration()));
		}
		case Benchmark.OWLIMSE: {
			return new OwlimParser(new File(bench.getDatasetConfiguration()));
		}
		case Benchmark.SLIDER: {
			return new SliderParser(new File(bench.getDatasetConfiguration()));
		}
		case Benchmark.RDFOX: {
			return new RdfoxParser(new File(bench.getDatasetConfiguration()));
		}
		default: {
			LOGGER.fatal("Unimplemented parser.");
			break;
		}
		}
		return null;
	}

	/**
	 * @param bench
	 * @return
	 */
	public static AbstractInferer getInferer(final Benchmark bench) {
		switch (bench.getReasonerConfiguration()) {
		case Benchmark.JENA: {
			return new JenaInferer(new File(bench.getDatasetConfiguration()),
					bench.getFragmentConfiguration());
		}
		case Benchmark.INFERRAY: {
			return new InferrayInferer(
					new File(bench.getDatasetConfiguration()),
					bench.getFragmentConfiguration());
		}
		case Benchmark.SESAME: {
			return new SesameInferer(new File(bench.getDatasetConfiguration()),
					bench.getFragmentConfiguration());
		}
		case Benchmark.OWLIMSE: {
			return new OwlimInferer(new File(bench.getDatasetConfiguration()),
					bench.getFragmentConfiguration());
		}
		case Benchmark.SLIDER: {
			return new SliderInferer(new File(bench.getDatasetConfiguration()),
					bench.getFragmentConfiguration());
		}
		case Benchmark.RDFOX: {
			return new RdfoxInferer(new File(bench.getDatasetConfiguration()),
					bench.getFragmentConfiguration());
		}
		default: {
			LOGGER.fatal("Unimplemented parser.");
			break;
		}
		}
		return null;
	}
}
