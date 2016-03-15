package com.zuehlke.shmack.sparkjobs.wordcount;

import static org.junit.Assert.assertEquals;
import static com.zuehlke.shmack.sparkjobs.wordcount.SortedCountAsserter.assertPosition;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.shmack.sparkjobs.base.HdfsUtils;
import com.zuehlke.shmack.sparkjobs.base.RemoteSparkTestExecutionContext;
import com.zuehlke.shmack.sparkjobs.base.RemoteSparkTestRunner;
import com.zuehlke.shmack.sparkjobs.base.SortedCounts;

public class WordCountRemoteTest {

	private final static Logger LOGGER = LoggerFactory.getLogger(WordCountRemoteTest.class);

	@Test
	public void testWordcountRemote() throws Exception {
		RemoteSparkTestRunner runner = new RemoteSparkTestRunner(WordCountRemoteTest.class, "myTest-1");
		runner.syncTestRessourcesToHdfs();

		runner.executeSparkRemote();
		runner.waitForSparkFinished();

		SortedCounts<String> sortedCounts = runner.getRemoteResult();
		assertEquals(7446, sortedCounts.size());
		assertPosition(sortedCounts, 19, 126, "analytics");
		assertPosition(sortedCounts, 1473, 3, "explosive");
		assertPosition(sortedCounts, 7417, 1, "ετοιμαζε");

	}

	public static void main(String[] args) throws Exception {
		HdfsUtils.pingHdfs();

		RemoteSparkTestExecutionContext execution = new RemoteSparkTestExecutionContext(WordCountRemoteTest.class, "myTest-1");
		String inputFile = RemoteSparkTestRunner.getHdfsTestRessourcePath("tweets/tweets_big_data_2000.json");
		LOGGER.info("Using input file for testing: " + inputFile);
		WordCount wordCount = new WordCount(inputFile);
		execution.executeWithStatusTracking(wordCount);
	}

}
