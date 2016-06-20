package fr.ujm.tse.lt2c.satin.inferray;

import fr.ujm.tse.lt2c.satin.inferray.configuration.ConfigurationBuilder;
import fr.ujm.tse.lt2c.satin.inferray.configuration.PropertyConfiguration;
import fr.ujm.tse.lt2c.satin.inferray.reasoner.Inferray;
import fr.ujm.tse.lt2c.satin.inferray.rules.profile.SupportedProfile;

public class InferrayUtils {

	/**
	 * Instanciate you shall not !
	 */
	private InferrayUtils() {

	}

	public static Inferray infererParse(SupportedProfile profile, String file,
			String axiomaticTriplesExtractDirectory) {
		final ConfigurationBuilder builder = new ConfigurationBuilder();
		final PropertyConfiguration config = builder
				.setDumpFileOnExit(false)
				.setForceQuickSort(false)
				.setMultithread(true)
				.setThreadpoolSize(8)
				.setFastClosure(false)
				.setRulesProfile(profile)
				.setAxiomaticTriplesDirectory(
						axiomaticTriplesExtractDirectory
								+ System.getProperty("file.separator")).build();
		final Inferray inferray = new Inferray(config);
		inferray.parse(file);
		return inferray;
	}
}
