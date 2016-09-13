package com.zuehlke.shmack.sparkjobs.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.shmack.sparkjobs.infrastructure.ExecExceptionHandling;
import com.zuehlke.shmack.sparkjobs.infrastructure.ExecuteResult;
import com.zuehlke.shmack.sparkjobs.infrastructure.ShmackUtils;

public class RemoteSparkTestRunner {

	private final static Logger LOGGER = LoggerFactory.getLogger(RemoteSparkTestRunner.class);

	static final File SPARK_TESTS_HDFS_FOLDER = new File("/spark-tests");
	private static final String DETAILS_FILENAME = "DETAILS";
	private static final String STATUS_FILENAME = "STATUS";
	private static final String RESULT_BIN_FILENAME = "RESULT.bin";

	public static final String SUBMITTED = "SUBMITTED";
	public static final String RUNNING = "RUNNING";
	public static final String FAILED = "FAILED";
	public static final String SUCCESSFUL = "SUCCESSFUL";

	private static final long STATUS_POLL_INTERVAL_MILLIS = 1_000;
	
	private static final File HDFS_RESSOURCES_DIRECTORY = new File(RemoteSparkTestRunner.SPARK_TESTS_HDFS_FOLDER,
			"resources");

	private static boolean ressourcesAlreadyInSync = false;

	private String testcaseId;
	private Class<?> testClass;

	private static boolean fatJarAlreadyCopied;

	private int driverCores = 1;
	private String driverMemory = "2G";

	/**
	 * @param testcaseId
	 *            used to create distinct directories for results and input
	 *            files.
	 */
	public RemoteSparkTestRunner(Class<?> testClass, String testcaseId) {
		this.testClass = testClass;
		if (testcaseId.contains(" ")) {
			throw new IllegalArgumentException(
					"Should not contains spaces to avoid Directory paths with spaces: " + testcaseId);
		}
		this.testcaseId = testcaseId;
	}

	static File getHdfsStatusFile(final Class<?> testClass, final String testcaseId) {
		return new File(getHdfsTestJobFolder(testClass, testcaseId), STATUS_FILENAME);
	}

	static File getHdfsResultFile(final Class<?> testClass, final String testcaseId) {
		return new File(getHdfsTestJobFolder(testClass, testcaseId), RESULT_BIN_FILENAME);
	}

	static File getHdfsDetailsFile(final Class<?> testClass, final String testcaseId) {
		return new File(getHdfsTestJobFolder(testClass, testcaseId), DETAILS_FILENAME);
	}

	static File getHdfsTestJobFolder(final Class<?> testclass, final String testCaseId) {
		return new File(SPARK_TESTS_HDFS_FOLDER, testclass.getName() + "/" + testCaseId);
	}

	private File getLocalTestJobFolder() {
		return new File("build/spark-tests", testClass.getName());
	}

	private File getLocalResultBinaryFile() {
		return new File(getLocalTestJobFolder(), RESULT_BIN_FILENAME);
	}

	public void executeSparkRemote(String... sparkMainArguments) throws Exception {
		final File hdfsTestJobFolder = getHdfsTestJobFolder(testClass, testcaseId);
		LOGGER.info("HDFS Working Directory: hdfs://hdfs" + hdfsTestJobFolder.getAbsolutePath());
		LOGGER.info("Deleting old status and result files...");
		ShmackUtils.deleteInHdfs(hdfsTestJobFolder);
		final File hdfsJarFile = syncFatJatToHdfs();

		String hdfsJarFileURL = ShmackUtils.getHdfsURL(hdfsJarFile);

		LOGGER.info("Writing initial Job status to HDFS...");
		ShmackUtils.writeStringToHdfs(getHdfsStatusFile(testClass, testcaseId), SUBMITTED);

		LOGGER.info("Submitting Spark-Job...");
		CommandLine cmdLine = new CommandLine("bash");
		cmdLine.addArgument(ShmackUtils.determineScriptDir() + "submit-spark-job.sh");
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
			statusText = ShmackUtils.readStringFromHdfs(getHdfsStatusFile(testClass, testcaseId));
			LOGGER.info("Status from HDFS-File:" + statusText);
			Thread.sleep(STATUS_POLL_INTERVAL_MILLIS);
		} while (RUNNING.equals(statusText) || SUBMITTED.equals(statusText));
		if (!SUCCESSFUL.equals(statusText)) {
			String errorDetails = ShmackUtils.readStringFromHdfs(getHdfsDetailsFile(testClass, testcaseId));
			throw new RuntimeException("Spark Execution failed. Details: \n" + errorDetails);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Serializable> T getRemoteResult() throws Exception {
		File localResultFile = getLocalResultBinaryFile();
		LOGGER.info("Copying result to " + localResultFile.getAbsolutePath() + " ...");
		ShmackUtils.copyFromHdfs(getHdfsResultFile(testClass, testcaseId), localResultFile);
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(localResultFile))) {
			Object result = ois.readObject();
			return (T) result;
		}
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

	public synchronized void syncTestRessourcesToHdfs() throws ExecuteException, IOException {
		if (ressourcesAlreadyInSync) {
			LOGGER.info("SKIPPING copy test resources to HDFS as already done before.");
			return;
		}
		LOGGER.info("Copying test resources to HDFS: " + ShmackUtils.getHdfsURL( HDFS_RESSOURCES_DIRECTORY)) ;
		ShmackUtils.syncFolderToHdfs(new File("src/test/resources"), HDFS_RESSOURCES_DIRECTORY);
		ressourcesAlreadyInSync = true;
	}

	public static String getHdfsTestRessourcePath(String relativeRessourcePath) {
		return "hdfs://hdfs" + HDFS_RESSOURCES_DIRECTORY.getAbsolutePath() + "/" + relativeRessourcePath;
	}

	static File getResultBinFile(final Class<?> testClass, final String testcaseId) {
		return new File(getHdfsTestJobFolder(testClass, testcaseId), RESULT_BIN_FILENAME);
	}

}
