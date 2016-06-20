package fr.ujm.tse.lt2c.satin;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.management.RuntimeErrorException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.datamodel.AbstractInferer;
import fr.ujm.tse.lt2c.satin.datamodel.AbstractParser;
import fr.ujm.tse.lt2c.satin.datamodel.BenchResult;

/**
 * @author Christophe Gravier, <christophe.gravier@univ-st-etienne.fr>
 * 
 */
public class Benchmark {

	// Heavy usage of private static final variables, I wanted to do it this way
	// to evaluate difference when using
	// heavily complicated enums as in inferray-benchmark project.
	private static final Logger LOGGER = Logger.getLogger(Benchmark.class);

	private Options options;
	private static final String REASONER_OPTION = "reasoner";
	private static final String FRAGMENT_OPTION = "fragment";
	private static final String DATASET_OPTION = "datasets";
	private static final String HELP_OPTION = "help";
	private static final String TIMEOUT_OPTION = "timeout";
	private static final String PARSE_ONLY = "parse-only";
	private static final String ITERATION_OPTION = "iterations";
	private static final String OUTPUTFILE_OPTION = "output-file";

	private static final String INVALIDARG_ERROR = "Invalid arguments.";

	public static final String JENA = "jena";
	public static final String SESAME = "sesame";
	public static final String INFERRAY = "inferray";
	public static final String OWLIMSE = "owlimse";
	public static final String SLIDER = "slider";
	public static final String RDFOX = "rdfox";

	public static final String RDFSDEFAULT = "rdfs-default";
	public static final String RDFSFULL = "rdfs-full";
	public static final String RHODF = "rho-df";
	public static final String RDFSPP = "rdfs++";

	private String reasonerConfiguration = null;
	private String fragmentConfiguration = null;
	private String datasetConfiguration = null;
	private boolean parseonlyConfiguration = false;
	private int iterationsConfiguration = 1;
	private int timeoutConfiguration = 10;
	private String outputFileConfiguration = null;

	@SuppressWarnings("serial")
	private static final List<String> REASONERS = new ArrayList<String>() {
		{
			this.add(JENA);
			this.add(SESAME);
			this.add(INFERRAY);
			this.add(OWLIMSE);
			this.add(SLIDER);
			this.add(RDFOX);
		}

		@Override
		public String toString() {
			return "{\"" + JENA + "\", \"" + SESAME + "\", \"" + INFERRAY
					+ "\", \"" + OWLIMSE + "\", \"" + SLIDER + "\", \"" + RDFOX + "\"}";
		}
	};

	@SuppressWarnings("serial")
	private static final List<String> FRAGMENTS = new ArrayList<String>() {
		{
			this.add(RDFSDEFAULT);
			this.add(RDFSFULL);
			this.add(RHODF);
			this.add(RDFSPP);
		}

		@Override
		public String toString() {
			return "{\"" + RDFSDEFAULT + "\", \"" + RDFSFULL + "\", \"" + RHODF
					+ "\", \"" + RDFSPP + "\"}";
		}
	};

	public static void main(final String[] args) {

		configureLog4jFromSystemProperties();

		final Benchmark bench = new Benchmark(args);
		long value = 0L;
		BenchResult benchResult = null;
		if (bench.isParseonlyConfiguration()) {
			for (int i = 0; i < bench.getIterationsConfiguration(); i++) {
				final AbstractParser parseEngine = BenchmarkFactory
						.getParser(bench);

				benchResult = bench.callParseBenchmark(parseEngine);
				value += benchResult.getExecTimeInMiliseconds();

			}
			LOGGER.info("\n*********\nAVG PARSE TIME ("
					+ bench.getIterationsConfiguration() + " runs) : "
					+ (value / bench.getIterationsConfiguration())
					+ " ms for configuration : " + bench.toString()
					+ "\n*********\n");
		} else {
			for (int i = 0; i < bench.getIterationsConfiguration(); i++) {
				final AbstractInferer inferenceEngine = BenchmarkFactory
						.getInferer(bench);
				benchResult = bench.callInferBenchmark(inferenceEngine);
			}
			LOGGER.info("\n*********\nLAST INFER TIME : "
					+ benchResult.getExecTimeInMiliseconds() + " ms , for configuration : "
					+ bench.toString() + "\n*********\n");
		}
		System.out.println(benchResult);
		BenchmarkUtils.logResult(bench, benchResult);
		System.exit(0);
	}

