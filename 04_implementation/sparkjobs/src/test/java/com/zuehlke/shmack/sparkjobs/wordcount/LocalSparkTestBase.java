package com.zuehlke.shmack.sparkjobs.wordcount;

import static org.junit.Assert.assertEquals;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import com.zuehlke.shmack.sparkjobs.wordcount.SortedCounts.Entry;

public class LocalSparkTestBase {

    protected static <T> void assertPosition(final SortedCounts<T> sortedCounts, final int position,
            final int expectedCount, final T expectedValue) {
        final Entry<T> entry = sortedCounts.getEntry(position);
        assertEquals(expectedCount, entry.getCount());
        assertEquals(expectedValue, entry.getValue());
    }

    protected JavaSparkContext createSparkContext() {
        final SparkConf conf = new SparkConf().setAppName("JavaWordCount").setMaster("local[2]")
                .set("spark.executor.memory", "1g");
        return new JavaSparkContext(conf);
    }

}
