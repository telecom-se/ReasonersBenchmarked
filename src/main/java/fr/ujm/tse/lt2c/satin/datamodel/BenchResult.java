package fr.ujm.tse.lt2c.satin.datamodel;

/**
 * @author Christophe Gravier, <christophe.gravier@univ-st-etienne.fr>
 * 
 */
public class BenchResult {
    private long execTimeInMiliseconds = 0L;
    private boolean hadTimeout = false;

    /**
     *
     * @param execTimeInMiliseconds
     * @param hadTimeout
     */
    public BenchResult(final long execTimeInMiliseconds, final boolean hadTimeout) {
        super();
        this.execTimeInMiliseconds = execTimeInMiliseconds;
        this.hadTimeout = hadTimeout;
    }

    private BenchResult() {
    }

    /**
     * @return the hadTimeout
     */
    public boolean isHadTimeout() {
        return this.hadTimeout;
    }

    /**
     * @param hadTimeout
     *            the hadTimeout to set
     */
    public void setHadTimeout(final boolean hadTimeout) {
        this.hadTimeout = hadTimeout;
    }

   
    /**
     * @return the execTimeInMiliseconds
     */
    public long getExecTimeInMiliseconds() {
        return this.execTimeInMiliseconds;
    }

    /**
     * @param execTimeInMiliseconds
     *            the execTimeInMiliseconds to set
     */
    public void setExecTimeInMiliseconds(final long e) {
        this.execTimeInMiliseconds = e;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (this.execTimeInMiliseconds ^ (this.execTimeInMiliseconds >>> 32));
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final BenchResult other = (BenchResult) obj;
        if (this.execTimeInMiliseconds != other.execTimeInMiliseconds) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BenchResult [execTimeInMiliseconds=" + this.execTimeInMiliseconds + "]";
    }
}
