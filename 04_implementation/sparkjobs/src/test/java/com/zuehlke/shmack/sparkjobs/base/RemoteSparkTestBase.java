package com.zuehlke.shmack.sparkjobs.base;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

public class RemoteSparkTestBase extends SparkTestBase {

    protected JavaSparkContext createSparkContext() {
        final SparkConf conf = new SparkConf().setAppName("JavaWordCount").setMaster("local[2]")
                .set("spark.executor.memory", "1g");
        return new JavaSparkContext(conf);
    }

}
