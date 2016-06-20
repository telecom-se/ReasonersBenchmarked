package fr.ujm.tse.lt2c.satin.datamodel;

import java.util.concurrent.Callable;

/**
 * @author Christophe Gravier, <christophe.gravier@univ-st-etienne.fr>
 * 
 */
public interface Inferer extends Callable<BenchResult> {

    /**
     * infer the model from file and measure parse time.
     * 
     * @param datasetfile
     *            where the dataset is stored.
     * @return the time to parse the model from the given <code>datasetfile</code>
     */
    public BenchResult inferClosureFromModel();
}
