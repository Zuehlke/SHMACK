package com.zuehlke.shmack.sparkjobs.wordcount;

import static org.junit.Assert.assertEquals;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Test;

import com.zuehlke.shmack.sparkjobs.base.RemoteSparkTestBase;
import com.zuehlke.shmack.sparkjobs.base.SortedCounts;

public class WordCountRemoteTest extends RemoteSparkTestBase {

    @Test
    public void testWordCount() {
        final String inputFile = "src/test/resources/tweets/tweets_big_data_2000.json";
        final WordCount wordCount = new WordCount(inputFile);
        try (JavaSparkContext spark = createSparkContext()) {
            final JavaPairRDD<String, Integer> rdd = wordCount.execute(spark);
            final SortedCounts<String> sortedCounts = SortedCounts.create(rdd);
            assertEquals(7446, sortedCounts.size());
            assertPosition(sortedCounts, 19, 126, "analytics");
            assertPosition(sortedCounts, 1473, 3, "explosive");
            assertPosition(sortedCounts, 7417, 1, "ετοιμαζε");
            sortedCounts.print(System.out);
        }
    }
}
