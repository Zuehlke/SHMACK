package com.zuehlke.shmack.sparkjobs.tutorials;

import static org.junit.Assert.assertEquals;

import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.shmack.sparkjobs.base.LocalSparkTestBase;

public class JavaSparkPiLocalTest extends LocalSparkTestBase {

	private final static Logger LOGGER = LoggerFactory.getLogger(JavaSparkPiLocalTest.class);
	
	@Test
	public void testWordcountLocal_10_Slices() throws Exception {
		testWordcountLocal(10, 1e-2);
	}

	@Test
	public void testWordcountLocal_100_Slices() throws Exception {
		testWordcountLocal(100, 1e-3);
	}

	@Test
	public void testWordcountLocal_200_Slices() throws Exception {
		testWordcountLocal(200, 1e-3);
	}

	private void testWordcountLocal(int nSlices, double allowedDelta) throws Exception {
		final JavaSparkPi sparkPi = new JavaSparkPi( nSlices );
		try (JavaSparkContext spark = createSparkContext(sparkPi.getApplicationName())) {
			final Double result = sparkPi.execute(spark);
			LOGGER.info("Result of Pi with {} number of slices: {}", nSlices, result);
			LOGGER.info("Difference from real Pi: {} ", Math.abs(Math.PI - result));
			assertEquals(Math.PI, result.doubleValue(), allowedDelta);
		}
	}

}
