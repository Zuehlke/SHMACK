package com.zuehlke.shmack.sparkjobs.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URISyntaxException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.shmack.sparkjobs.infrastructure.ExecExceptionHandling;
import com.zuehlke.shmack.sparkjobs.infrastructure.ExecuteResult;
import com.zuehlke.shmack.sparkjobs.infrastructure.ShmackUtils;

public class RemoteSparkTestBase extends ShmackTestBase {

	private final static Logger LOGGER = LoggerFactory.getLogger(RemoteSparkTestBase.class);
	
	private static final File SPARK_TESTS_HDFS_FOLDER = new File("/spark-tests");
	private static final File HDFS_RESSOURCES_DIRECTORY = new File(SPARK_TESTS_HDFS_FOLDER, "resources");

	private static final String DETAILS_FILENAME = "DETAILS";
	private static final String STATUS_FILENAME = "STATUS";
	private static final String RESULT_FILENAME = "RESULT";

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

	private static boolean ressourcesAlreadyInSync = false;

	protected final void executeWithStatusTracking(TestableSparkJob<? extends Serializable> job) throws Exception {
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
		File binFile = new File(getHdfsTestJobFolder(), RESULT_FILENAME);
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
		return new File(getHdfsTestJobFolder(), RESULT_FILENAME);
	}

	private File getHdfsDetailsFile() {
		return new File(getHdfsTestJobFolder(), DETAILS_FILENAME);
	}

	private File getHdfsTestJobFolder() {
		return new File(SPARK_TESTS_HDFS_FOLDER, this.getClass().getName());
	}

	private File getLocalTestJobFolder() {
		return new File("build/spark-tests", this.getClass().getName());
	}

	private File getLocalResultFile() {
		return new File(getLocalTestJobFolder(), RESULT_FILENAME);
	}

	protected void syncTestRessourcesToHdfs() throws ExecuteException, IOException {
		if (!ressourcesAlreadyInSync) {
			LOGGER.info("Synchronizing test resources to HDFS: " + HDFS_RESSOURCES_DIRECTORY.getAbsolutePath());
			ShmackUtils.syncFolderToHdfs(new File("src/test/resources"), HDFS_RESSOURCES_DIRECTORY);
			ressourcesAlreadyInSync = true;
		}
	}

	protected static String getHdfsTestRessourcePath(String relativeRessourcePath) {
		return "hdfs://hdfs" + HDFS_RESSOURCES_DIRECTORY.getAbsolutePath() + "/" + relativeRessourcePath;
	}

	/**
	 * <pre>
	 * Based on the following script:
	 cd ~/shmack/repo/04_implementation/sparkjobs
	 gradle clean fatJarWithTests
	 sync-to-hdfs.sh ~/shmack/repo/04_implementation/sparkjobs/build/libs sparkjobs-tests-libs
	 submit-spark-job.sh -Dspark.mesos.coarse=true --driver-cores 1
	    --driver-memory 1024M
	    --class com.zuehlke.shmack.sparkjobs.wordcount.WordCountRemoteTest
	    hdfs://hdfs/sparkjobs-tests-libs/sparkjobs-all-with-tests-1.0-SNAPSHOT.jar  30
	 * </pre>
	 * @param string 
	 */
	protected void executeSparkRemote(String... sparkMainArguments) throws Exception {
		File hdfsTestJobFolder = getHdfsTestJobFolder();
		LOGGER.info("HDFS Working Directory: hdfs://hdfs" + hdfsTestJobFolder.getAbsolutePath());
		LOGGER.info("Deleting old status and result files...");
		ShmackUtils.deleteInHdfs(hdfsTestJobFolder);
		LOGGER.info("Copying Fat-JAR to hdfs...");
		File localJarFile = new File("build/libs/sparkjobs-all-with-tests-1.0-SNAPSHOT.jar");
		File hdfsJarFile = new File(hdfsTestJobFolder, "spark-test-job.jar");
		ShmackUtils.copyToHdfs(localJarFile, hdfsJarFile);

		String hdfsJarFileURL = ShmackUtils.getHdfsURL(hdfsJarFile);

		ShmackUtils.writeStringToHdfs(getHdfsStatusFile(), SUBMITTED);

		LOGGER.info("Submitting Spark-Job...");
		CommandLine cmdLine = new CommandLine("bash");
		cmdLine.addArgument("submit-spark-job.sh");
		cmdLine.addArgument("-Dspark.mesos.coarse=true");
		cmdLine.addArgument("--driver-cores");
		cmdLine.addArgument("1");
		cmdLine.addArgument("--driver-memory");
		cmdLine.addArgument("2G");
		cmdLine.addArgument("--class");
		cmdLine.addArgument(this.getClass().getName());
		cmdLine.addArgument(hdfsJarFileURL);
		cmdLine.addArguments(sparkMainArguments);
		ExecuteResult result = ShmackUtils.runOnLocalhost(ExecExceptionHandling.THROW_EXCEPTION_IF_EXIT_CODE_NOT_0, cmdLine);
		LOGGER.info(result.getStandardOutput());
	}

	protected void waitForSparkFinished() throws Exception {
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
	protected <T extends Serializable> T getRemoteResult() throws Exception {
		File localResultFile = getLocalResultFile();
		LOGGER.info("Copying result to " + localResultFile.getAbsolutePath() + " ...");
		ShmackUtils.copyFromHdfs(getHdfsResultFile(), localResultFile);
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(localResultFile))) {
			Object result = ois.readObject();
			return (T) result;
		}
	}

}
