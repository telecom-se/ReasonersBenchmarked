package fr.ujm.tse.lt2c.satin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.management.RuntimeErrorException;

import org.apache.log4j.Logger;

public class BenchmarkResourcesUtils {
	private static final Logger LOGGER = Logger
			.getLogger(BenchmarkResourcesUtils.class);

	public String extractResourceToTemp(String resourceName, String filePrefix,
			String fileSuffix) {
		InputStream resourceStream = this.getClass().getClassLoader()
				.getResourceAsStream(resourceName);
		File tempFile;
		try {
			tempFile = File.createTempFile(filePrefix, fileSuffix);
			// tempFile.deleteOnExit();
			FileOutputStream fout = null;

			fout = new FileOutputStream(tempFile);
			int c;

			while ((c = resourceStream.read()) != -1) {
				fout.write(c);
			}
			if (resourceStream != null) {
				resourceStream.close();
			}
			if (fout != null) {
				fout.close();
			}
			return tempFile.getAbsolutePath();
		} catch (IOException e) {
			LOGGER.error("cannot create temporary file to extract embeded file in this JAR file (file: "
					+ filePrefix + ")");
			throw new RuntimeErrorException(new Error(
					"cannot write to temporary file "));
		}
	}
}
