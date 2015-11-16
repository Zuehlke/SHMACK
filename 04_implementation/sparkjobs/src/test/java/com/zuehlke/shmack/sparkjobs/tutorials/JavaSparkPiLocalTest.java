package com.zuehlke.shmack.sparkjobs.tutorials;

import static org.junit.Assert.assertEquals;

import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Test;

import com.zuehlke.shmack.sparkjobs.base.LocalSparkTestBase;

public class JavaSparkPiLocalTest extends LocalSparkTestBase {

	@Test
	public void testWordCount() {
		final JavaSparkPi sparkPi = new JavaSparkPi();
		try (JavaSparkContext spark = createSparkContext(sparkPi.getApplicationName())) {
			final Double result = sparkPi.execute(spark);
			assertEquals(Math.PI, result, 1e-2);
		}
	}

}
