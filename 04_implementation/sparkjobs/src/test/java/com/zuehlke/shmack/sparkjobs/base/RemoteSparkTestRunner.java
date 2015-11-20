package com.zuehlke.shmack.sparkjobs.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.shmack.sparkjobs.infrastructure.ExecExceptionHandling;
import com.zuehlke.shmack.sparkjobs.infrastructure.ExecuteResult;
import com.zuehlke.shmack.sparkjobs.infrastructure.ShmackUtils;

public class RemoteSparkTestRunner extends ShmackTestBase {

	private final static Logger LOGGER = LoggerFactory.getLogger(RemoteSparkTestRunner.class);

	static final File SPARK_TESTS_HDFS_FOLDER = new File("/spark-tests");
	private static final String DETAILS_FILENAME = "DETAILS";
	private static final String STATUS_FILENAME = "STATUS";
	private static final String RESULT_BIN_FILENAME = "RESULT.bin";
	private static final String RESULT_TEXT_FILENAME = "RESULT.txt";

	protected static JavaSparkContext createSparkContext(String appName) {
		SparkConf sparkConf = new SparkConf().setAppName(appName);
		JavaSparkContext jsc = new JavaSparkContext(sparkConf);
		return jsc;
	}

	public static final String SUBMITTED = "SUBMITTED";
	public static final String RUNNING = "RUNNING";
	public static final String FAILED = "FAILED";
	public static final String SUCCESSFUL = "SUCCESSFUL";

	private static final long STATUS_POLL_INTERVAL_MILLIS = 1_000;

	private String testcaseId;
	private Class<? extends RemoteSparkTestBase> testClass;

	private static boolean fatJarAlreadyCopied;

	private int driverCores = 1;
	private String driverMemory = "2G";

	/**
	 * @param testcaseId
	 *            used to create distinct directories for results and input
	 *            files.
	 */
	public RemoteSparkTestRunner(Class<? extends RemoteSparkTestBase> testClass, String testcaseId) {
		this.testClass = testClass;
		if (testcaseId.contains(" ")) {
			throw new IllegalArgumentException(
					"Should not contains spaces to avoid Directory paths with spaces: " + testcaseId);
		}
		this.testcaseId = testcaseId;
	}

