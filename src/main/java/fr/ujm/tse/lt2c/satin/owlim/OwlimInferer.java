package fr.ujm.tse.lt2c.satin.owlim;

import java.io.File;

import fr.ujm.tse.lt2c.satin.BenchmarkUtils;
import fr.ujm.tse.lt2c.satin.datamodel.AbstractInferer;
import fr.ujm.tse.lt2c.satin.datamodel.BenchResult;

public class OwlimInferer extends AbstractInferer {
	public OwlimInferer(final File datasetfile, final String fragment) {
		super(datasetfile, fragment);
	}

	@Override
	public BenchResult inferClosureFromModel() {
		OwlimRunner owlim = new OwlimRunner();
		try {
			long elaspedNano = owlim.getProcessTime();
			BenchResult b = new BenchResult(elaspedNano
							/ BenchmarkUtils.NANO_TO_MILISEC_RATIO, false);
			return b;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
