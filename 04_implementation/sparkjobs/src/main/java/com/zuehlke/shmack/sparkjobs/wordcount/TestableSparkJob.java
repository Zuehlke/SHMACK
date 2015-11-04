package com.zuehlke.shmack.sparkjobs.wordcount;

import org.apache.spark.api.java.JavaSparkContext;

public abstract class TestableSparkJob<ResultType> {

    public abstract ResultType execute(JavaSparkContext spark);
    
}
