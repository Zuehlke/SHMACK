package com.zuehlke.shmack.sparkjobs.wordcount;

import static org.junit.Assert.assertEquals;

import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Test;

import com.zuehlke.shmack.sparkjobs.base.LocalSparkTestBase;
import com.zuehlke.shmack.sparkjobs.base.SortedCounts;

public class WordCountLocalTest extends LocalSparkTestBase {

    @Test
    public void testWordCount() throws Exception {
        final String inputFile = "src/test/resources/tweets/tweets_big_data_2000.json";
        final WordCount wordCount = new WordCount(inputFile);
        try (JavaSparkContext spark = createSparkContext(wordCount.getApplicationName())) {
            final SortedCounts<String> sortedCounts = wordCount.execute(spark);
            assertEquals(7446, sortedCounts.size());
            assertPosition(sortedCounts, 19, 126, "analytics");
            assertPosition(sortedCounts, 1473, 3, "explosive");
            assertPosition(sortedCounts, 7417, 1, "ετοιμαζε");
            sortedCounts.print(System.out);
        }
    }
}
