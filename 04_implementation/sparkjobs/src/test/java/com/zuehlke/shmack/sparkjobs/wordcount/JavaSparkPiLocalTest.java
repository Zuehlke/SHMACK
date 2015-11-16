package com.zuehlke.shmack.sparkjobs.wordcount;

import static org.junit.Assert.assertEquals;

import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Test;

import com.zuehlke.shmack.sparkjobs.tutorials.JavaSparkPi;

public class JavaSparkPiLocalTest extends LocalSparkTestBase {

	@Test
	public void testWordCount() {
		final JavaSparkPi wordCount = new JavaSparkPi();
		try (JavaSparkContext spark = createSparkContext(wordCount.getApplicationName())) {
			final Double result = wordCount.execute(spark);
			assertEquals(Math.PI, result, 1e-2);
		}
	}

}
