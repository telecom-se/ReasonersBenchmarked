package fr.ujm.tse.lt2c.satin.datamodel;

import java.io.File;

/**
 * @author Christophe Gravier, <christophe.gravier@univ-st-etienne.fr>
 * 
 */
public abstract class AbstractInferer implements Inferer {
    private File datasetfile;
    private String fragment;

    /**
     * @param datasetfile
     * @param fragment
     */
    public AbstractInferer(final File datasetfile, final String fragment) {
        super();
        this.datasetfile = datasetfile;
        this.fragment = fragment;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public BenchResult call() throws Exception {
        return this.inferClosureFromModel();
    }

    /**
     * @return the datasetfile
     */
    public File getDatasetfile() {
        return this.datasetfile;
    }

    /**
     * @param datasetfile
     *            the datasetfile to set
     */
    public void setDatasetfile(final File datasetfile) {
        this.datasetfile = datasetfile;
    }

    /**
     * @return the fragment
     */
    public String getFragment() {
        return this.fragment;
    }

    /**
     * @param fragment
     *            the fragment to set
     */
    public void setFragment(final String fragment) {
        this.fragment = fragment;
    }

}
