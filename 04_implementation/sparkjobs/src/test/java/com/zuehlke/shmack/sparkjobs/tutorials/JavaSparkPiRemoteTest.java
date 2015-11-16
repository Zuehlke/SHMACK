package com.zuehlke.shmack.sparkjobs.tutorials;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.spark.api.java.JavaSparkContext;

import com.zuehlke.shmack.sparkjobs.base.HdfsUtils;
import com.zuehlke.shmack.sparkjobs.base.RemoteSparkTestBase;

public class JavaSparkPiRemoteTest extends RemoteSparkTestBase {

	// @Test
	public void testWordCount() {
		final JavaSparkPi sparkPi = new JavaSparkPi();
		try (JavaSparkContext spark = createSparkContext(sparkPi.getApplicationName())) {
			final Double result = sparkPi.execute(spark);
			assertEquals(Math.PI, result, 1e-2);
		}
	}

	public static void main(String[] args) throws IOException, URISyntaxException {
		JavaSparkPi sparkPi = new JavaSparkPi();

		try (JavaSparkContext spark = createSparkContext(sparkPi.getApplicationName())) {
			final Double result = sparkPi.execute(spark);

			File file = new File("/tmp/JavaSparkResult");
			System.out.println("Writing result to local file: " + file.getAbsolutePath());
			String fileContent = "Result: " + result;
			FileUtils.writeStringToFile(file, fileContent, StandardCharsets.UTF_8);

			HdfsUtils.writeStringToHdfsFile(file, fileContent, StandardCharsets.UTF_8);
		}
	}

}
