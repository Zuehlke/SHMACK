package com.zuehlke.shmack.sparkjobs.tutorials;

import static org.junit.Assert.assertEquals;

import org.apache.spark.api.java.JavaSparkContext;

import com.zuehlke.shmack.sparkjobs.base.RemoteSparkTestBase;

public class JavaSparkPiRemoteTest extends RemoteSparkTestBase {

	// @Test
	public void testWordCount() throws Exception {
		final JavaSparkPi sparkPi = new JavaSparkPi();
		try (JavaSparkContext spark = createSparkContext(sparkPi.getApplicationName())) {
			final Double result = sparkPi.execute(spark);
			assertEquals(Math.PI, result, 1e-2);
		}
	}

	public static void main(String[] args) throws Exception {
		JavaSparkPi sparkPi = new JavaSparkPi();
		JavaSparkPiRemoteTest myTest = new JavaSparkPiRemoteTest();
		myTest.executeWithStatusTracking(sparkPi);
	}

}
