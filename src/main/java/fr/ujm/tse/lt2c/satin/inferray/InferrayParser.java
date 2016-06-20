package fr.ujm.tse.lt2c.satin.inferray;

import java.io.File;

import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.BenchmarkUtils;
import fr.ujm.tse.lt2c.satin.datamodel.AbstractParser;
import fr.ujm.tse.lt2c.satin.datamodel.BenchResult;
import fr.ujm.tse.lt2c.satin.inferray.reasoner.Inferray;
import fr.ujm.tse.lt2c.satin.inferray.rules.profile.SupportedProfile;

public class InferrayParser extends AbstractParser {

	private String axiomaticTripleExtractDirectory;

	/**
	 * @param datasetfile
	 */
	public InferrayParser(final File datasetfile) {
		super(datasetfile);
		this.axiomaticTripleExtractDirectory = new File(
				System.getProperty("java.io.tmpdir")
						+ System.getProperty("file.separator"))
				.getAbsolutePath();
	}

	private static Logger LOGGER = Logger.getLogger(InferrayParser.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.ujm.tse.lt2c.satin.Parser#getModelFromFile(java.io.File)
	 */
	@Override
	public BenchResult getModelFromFile() {
		final long startTime = System.nanoTime();

		Inferray inferray = InferrayUtils.infererParse(
				SupportedProfile.RDFSPLUS, this.datasetfile.getAbsolutePath(),
				axiomaticTripleExtractDirectory);

		long stopTime = System.nanoTime();

		return new BenchResult((stopTime - startTime)
				/ BenchmarkUtils.NANO_TO_MILISEC_RATIO, false);
	}
}
