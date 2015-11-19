package com.zuehlke.shmack.sparkjobs.wordcount;

import com.zuehlke.shmack.sparkjobs.base.HdfsUtils;
import com.zuehlke.shmack.sparkjobs.base.RemoteSparkTestBase;

public class WordCountRemoteTest extends RemoteSparkTestBase {

	// final SortedCounts<String> sortedCounts = SortedCounts.create(rdd);
	// assertEquals(7446, sortedCounts.size());
	// assertPosition(sortedCounts, 19, 126, "analytics");
	// assertPosition(sortedCounts, 1473, 3, "explosive");
	// assertPosition(sortedCounts, 7417, 1, "ετοιμαζε");

	/**
	 * <pre>
	 * To be invoked before:
	 * sync-to-hdfs.sh ~/shmack/repo/04_implementation/sparkjobs/src/test/resources /sparkjobs-tests-resources
	 * 
	 * --> results in the following hdfs files:
	 * hadoop fs -ls hdfs://hdfs/sparkjobs-tests-resources
	 * drwxrwxr-x   - core core          0 2015-11-17 12:46 hdfs://hdfs/sparkjobs-tests-resources/tweets
	 * </pre>
	 */
	public static void main(String[] args) throws Exception {

		HdfsUtils.pingHdfs();

		String inputFile = "hdfs://hdfs/sparkjobs-tests-resources/tweets/tweets_big_data_2000.json";
		System.err.println("Using input file for testing: " + inputFile);
		WordCount wordCount = new WordCount(inputFile);
		WordCountRemoteTest myTest = new WordCountRemoteTest();
		myTest.executeWithStatusTracking(wordCount);
	}

}