	public final void executeWithStatusTracking(TestableSparkJob<? extends Serializable> job) throws Exception {
		boolean successful = false;
		try (JavaSparkContext spark = createSparkContext(job.getApplicationName())) {
			HdfsUtils.deleteInHdfs(getHdfsTestJobFolder());
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
		File binFile = new File(getHdfsTestJobFolder(), RESULT_BIN_FILENAME);
		HdfsUtils.writeObjectToHdfsFile(binFile, result);
	}

	private void writeStatus(String status, String details) {
		try {
			HdfsUtils.writeStringToHdfsFile(getHdfsStatusFile(), status);
			if (details != null) {
				HdfsUtils.writeStringToHdfsFile(getHdfsDetailsFile(), details);
			}
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException("Failed to write test status or details to HDFS", e);
		}
	}

	private File getHdfsStatusFile() {
		return new File(getHdfsTestJobFolder(), STATUS_FILENAME);
	}

	private File getHdfsResultFile() {
		return new File(getHdfsTestJobFolder(), RESULT_BIN_FILENAME);
	}

	private File getHdfsDetailsFile() {
		return new File(getHdfsTestJobFolder(), DETAILS_FILENAME);
	}

	private File getHdfsTestJobFolder() {
		return new File(SPARK_TESTS_HDFS_FOLDER, testClass.getName() + "/" + testcaseId);
	}

	private File getLocalTestJobFolder() {
		return new File("build/spark-tests", testClass.getName());
	}

	private File getLocalResultBinaryFile() {
		return new File(getLocalTestJobFolder(), RESULT_BIN_FILENAME);
	}

	private File getLocalResultTextFile() {
		return new File(getLocalTestJobFolder(), RESULT_TEXT_FILENAME);
	}

	public void executeSparkRemote(String... sparkMainArguments) throws Exception {
		LOGGER.info("HDFS Working Directory: hdfs://hdfs" + getHdfsTestJobFolder().getAbsolutePath());
		LOGGER.info("Deleting old status and result files...");
		ShmackUtils.deleteInHdfs(getHdfsTestJobFolder());
		File hdfsJarFile = syncFatJatToHdfs();

		String hdfsJarFileURL = ShmackUtils.getHdfsURL(hdfsJarFile);

		LOGGER.info("Writing initial Job status to HDFS...");
		ShmackUtils.writeStringToHdfs(getHdfsStatusFile(), SUBMITTED);

		LOGGER.info("Submitting Spark-Job...");
		CommandLine cmdLine = new CommandLine("bash");
		cmdLine.addArgument("submit-spark-job.sh");
		cmdLine.addArgument("-Dspark.mesos.coarse=true");
		cmdLine.addArgument("--driver-cores");
		cmdLine.addArgument(String.valueOf(getDriverCores()));
		cmdLine.addArgument("--driver-memory");
		cmdLine.addArgument(getDriverMemory());
		cmdLine.addArgument("--class");
		cmdLine.addArgument(testClass.getName());
		cmdLine.addArgument(hdfsJarFileURL);
		cmdLine.addArguments(sparkMainArguments);
		ExecuteResult result = ShmackUtils.runOnLocalhost(ExecExceptionHandling.THROW_EXCEPTION_IF_EXIT_CODE_NOT_0,
				cmdLine);
		LOGGER.info(result.getStandardOutput());
	}

	private synchronized File syncFatJatToHdfs() throws ExecuteException, IOException {
		File localJarFile = new File("build/libs/sparkjobs-all-with-tests-1.0-SNAPSHOT.jar");
		File hdfsJarFile = new File(SPARK_TESTS_HDFS_FOLDER, "spark-tests.jar");
		if (fatJarAlreadyCopied) {
			LOGGER.info("SKIPPING copy Fat-JAR to hdfs (already copied before).");
			return hdfsJarFile;
		}
		LOGGER.info("Copying Fat-JAR to hdfs: " + ShmackUtils.getHdfsURL(hdfsJarFile) + "...");
		ShmackUtils.copyToHdfs(localJarFile, hdfsJarFile);
		fatJarAlreadyCopied = true;
		return hdfsJarFile;
	}

	public void waitForSparkFinished() throws Exception {
		String statusText;
		do {
			statusText = ShmackUtils.readStringFromHdfs(getHdfsStatusFile());
			LOGGER.info("Status from HDFS-File:" + statusText);
			Thread.sleep(STATUS_POLL_INTERVAL_MILLIS);
		} while (RUNNING.equals(statusText) || SUBMITTED.equals(statusText));
		if (!SUCCESSFUL.equals(statusText)) {
			statusText = ShmackUtils.readStringFromHdfs(getHdfsDetailsFile());
			throw new RuntimeException("Spark Execution failed. Details: \n" + statusText);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Serializable> T getRemoteResult() throws Exception {
		File localResultFile = getLocalResultBinaryFile();
		LOGGER.info("Copying result to " + localResultFile.getAbsolutePath() + " ...");
		ShmackUtils.copyFromHdfs(getHdfsResultFile(), localResultFile);
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(localResultFile))) {
			Object result = ois.readObject();
			return (T) result;
		}
	}

	public void writeRemoteResultAsStringToFile(Object remoteResult) throws IOException {
		FileUtils.writeStringToFile(getLocalResultTextFile(), String.valueOf(remoteResult), StandardCharsets.UTF_8);
	}

	public int getDriverCores() {
		return driverCores;
	}

	public void setDriverCores(int driverCores) {
		this.driverCores = driverCores;
	}

	public String getDriverMemory() {
		return driverMemory;
	}

	public void setDriverMemory(String driverMemory) {
		this.driverMemory = driverMemory;
	}

}
