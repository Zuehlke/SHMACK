package com.zuehlke.shmack.sparkjobs.wordcount;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.shmack.sparkjobs.base.HdfsUtils;
import com.zuehlke.shmack.sparkjobs.base.RemoteSparkTestBase;
import com.zuehlke.shmack.sparkjobs.base.SortedCounts;

public class WordCountRemoteTest extends RemoteSparkTestBase {

	@Test
	public void testWordcountRemote() throws Exception {
		syncTestRessourcesToHdfs();
		executeSparkRemote();
		waitForSparkFinished();

		SortedCounts<String> sortedCounts = getRemoteResult();
		assertEquals(7446, sortedCounts.size());
		assertPosition(sortedCounts, 19, 126, "analytics");
		assertPosition(sortedCounts, 1473, 3, "explosive");
		assertPosition(sortedCounts, 7417, 1, "ετοιμαζε");

	}

	public static void main(String[] args) throws Exception {

		HdfsUtils.pingHdfs();

		String inputFile = getHdfsTestRessourcePath("tweets/tweets_big_data_2000.json");
		System.out.println("Using input file for testing: " + inputFile);
		WordCount wordCount = new WordCount(inputFile);
		WordCountRemoteTest myTest = new WordCountRemoteTest();
		myTest.executeWithStatusTracking(wordCount);
	}

}
