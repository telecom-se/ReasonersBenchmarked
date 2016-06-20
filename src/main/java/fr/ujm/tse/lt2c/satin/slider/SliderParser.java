package fr.ujm.tse.lt2c.satin.slider;

import java.io.File;

import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.BenchmarkUtils;
import fr.ujm.tse.lt2c.satin.datamodel.AbstractParser;
import fr.ujm.tse.lt2c.satin.datamodel.BenchResult;
import fr.ujm.tse.lt2c.satin.slider.dictionary.DictionaryPrimitrivesRWLock;
import fr.ujm.tse.lt2c.satin.slider.interfaces.Parser;
import fr.ujm.tse.lt2c.satin.slider.triplestore.VerticalPartioningTripleStoreRWLock;
import fr.ujm.tse.lt2c.satin.slider.utils.ParserImplNaive;

public class SliderParser extends AbstractParser {

	/**
	 * @param datasetfile
	 */
	public SliderParser(final File datasetfile) {
		super(datasetfile);
	}

	private static Logger LOGGER = Logger.getLogger(SliderParser.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.ujm.tse.lt2c.satin.Parser#getModelFromFile(java.io.File)
	 */
	@Override
	public BenchResult getModelFromFile() {
		final Parser parser = new ParserImplNaive(
				new DictionaryPrimitrivesRWLock(),
				new VerticalPartioningTripleStoreRWLock());
		final long startTime = System.nanoTime();
		parser.parse(datasetfile.getAbsolutePath());
		return new BenchResult((System.nanoTime() - startTime)
				/ BenchmarkUtils.NANO_TO_MILISEC_RATIO, false);
	}
}
