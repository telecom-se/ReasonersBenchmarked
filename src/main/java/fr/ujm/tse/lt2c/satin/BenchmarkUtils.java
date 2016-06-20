package fr.ujm.tse.lt2c.satin;

import java.io.File;
import java.io.IOException;

import javax.management.RuntimeErrorException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.datamodel.BenchResult;

/**
 * @author Christophe Gravier, <christophe.gravier@univ-st-etienne.fr>
 * 
 */
public class BenchmarkUtils {

	private static final Logger LOGGER = Logger.getLogger(BenchmarkUtils.class);

	public static long NANO_TO_MILISEC_RATIO = 1000000;

	/**
	 * hide default, unexpected, constructor
	 */
	private BenchmarkUtils() {

	}

	/**
	 * @param bench
	 * @param value
	 */
	public static void logResult(final Benchmark bench,
			final BenchResult benchResult) {

		final File outputFile = new File(bench.getOutputFileConfiguration());
		prepareOutputFile(outputFile);

		try {
			final StringBuffer benchResultLine = new StringBuffer();
			benchResultLine.append(bench.getReasonerConfiguration())
					.append(",");
			if (bench.isParseonlyConfiguration()) {
				benchResultLine.append("PARSE,");
			} else {
				benchResultLine.append("PARSE+INFER,");
			}
			benchResultLine.append(bench.getDatasetConfiguration()).append(",");
			benchResultLine.append(bench.getFragmentConfiguration())
					.append(",");
			benchResultLine.append(bench.getIterationsConfiguration()).append(
					",");
			benchResultLine.append(bench.getTimeoutConfiguration()).append(",");

			if (benchResult != null) {

				if (benchResult.isHadTimeout()) {
					benchResultLine.append(">")
							.append(bench.getTimeoutConfiguration())
							.append(",");
					benchResultLine.append("N/A").append("\n");
				} else {
					benchResultLine.append(
							benchResult.getExecTimeInMiliseconds()).append(",");
					benchResultLine.append("\n");
				}
				FileUtils.writeStringToFile(outputFile,
						benchResultLine.toString(), true);
			}
		} catch (final IOException e) {
			LOGGER.fatal("Cannot append header to CSV output file "
					+ outputFile, e);
			throw new RuntimeErrorException(new Error(
					"Cannot create output file."));
		}
	}

	/**
	 * @param outputFile
	 */
	private static void prepareOutputFile(final File outputFile) {
		if (!outputFile.exists()) {
			LOGGER.info("Creating non existing outputFile " + outputFile);
			try {
				outputFile.createNewFile();
			} catch (final IOException e) {
				LOGGER.fatal("Cannot create non existing output file "
						+ outputFile, e);
				throw new RuntimeErrorException(new Error(
						"Cannot create output file."));
			}
		}
		if (outputFile.length() == 0) {
			LOGGER.info("Columns name are missing in the output file, adding them...");
			try {
				FileUtils
						.writeStringToFile(
								outputFile,
								"Reasoner, Parse or parse+infer, Dataset, Fragment, Iterations, Timeout (seconds), Java exec time\n",
								true);
			} catch (final IOException e) {
				LOGGER.fatal("Cannot append header to CSV output file "
						+ outputFile, e);
				throw new RuntimeErrorException(new Error(
						"Cannot create output file."));
			}
		}
	}

}
