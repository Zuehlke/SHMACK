package com.zuehlke.shmack.sparkjobs.base;

import org.apache.spark.api.java.JavaSparkContext;

public abstract class TestableSparkJob<ResultType> {

	public abstract String getApplicationName();
	
    public abstract ResultType execute(JavaSparkContext spark);
    
}
