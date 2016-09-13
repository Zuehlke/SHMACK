package com.zuehlke.shmack.sparkjobs.wordcount;

import static com.zuehlke.shmack.sparkjobs.wordcount.SortedCountAsserter.assertPosition;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.shmack.sparkjobs.base.LocalSparkTestRunner;
import com.zuehlke.shmack.sparkjobs.base.SortedCounts;


public class WordCountLocalTest {

	@Test
	public void testWordCount() throws Exception {
		final String inputFile = "src/test/resources/tweets/tweets_big_data_2000.json";
		final WordCount wordCount = new WordCount(inputFile);
		final SortedCounts<String> sortedCounts = LocalSparkTestRunner.execute(wordCount);
		assertEquals(7446, sortedCounts.size());
		assertPosition(sortedCounts, 19, 126, "analytics");
		assertPosition(sortedCounts, 1473, 3, "explosive");
		assertPosition(sortedCounts, 7417, 1, "ετοιμαζε");
	}
}
