package fr.ujm.tse.lt2c.satin.owlim;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser.DatatypeHandling;

import fr.ujm.tse.lt2c.satin.BenchmarkResourcesUtils;
import fr.ujm.tse.lt2c.satin.owlim.OwlimApplication.Parameters;

public class OwlimRunner {

	private OwlimApplication gettingStartedApplication = null;

	public long getProcessTime() {
		return processTime;
	}

	public void setProcessTime(long processTime) {
		this.processTime = processTime;
	}

	private long numberOfImliciteStatements = 0L;
	private long processTime = 0L;

	public long getNumberOfImliciteStatements() {
		return numberOfImliciteStatements;
	}

	public void setNumberOfImliciteStatements(long numberOfImliciteStatements) {
		this.numberOfImliciteStatements = numberOfImliciteStatements;
	}

	public OwlimRunner() {
		// Delete a repo is exising
		File f = new File("repositories");
		if (f.exists() && f.isDirectory()) {
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.setProperty("entityExpansionLimit", "1000000");

		// Parse all the parameters
		Parameters params = new Parameters(ArrayUtils.EMPTY_STRING_ARRAY);

		// Set default values for missing parameters

		params.setDefaultValue(OwlimApplication.PARAM_CONFIG,
				new BenchmarkResourcesUtils().extractResourceToTemp(
						"owlim-se.ttl", "owlim-se", ".ttl"));

		params.setDefaultValue(OwlimApplication.PARAM_SHOWRESULTS, "true");
		params.setDefaultValue(OwlimApplication.PARAM_SHOWSTATS, "false");
		params.setDefaultValue(OwlimApplication.PARAM_UPDATES, "false");
		// params.setDefaultValue(OwlimApplication.PARAM_QUERYFILE,
		// "./queries/sample.sparql");
		params.setDefaultValue(OwlimApplication.PARAM_EXPORT_FORMAT,
				RDFFormat.NTRIPLES.getName());

		// params.setDefaultValue(PARAM_PRELOAD, "./preload");
		params.setDefaultValue(OwlimApplication.PARAM_VERIFY, "false");
		params.setDefaultValue(OwlimApplication.PARAM_STOP_ON_ERROR, "true");
		params.setDefaultValue(OwlimApplication.PARAM_PRESERVE_BNODES, "false");
		params.setDefaultValue(OwlimApplication.PARAM_DATATYPE_HANDLING,
				DatatypeHandling.VERIFY.name());
		params.setDefaultValue(OwlimApplication.PARAM_CHUNK_SIZE, "500000");

		OwlimUtils.log("Using parameters:");
		OwlimUtils.log(params.toString());

		try {
			long initializationStart = System.nanoTime();
			gettingStartedApplication = new OwlimApplication(
					params.getParameters());
			this.numberOfImliciteStatements = gettingStartedApplication
					.numberOfImplicitStatements();

			this.processTime = System.nanoTime() - initializationStart;

		} catch (Throwable ex) {
			OwlimUtils
					.log("An exception occured at some point during execution:");
			ex.printStackTrace();
		} finally {

		}

		if (gettingStartedApplication != null) {
			try {
				gettingStartedApplication.shutdown();
			} catch (Throwable e) {
				// do nothing, resource is already closed.
			}
		}
	}

	public OwlimApplication getGettingStartedApplication() {
		return gettingStartedApplication;
	}

	public void setGettingStartedApplication(
			OwlimApplication gettingStartedApplication) {
		this.gettingStartedApplication = gettingStartedApplication;
	}
}
