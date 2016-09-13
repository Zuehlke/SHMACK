package com.zuehlke.shmack.sparkjobs.base;

public class LocalSparkTestRunner {
	   public static <ResultType> ResultType execute(TestableSparkJob<ResultType> job) {
		   LocalSparkTestExecutionContext execution = new LocalSparkTestExecutionContext();
		   return execution.execute(job);
	   }
}
