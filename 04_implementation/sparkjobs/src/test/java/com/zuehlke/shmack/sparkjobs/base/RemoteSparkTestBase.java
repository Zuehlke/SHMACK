package com.zuehlke.shmack.sparkjobs.base;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

public class RemoteSparkTestBase extends SparkTestBase {

    protected static JavaSparkContext createSparkContext(String appName) {
        SparkConf sparkConf = new SparkConf().setAppName(appName);
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);
        return jsc;
    }
}
