package com.zuehlke.shmack.sparkjobs.base;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

public class LocalSparkTestBase extends ShmackTestBase {

    protected JavaSparkContext createSparkContext(String appName) {
        final SparkConf conf = new SparkConf().setAppName(appName).setMaster("local[*]")
                .set("spark.executor.memory", "1g");
        return new JavaSparkContext(conf);
    }

}
