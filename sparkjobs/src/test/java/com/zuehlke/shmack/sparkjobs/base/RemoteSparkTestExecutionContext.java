package com.zuehlke.shmack.sparkjobs.base;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.spark.api.java.JavaSparkContext;

public class RemoteSparkTestExecutionContext {

	static final File SPARK_TESTS_HDFS_FOLDER = new File("/spark-tests");

	public static final String SUBMITTED = "SUBMITTED";
	public static final String RUNNING = "RUNNING";
	public static final String FAILED = "FAILED";
	public static final String SUCCESSFUL = "SUCCESSFUL";


	private String testcaseId;
	private Class<?> testClass;
	private JavaSparkContext ctx = new JavaSparkContext();

	public String getTestcaseId() {
		return testcaseId;
	}
	
	public Class<?> getTestClass() {
		return testClass;
	}
	
	public JavaSparkContext getSparkContext() {
		return ctx;
	}

	/**
	 * @param testcaseId
	 *            used to create distinct directories for results and input
	 *            files.
	 */
	public RemoteSparkTestExecutionContext(Class<?> testClass, String testcaseId) {
		this.testClass = testClass;
		if (testcaseId.contains(" ")) {
			throw new IllegalArgumentException(
					"Should not contains spaces to avoid Directory paths with spaces: " + testcaseId);
		}
		this.testcaseId = testcaseId;
	}

	public final void executeWithStatusTracking(TestableSparkJob<? extends Serializable> job) throws Exception {
		boolean successful = false;
		try {
			ctx.sc().conf().setAppName(job.getApplicationName()); 
			writeStatus(RUNNING, null);
			Serializable result = job.execute(ctx);
			writeResultObject(result);
			writeStatus(SUCCESSFUL, null);
			successful = true;
		} catch (Exception ex) {
			writeStatus(FAILED, "Caught Exception: \n" + ex + " \n " + ExceptionUtils.getStackTrace(ex));
			throw ex;
		} finally {
			if (!successful) {
				writeStatus(FAILED,
						"something really very bad happended. Maybe a java.lang.Error (which shall never be caught) ...");
			}
		}
	}

	private void writeStatus(String status, String details) {
		try {
			HdfsUtils.writeStringToHdfsFile(RemoteSparkTestRunner.getHdfsStatusFile(testClass, testcaseId), status);
			if (details != null) {
				HdfsUtils.writeStringToHdfsFile(RemoteSparkTestRunner.getHdfsDetailsFile(testClass, testcaseId), details);
			}
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException("Failed to write test status or details to HDFS", e);
		}
	}
	
	private void writeResultObject(Serializable result) throws IOException, URISyntaxException {
		File binFile = RemoteSparkTestRunner.getResultBinFile(testClass, testcaseId);
		HdfsUtils.writeObjectToHdfsFile(binFile, result);
	}

}
