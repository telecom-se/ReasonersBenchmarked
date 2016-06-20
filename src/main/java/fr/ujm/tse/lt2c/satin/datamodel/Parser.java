package fr.ujm.tse.lt2c.satin.datamodel;

import java.util.concurrent.Callable;

/**
 * @author Christophe Gravier, <christophe.gravier@univ-st-etienne.fr>
 * 
 */
public interface Parser extends Callable<BenchResult> {
	/**
	 * read model from file and measure parse time.
	 * 
	 * @param datasetfile
	 *            where the dataset is stored.
	 * @return the time to parse the model from the given
	 *         <code>datasetfile</code>, in miliseconds.
	 */
	public BenchResult getModelFromFile();
}