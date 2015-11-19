package com.zuehlke.shmack.sparkjobs.base;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

public class RemoteSparkTestBase extends ShmackTestBase {

	protected static JavaSparkContext createSparkContext(String appName) {
		SparkConf sparkConf = new SparkConf().setAppName(appName);
		JavaSparkContext jsc = new JavaSparkContext(sparkConf);
		return jsc;
	}

	public static final String RUNNING = "RUNNING";
	public static final String FAILED = "FAILED";
	public static final String SUCCESSFUL = "SUCCESSFUL";
	public static final String FINISHED = "EXCEPTION";

	protected final void executeWithStatusTracking(TestableSparkJob<? extends Serializable> job) throws Exception {
		boolean successful = false;
		try (JavaSparkContext spark = createSparkContext(job.getApplicationName())) {
			HdfsUtils.deleteInHdfs(getTestJobFolder());
			writeStatus(RUNNING, null);
			Serializable result = job.execute(spark);
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

	private void writeResultObject(Serializable result) throws IOException, URISyntaxException {
		File binFile = new File(getTestJobFolder(), "RESULT");
		HdfsUtils.writeObjectToHdfsFile(binFile, result);
	}

	private void writeStatus(String status, String details) {
		try {
			HdfsUtils.writeStringToHdfsFile(getStatusFile(), status);
			if (details != null) {
				HdfsUtils.writeStringToHdfsFile(getDetailsFile(), details);
			}
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException("Failed to write test status or details to HDFS", e);
		}
	}

	private File getStatusFile() {
		return new File(getTestJobFolder(), "STATUS");
	}

	private File getDetailsFile() {
		return new File(getTestJobFolder(), "DETAILS");
	}

	private File getTestJobFolder() {
		return new File("/spark-tests/" + getClass().getName());
	}

}
