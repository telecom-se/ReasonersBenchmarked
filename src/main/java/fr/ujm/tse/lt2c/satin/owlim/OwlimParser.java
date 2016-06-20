package fr.ujm.tse.lt2c.satin.owlim;

import java.io.File;

import fr.ujm.tse.lt2c.satin.BenchmarkUtils;
import fr.ujm.tse.lt2c.satin.datamodel.AbstractParser;
import fr.ujm.tse.lt2c.satin.datamodel.BenchResult;

public class OwlimParser extends AbstractParser {

	public OwlimParser(final File datasetfile) {
		super(datasetfile);
	}

	@Override
	public BenchResult getModelFromFile() {
		OwlimRunner owlim = new OwlimRunner();
		return new BenchResult(owlim.getProcessTime()
				/ BenchmarkUtils.NANO_TO_MILISEC_RATIO, false);
	}
}
