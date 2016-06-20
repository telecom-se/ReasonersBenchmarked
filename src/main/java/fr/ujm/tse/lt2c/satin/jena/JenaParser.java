package fr.ujm.tse.lt2c.satin.jena;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.management.RuntimeErrorException;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import fr.ujm.tse.lt2c.satin.BenchmarkUtils;
import fr.ujm.tse.lt2c.satin.datamodel.AbstractParser;
import fr.ujm.tse.lt2c.satin.datamodel.BenchResult;

public class JenaParser extends AbstractParser {

	/**
	 * @param datasetfile
	 */
	public JenaParser(final File datasetfile) {
		super(datasetfile);
	}

	private static Logger LOGGER = Logger.getLogger(JenaParser.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.ujm.tse.lt2c.satin.Parser#getModelFromFile(java.io.File)
	 */
	@Override
	public BenchResult getModelFromFile() {
		final long startTime = System.nanoTime();
		final Model model = ModelFactory.createDefaultModel();
		try {
			final InputStream is = new FileInputStream(this.datasetfile);
			model.read(is, null, "N-TRIPLE");
		} catch (final FileNotFoundException e) {
			LOGGER.fatal("Cannot find");
			throw new RuntimeErrorException(new Error("Invalid dataset file."));
		}
		return new BenchResult((System.nanoTime() - startTime)
				/ BenchmarkUtils.NANO_TO_MILISEC_RATIO, false);
	}
}
