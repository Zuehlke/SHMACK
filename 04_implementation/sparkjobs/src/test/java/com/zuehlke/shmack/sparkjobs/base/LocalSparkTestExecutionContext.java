package com.zuehlke.shmack.sparkjobs.base;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

public class LocalSparkTestExecutionContext  {
	private SparkConf conf = new SparkConf()
			.setAppName("LocalSparkTest_NoJobYet")
			.setMaster("local[*]")
			.set("spark.driver.allowMultipleContexts", "true")
			// Not running the Spark UI can speed up testing ... but make debugging harder.
			// So enable depending whether running in debug mode  using 
			// http://stackoverflow.com/questions/3776204/how-to-find-out-if-debug-mode-is-enabled
			.set("spark.ui.enabled", Boolean.toString(java.lang.management.ManagementFactory.getRuntimeMXBean().
					getInputArguments().toString().indexOf("jdwp") >= 0));
	private JavaSparkContext ctx = new JavaSparkContext(conf);

	public JavaSparkContext getSparkContext() {
		return ctx;
	}
	
    public <ResultType> ResultType execute(TestableSparkJob<ResultType> job) {
		conf.setAppName(job.getApplicationName());
		conf.setIfMissing("spark.master", "local[*]");
		conf.setIfMissing("spark.testing", "true");
		
    	return job.execute(ctx);
    }
}
