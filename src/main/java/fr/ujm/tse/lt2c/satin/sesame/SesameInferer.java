package fr.ujm.tse.lt2c.satin.sesame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.management.RuntimeErrorException;

import org.apache.log4j.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;

import fr.ujm.tse.lt2c.satin.Benchmark;
import fr.ujm.tse.lt2c.satin.BenchmarkUtils;
import fr.ujm.tse.lt2c.satin.datamodel.AbstractInferer;
import fr.ujm.tse.lt2c.satin.datamodel.BenchResult;

public class SesameInferer extends AbstractInferer {

	/**
	 * @param datasetfile
	 */
	public SesameInferer(final File datasetfile, String fragment) {
		super(datasetfile, fragment);
	}

	private static Logger LOGGER = Logger.getLogger(SesameParser.class);

	@Override
	public BenchResult inferClosureFromModel() {

		switch (this.getFragment()) {
		case Benchmark.RDFSDEFAULT: {
			LOGGER.warn("Unsupported fragment for Sesame : "
					+ this.getFragment());
			throw new RuntimeErrorException(new Error(
					"Unsupported fragment for Sesame"));
		}
		case Benchmark.RDFSFULL: {
			return inferSesameRDFSFull();
		}
		case Benchmark.RHODF: {
			LOGGER.warn("Unsupported fragment for Sesame : "
					+ this.getFragment());
			throw new RuntimeErrorException(new Error(
					"Unsupported fragment for Sesame"));
		}
		case Benchmark.RDFSPP: {
			LOGGER.warn("Unsupported fragment for Sesame : "
					+ this.getFragment());
			throw new RuntimeErrorException(new Error(
					"Unsupported fragment for Sesame"));
		}
		default: {
			LOGGER.fatal("Unsupported fragment for Jena : "
					+ this.getFragment());
			throw new RuntimeErrorException(new Error(
					"Unsupported fragment for Sesame"));
		}
		}
	}

	private BenchResult inferSesameRDFSFull() {
		final long startTime = System.nanoTime();
		try {
			SailRepository repo = new SailRepository(
					new ForwardChainingRDFSInferencer(new MemoryStore()));

			repo.initialize();
			SailRepositoryConnection connection = repo.getConnection();
			connection.add(this.getDatasetfile(), "", Rio
					.getParserFormatForFileName(this.getDatasetfile()
							.toString()));
			connection.commit();

			RepositoryResult<Statement> results = connection.getStatements(
					null, null, null, false, (Resource) null);
			long countExplicit = 0;
			while (results.hasNext()) {
				countExplicit++;
				results.next();
			}

			results = connection.getStatements(null, null, null, true,
					(Resource) null);
			long countAll = 0;
			while (results.hasNext()) {
				countAll++;
				results.next();
			}

			final long endTime = System.nanoTime();

			return new BenchResult(
					(endTime - startTime)
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
					"Invalid dataset format, parse error for sesame parser"));
		} catch (IOException e) {
			LOGGER.fatal("Dataset file not found");
			throw new RuntimeErrorException(new Error(
					"Cannot find dataset file for sesame. "));
		}
	}
}
