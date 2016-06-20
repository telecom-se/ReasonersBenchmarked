package fr.ujm.tse.lt2c.satin.datamodel;

import java.io.File;

/**
 * @author Christophe Gravier, <christophe.gravier@univ-st-etienne.fr>
 * 
 */
public abstract class AbstractParser implements Parser {
	protected final File datasetfile;

	/**
	 * @param datasetfile
	 */
	public AbstractParser(final File datasetfile) {
		super();
		this.datasetfile = datasetfile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public BenchResult call() throws Exception {
		return this.getModelFromFile();
	}
}
