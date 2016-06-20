package fr.ujm.tse.lt2c.satin.sesame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.management.RuntimeErrorException;

import org.apache.log4j.Logger;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;

import fr.ujm.tse.lt2c.satin.BenchmarkUtils;
import fr.ujm.tse.lt2c.satin.datamodel.AbstractParser;
import fr.ujm.tse.lt2c.satin.datamodel.BenchResult;

public class SesameParser extends AbstractParser {

	/**
	 * @param datasetfile
	 */
	public SesameParser(final File datasetfile) {
		super(datasetfile);
	}

	private static Logger LOGGER = Logger.getLogger(SesameParser.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.ujm.tse.lt2c.satin.Parser#getModelFromFile(java.io.File)
	 */
	@Override
	public BenchResult getModelFromFile() {
		final long startTime = System.nanoTime();
		try {
			SailRepository repo = new SailRepository(new MemoryStore());
			repo.initialize();
			SailRepositoryConnection connection = repo.getConnection();
			connection.add(datasetfile, "",
					Rio.getParserFormatForFileName(datasetfile.toString()));
			long stopTime = System.nanoTime();

			return new BenchResult((stopTime - startTime)
					/ BenchmarkUtils.NANO_TO_MILISEC_RATIO, false);

		} catch (final FileNotFoundException e) {
			LOGGER.fatal("Cannot find");
			throw new RuntimeErrorException(new Error("Invalid dataset file."));
		} catch (RepositoryException e) {
			LOGGER.fatal("Cannot create Sesame repository");
			throw new RuntimeErrorException(new Error(
					"Invalid sesame repository creation."));
		} catch (RDFParseException e) {
			LOGGER.fatal("Cannot create Sesame repository");
			throw new RuntimeErrorException(new Error(
					"Invalid dataset format, parse error for sesame parser (dataset : "
							+ datasetfile + ")"));
		} catch (IOException e) {
			LOGGER.fatal("Dataset file not found");
			throw new RuntimeErrorException(new Error(
					"Cannot find dataset file for sesame (dataset : "
							+ datasetfile + ")"));
		}
	}
}
