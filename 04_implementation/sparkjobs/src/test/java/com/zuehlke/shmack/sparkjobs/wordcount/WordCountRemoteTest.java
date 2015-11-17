package com.zuehlke.shmack.sparkjobs.wordcount;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import com.zuehlke.shmack.sparkjobs.base.HdfsUtils;
import com.zuehlke.shmack.sparkjobs.base.RemoteSparkTestBase;
import com.zuehlke.shmack.sparkjobs.base.SortedCounts;

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
	public static void main(String[] args) throws IOException, URISyntaxException {
		String inputFile = "hdfs://hdfs/sparkjobs-tests-resources/tweets/tweets_big_data_2000.json";
		System.err.println("Using input file for testing: " + inputFile);
		WordCount sparkPi = new WordCount(inputFile);

		try (JavaSparkContext spark = createSparkContext(sparkPi.getApplicationName())) {
			final JavaPairRDD<String, Integer> result = sparkPi.execute(spark);

			final SortedCounts<String> sortedCounts = SortedCounts.create(result);

			File textFile = new File("/tmp/WordCountRemoteTest/Result.txt");
			System.out.println("Writing results to HDFS file " + textFile.getAbsolutePath() );
			String fileContent = sortedCounts.toString();
			HdfsUtils.writeStringToHdfsFile(textFile, fileContent, StandardCharsets.UTF_8);

			System.out.println("Writing results to HDFS file " + textFile.getAbsolutePath() );
			File binFile = new File("/tmp/WordCountRemoteTest/Result.bin");
			HdfsUtils.writeStringToHdfsFile(binFile, fileContent, StandardCharsets.UTF_8);

		}
	}

}