	/**
	 * @param inferenceEngine
	 */
	private BenchResult callParseBenchmark(final AbstractParser underTest) {
		LOGGER.info("Benchmarking parse engine "
				+ this.getReasonerConfiguration());
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final FutureTask<BenchResult> future = new FutureTask<BenchResult>(
				underTest);
		try {
			executor.submit(future);
			return future.get(this.timeoutConfiguration, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
			LOGGER.fatal("Cannot evaluate PARSE for the following configuration : "
					+ this.toString());
			throw new RuntimeErrorException(new Error(
					"Interrupted exception for the following configuration : "
							+ this.toString()));
		} catch (final ExecutionException e) {
			e.printStackTrace();
			LOGGER.fatal("Cannot evaluate PARSE for the following configuration : "
					+ this.toString());
			throw new RuntimeErrorException(new Error(
					"Execute exception for the following configuration : "
							+ this.toString()));
		} catch (final TimeoutException e) {
			LOGGER.info("Timeout for the following configuration : "
					+ this.toString());
			return new BenchResult(0, true);
		}
	}

	/**
	 * @param inferenceEngine
	 */
	private BenchResult callInferBenchmark(final AbstractInferer underTest) {
		LOGGER.info("Benchmarking inference engine "
				+ this.getReasonerConfiguration());
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final FutureTask<BenchResult> future = new FutureTask<BenchResult>(
				underTest);
		try {
			executor.submit(future);

			return future.get(this.timeoutConfiguration, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
			LOGGER.fatal("Cannot evaluate INFER for the following configuration : "
					+ this.toString());
			throw new RuntimeErrorException(new Error(
					"Interrupted exception for the following configuration : "
							+ this.toString()));
		} catch (final ExecutionException e) {
			LOGGER.fatal("Cannot evaluate INFER for the following configuration : "
					+ this.toString());
			e.printStackTrace();
			throw new RuntimeErrorException(new Error(
					"Execute exception for the following configuration : "
							+ this.toString()));
		} catch (final TimeoutException e) {
			LOGGER.info("Timeout for the following configuration : "
					+ this.toString());

			return new BenchResult(0, true);
		}
	}

	/**
	 * Hides default constructor.
	 */
	@SuppressWarnings("unused")
	private Benchmark() {
		LOGGER.fatal("This constructor not have been called!");
		throw new RuntimeErrorException(new Error(
				"This constructor not have been called."));
	}

	/**
	 * @param options
	 */
	public Benchmark(final String[] args) {
		this.options = this.createCliOptions();
		try {
			final CommandLineParser parser = new GnuParser();
			final CommandLine cmd = parser.parse(this.options, args);
			if (cmd.hasOption(HELP_OPTION)) {
				this.printHelp();
			} else {
				this.checkOptionsAndFailIfNeeded(cmd);
			}

			this.reasonerConfiguration = cmd.getOptionValue("R");
			this.datasetConfiguration = cmd.getOptionValue("D");
			this.fragmentConfiguration = cmd.getOptionValue("F");
			this.timeoutConfiguration = Integer.parseInt(cmd
					.getOptionValue("T"));
			this.iterationsConfiguration = Integer.parseInt(cmd
					.getOptionValue("I"));
			this.parseonlyConfiguration = "true"
					.equals(cmd.getOptionValue("P"));
			this.outputFileConfiguration = cmd.getOptionValue("O");
		} catch (final ParseException e) {
			LOGGER.error("Cannot parse program command line. Usage :", e);
			this.printHelp();
		}

		owlimSpecialConfiguration();
	}

	private void owlimSpecialConfiguration() {
		if (reasonerConfiguration.equals("owlimse")) {
			Properties p = System.getProperties();
			p.setProperty("imports", datasetConfiguration);
//			if (fragmentConfiguration.equals("rdfs-default")) {
//				p.setProperty("ruleset", "rdfs-optimized");
//			} else if (fragmentConfiguration.equals("rdfs-full")) {
//				p.setProperty("ruleset", "rdfs");
//			} else if (fragmentConfiguration.equals("rho-df")) {
//				String fragment = System.getProperty("java.io.tmpdir")
//						+ System.getProperty("file.separator") + "rhodf.pie";
//				setFragment(p, fragment);
//			} else if (fragmentConfiguration.equals("rdfs++")) {
//				String fragment = System.getProperty("java.io.tmpdir")
//						+ System.getProperty("file.separator") + "rdfsplus.pie";
//				setFragment(p, fragment);
//			} else {
//				p.setProperty("ruleset", "empty");
//				LOGGER.warn("Unsuported fragment provided for owlim-se (you provided *"
//						+ datasetConfiguration + "*)");
//			}
			p.setProperty("ruleset", "empty");
			p.setProperty("debug.level", "5");

		}
	}

	private void setFragment(Properties p, String fragment) {
		if (new File(fragment).exists()) {
			p.setProperty("ruleset", fragment);
		} else {
			throw new RuntimeException(
					new Error("The .pie file for the fragment " + fragment
							+ " was not properly extracted to "
							+ System.getProperty("java.io.tmpdir")
							+ System.getProperty("file.separator")
							+ "rhodf.pie by the inferray-benchmark executable."));
		}
	}

	/**
	 * @param cmd
	 */
	private void checkOptionsAndFailIfNeeded(final CommandLine cmd) {
		if (!cmd.hasOption("R") || !cmd.hasOption("D") || !cmd.hasOption("F")
				|| !cmd.hasOption("T") || !cmd.hasOption("I")
				|| !cmd.hasOption("P") || !cmd.hasOption("O")) {
			LOGGER.fatal("Invalid program argument. Missing reasoner, dataset, fragment, timeout, iterations, parse-only, outputfile and/or timeout arguments.");
			this.printHelp();
			throw new RuntimeErrorException(new Error("Invalid program argument. Missing reasoner, dataset, fragment, timeout, iterations, parse-only, outputfile and/or timeout arguments."));
		} else {
			if (!REASONERS.contains(cmd.getOptionValue("R"))) {
				LOGGER.fatal("Unexpected reasoner option.");
				this.printHelp();
				throw new RuntimeErrorException(new Error("Reasoner option :"+INVALIDARG_ERROR));
			} else if (!FRAGMENTS.contains(cmd.getOptionValue("F"))) {
				LOGGER.fatal("Unexpected logical fragment option.");
				this.printHelp();
				throw new RuntimeErrorException(new Error("Fragment option :"+INVALIDARG_ERROR));
			} else if (!this.isANumber(cmd.getOptionValue("T"))) {
				LOGGER.fatal("Timeout value (" + cmd.getOptionValue("T")
						+ ") is not a valid number.");
				this.printHelp();
				throw new RuntimeErrorException(new Error("Timeout option :"+INVALIDARG_ERROR));
			} else if (!this.isANumber(cmd.getOptionValue("I"))) {
				LOGGER.fatal("Timeout value (" + cmd.getOptionValue("I")
						+ ") is not a valid number.");
				this.printHelp();
				throw new RuntimeErrorException(new Error("Iteration option :"+INVALIDARG_ERROR));
			} else if (!this.fileExist(cmd.getOptionValue("D"))) {
				LOGGER.fatal("Dataset file (" + cmd.getOptionValue("D")
						+ ") does not exist or is not readable.");
				this.printHelp();
				throw new RuntimeErrorException(new Error("Dataset option :"+INVALIDARG_ERROR+" Dataset file (" + cmd.getOptionValue("D")
						+ ") does not exist or is not readable."));
			} else if (cmd.getOptionValue("O").isEmpty()) {
				LOGGER.fatal("Output file (" + cmd.getOptionValue("O")
						+ ") does not exist or is not readbale.");
				this.printHelp();
				throw new RuntimeErrorException(new Error("Outputfile option :"+INVALIDARG_ERROR));
			} else if (!"true".equals(cmd.getOptionValue("P"))
					&& !"false".equals(cmd.getOptionValue("P"))) {
				LOGGER.fatal("Parse-only arguement (true or false) was not provided or was incorrect.");
				this.printHelp();
				throw new RuntimeErrorException(new Error("Parse-only option :"+INVALIDARG_ERROR));
			}
		}
	}

	/**
	 * @param optionValue
	 * @return
	 */
	private boolean fileExist(final String optionValue) {
		final File f = new File(optionValue);
		return (f.exists() && f.canRead());
	}

	/**
	 * @param optionValue
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean isANumber(final String optionValue) {
		try {
			final Integer i = Integer.parseInt(optionValue);
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	/**
	 * Initialize the program CLI for parsing and help.
	 * 
	 * @return The CLI Options for <code>this</code>
	 */
	private Options createCliOptions() {
		this.options = new Options();
		this.options.addOption(
				"R",
				REASONER_OPTION,
				true,
				"Reasoner to benchmark. Possible values: "
						+ REASONERS.toString());
		this.options.addOption("D", DATASET_OPTION, true,
				"Datasets to benchmark as its absolute path on filesystem.");
		this.options.addOption("F", FRAGMENT_OPTION, true,
				"Logical fragment to benchmark with. Possible values: "
						+ FRAGMENTS.toString());
		this.options
				.addOption(
						"T",
						TIMEOUT_OPTION,
						true,
						"Max number of seconds for a reasoner to perform the inference task on a given fragment and a given dataset under test.");
		this.options
				.addOption(
						"P",
						PARSE_ONLY,
						true,
						"Should the reasoner only parse the data (when set to true) or also perform inference (when set to false).");
		this.options
				.addOption(
						"I",
						ITERATION_OPTION,
						true,
						"Number of iterations to perform. The program will compute an average of parsing time for this number of iteration when "
								+ PARSE_ONLY
								+ " was set to true, and will compute parsing+inference time only for the last iteration when the option was set to false (In this case the previous iterations serves as warmup).");
		this.options.addOption("O", OUTPUTFILE_OPTION, true,
				"Where to append the result line for this benchmark call.");
		this.options
				.addOption("H", HELP_OPTION, false, "Display help command.");
		return this.options;
	}

	/**
	 * Display the command help to the standard output.
	 * 
	 * @param options
	 *            the options that can be used with the program.
	 */
	private void printHelp() {
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Benchmark", this.options);
	}

	/**
	 * @return the options
	 */
	public Options getOptions() {
		return this.options;
	}

	/**
	 * @param options
	 *            the options to set
	 */
	public void setOptions(final Options options) {
		this.options = options;
	}

	/**
	 * @return the reasonerConfiguration
	 */
	public String getReasonerConfiguration() {
		return this.reasonerConfiguration;
	}

	/**
	 * @param reasonerConfiguration
	 *            the reasonerConfiguration to set
	 */
	public void setReasonerConfiguration(final String reasonerConfiguration) {
		this.reasonerConfiguration = reasonerConfiguration;
	}

	/**
	 * @return the fragmentConfiguration
	 */
	public String getFragmentConfiguration() {
		return this.fragmentConfiguration;
	}

	/**
	 * @param fragmentConfiguration
	 *            the fragmentConfiguration to set
	 */
	public void setFragmentConfiguration(final String fragmentConfiguration) {
		this.fragmentConfiguration = fragmentConfiguration;
	}

	/**
	 * @return the datasetConfiguration
	 */
	public String getDatasetConfiguration() {
		return this.datasetConfiguration;
	}

	/**
	 * @param datasetConfiguration
	 *            the datasetConfiguration to set
	 */
	public void setDatasetConfiguration(final String datasetConfiguration) {
		this.datasetConfiguration = datasetConfiguration;
	}

	/**
	 * @return the parseonlyConfiguration
	 */
	public boolean isParseonlyConfiguration() {
		return this.parseonlyConfiguration;
	}

	/**
	 * @param parseonlyConfiguration
	 *            the parseonlyConfiguration to set
	 */
	public void setParseonlyConfiguration(final boolean parseonlyConfiguration) {
		this.parseonlyConfiguration = parseonlyConfiguration;
	}

	/**
	 * @return the iterationsConfiguration
	 */
	public int getIterationsConfiguration() {
		return this.iterationsConfiguration;
	}

	/**
	 * @param iterationsConfiguration
	 *            the iterationsConfiguration to set
	 */
	public void setIterationsConfiguration(final int iterationsConfiguration) {
		this.iterationsConfiguration = iterationsConfiguration;
	}

	/**
	 * @return the timeoutConfiguration
	 */
	public int getTimeoutConfiguration() {
		return this.timeoutConfiguration;
	}

	/**
	 * @param timeoutConfiguration
	 *            the timeoutConfiguration to set
	 */
	public void setTimeoutConfiguration(final int timeoutConfiguration) {
		this.timeoutConfiguration = timeoutConfiguration;
	}

	/**
	 * @return the outputFileConfiguration
	 */
	public String getOutputFileConfiguration() {
		return this.outputFileConfiguration;
	}

	/**
	 * @param outputFileConfiguration
	 *            the outputFileConfiguration to set
	 */
	public void setOutputFileConfiguration(final String outputFileConfiguration) {
		this.outputFileConfiguration = outputFileConfiguration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Benchmark [reasonerConfiguration=" + this.reasonerConfiguration
				+ ", fragmentConfiguration=" + this.fragmentConfiguration
				+ ", datasetConfiguration=" + this.datasetConfiguration
				+ ", parseonlyConfiguration=" + this.parseonlyConfiguration
				+ ", iterationsConfiguration=" + this.iterationsConfiguration
				+ ", timeoutConfiguration=" + this.timeoutConfiguration
				+ ", outputFileConfiguration=" + this.outputFileConfiguration
				+ "]";
	}
	@SuppressWarnings("unused")
	public static void configureLog4jFromSystemProperties() {
		final String LOGGER_PREFIX = "log4j.";

		for (String propertyName : System.getProperties().stringPropertyNames()) {
			if (propertyName.startsWith(LOGGER_PREFIX)) {
				
				String loggerName = propertyName.substring(LOGGER_PREFIX
						.length());
				String levelName = System.getProperty(propertyName, "");
				Level level = Level.toLevel(levelName); // defaults to DEBUG
				if (!"".equals(levelName)
						&& !levelName.toUpperCase().equals(level.toString())) {
					LOGGER.error("Skipping unrecognized log4j log level "
							+ levelName + ": -D" + propertyName + "="
							+ levelName);
					continue;
				}

				for (@SuppressWarnings("rawtypes")
				Enumeration loggers = LogManager.getCurrentLoggers(); loggers
						.hasMoreElements();) {
					Logger logger = (Logger) loggers.nextElement();
					LOGGER.info("Setting " + logger.getName() + " => "
							+ level.toString());
					logger.setLevel(level);
				}
			}
		}
	}
}
