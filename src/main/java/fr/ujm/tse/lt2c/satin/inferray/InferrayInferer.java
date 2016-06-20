package fr.ujm.tse.lt2c.satin.inferray;

import java.io.File;

import javax.management.RuntimeErrorException;

import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.Benchmark;
import fr.ujm.tse.lt2c.satin.datamodel.AbstractInferer;
import fr.ujm.tse.lt2c.satin.datamodel.BenchResult;
import fr.ujm.tse.lt2c.satin.inferray.reasoner.Inferray;
import fr.ujm.tse.lt2c.satin.inferray.rules.profile.SupportedProfile;

public class InferrayInferer extends AbstractInferer {

	private String axiomaticTripleExtractDirectory = null;

	/**
	 * @param datasetfile
	 */
	public InferrayInferer(final File datasetfile, String fragment) {
		super(datasetfile, fragment);
		this.axiomaticTripleExtractDirectory = new File(
				System.getProperty("java.io.tmpdir")
						+ System.getProperty("file.separator"))
				.getAbsolutePath();
	}

	private static Logger LOGGER = Logger.getLogger(InferrayInferer.class);

	@Override
	public BenchResult inferClosureFromModel() {

		SupportedProfile ruleProfile = null;

		switch (this.getFragment()) {
		case Benchmark.RDFSDEFAULT: {
			ruleProfile = SupportedProfile.RDFSDEFAULT;
			break;
		}
		case Benchmark.RDFSFULL: {
			ruleProfile = SupportedProfile.RDFS;
			break;
		}
		case Benchmark.RHODF: {
			ruleProfile = SupportedProfile.RHODF;
			break;
		}
		case Benchmark.RDFSPP: {
			ruleProfile = SupportedProfile.RDFSPLUS;
			break;
		}
		default: {
			LOGGER.fatal("Unsupported fragment for Inferray : "
					+ this.getFragment());
			throw new RuntimeErrorException(new Error(
					"Unsupported fragment for Inferray"));
		}
		}

		long startTime = System.nanoTime();

		Inferray inferray = InferrayUtils.infererParse(ruleProfile, this
				.getDatasetfile().getAbsolutePath(),
				axiomaticTripleExtractDirectory);
		inferray.process();
		long parseTime = inferray.getInferenceTime(); // already in miliseconds.
		long stopTime = System.nanoTime();

		return new BenchResult(parseTime, false);

	}
}
