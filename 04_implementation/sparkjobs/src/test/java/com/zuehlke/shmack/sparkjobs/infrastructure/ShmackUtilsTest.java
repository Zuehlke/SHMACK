package com.zuehlke.shmack.sparkjobs.infrastructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.zuehlke.shmack.sparkjobs.base.ShmackTestBase;

public class ShmackUtilsTest extends ShmackTestBase {

	private final static File LOCAL_SRC_DIR = new File("build/RemoteDataTransferTest/local-src-dir/");
	private final static File LOCAL_TARGET_DIR = new File("build/RemoteDataTransferTest/local-target-dir/");
	private final static File REMOTE_DIR = new File("/tmp/ssh-transfer-test/");

	private static final int SMALL_NUMBER_OF_FILES = 2;
	private static final int LARGE_NUMBER_OF_FILES = 1000;

	private static void resetTransferDirectories(int numberOfFiles) throws IOException {
		if (LOCAL_SRC_DIR.exists()) {
			FileUtils.forceDelete(LOCAL_SRC_DIR);
		}
		if (LOCAL_TARGET_DIR.exists()) {
			FileUtils.forceDelete(LOCAL_TARGET_DIR);
		}
		FileUtils.forceMkdir(LOCAL_SRC_DIR);
		for (int i = 0; i < numberOfFiles; i++) {
			writeRandomFileContent(getTestFilename(i));
		}
	}

	private static String getTestFilename(int i) {
		return "test-file-" + i + ".txt";
	}

	private static void writeRandomFileContent(String targetFileName) throws IOException {
		String fileContent = "This is content of " + targetFileName
				+ " .\n It intentionally varies on each test invocation: " + (new Date()) + " - " + System.nanoTime();
		File targetFile = new File(LOCAL_SRC_DIR, targetFileName);
		FileUtils.writeStringToFile(targetFile, fileContent, StandardCharsets.UTF_8);
		System.out.println("Created source file: " + targetFile.getAbsolutePath());
	}

	@Test
	public void testSyncFolderMasterAndSlave() throws ExecuteException, IOException {
		resetTransferDirectories(SMALL_NUMBER_OF_FILES);
		ShmackUtils.syncFolderToMasterAndSlave(LOCAL_SRC_DIR, REMOTE_DIR);
		ShmackUtils.syncFolderFromSlave(REMOTE_DIR, LOCAL_TARGET_DIR);
		assertFolderContentEquals(LOCAL_SRC_DIR, LOCAL_TARGET_DIR);
	}

	private void assertFolderContentEquals(File localSrcDir, File localTargetDir) throws IOException {
		File[] localSrcFiles = localSrcDir.listFiles();
		for (File file : localSrcFiles) {
			assertFileContentEquals(localSrcDir, localTargetDir, file.getName());
		}
	}

	@Test
	public void testSyncFolderMaster() throws ExecuteException, IOException {
		resetTransferDirectories(SMALL_NUMBER_OF_FILES);
		ShmackUtils.syncFolderToMaster(LOCAL_SRC_DIR, REMOTE_DIR);
		ShmackUtils.syncFolderFromMaster(REMOTE_DIR, LOCAL_TARGET_DIR);
		assertFolderContentEquals(LOCAL_SRC_DIR, LOCAL_TARGET_DIR);
	}

	private void assertFileContentEquals(File expectedFilesDir, File actualFilesDir, String filename)
			throws IOException {

		File expectedFile = new File(expectedFilesDir, filename);
		File actualFile = new File(actualFilesDir, filename);

		String expectedContent = FileUtils.readFileToString(expectedFile, StandardCharsets.UTF_8);
		String actualContent = FileUtils.readFileToString(actualFile, StandardCharsets.UTF_8);

		assertEquals("Content of " + actualFile.getAbsolutePath(), expectedContent, actualContent);

	}

	@Test
	public void testRunOnMaster() throws ExecuteException, IOException {
		ExecuteResult result = ShmackUtils.runOnMaster("hostname");
		// example hostname: ip-10-0-7-102.us-west-1.compute.internal
		assertTrue("Hostname of aws-node: " + result, result.getStandardOutput().contains("compute.internal"));
	}

	@Test
	public void testSyncFolderHdfs() throws ExecuteException, IOException {
		resetTransferDirectories(SMALL_NUMBER_OF_FILES);
		ShmackUtils.syncFolderToHdfs(LOCAL_SRC_DIR, REMOTE_DIR);
		ShmackUtils.syncFolderFromHdfs(REMOTE_DIR, LOCAL_TARGET_DIR);
		assertFolderContentEquals(LOCAL_SRC_DIR, LOCAL_TARGET_DIR);
	}

	@Test
	public void testDeleteAndCopyFolderHdfs() throws ExecuteException, IOException {
		resetTransferDirectories(SMALL_NUMBER_OF_FILES);
		ShmackUtils.deleteFolderInHdfs(REMOTE_DIR);
		ShmackUtils.copyFolderToHdfs(LOCAL_SRC_DIR, REMOTE_DIR);
		ShmackUtils.syncFolderFromHdfs(REMOTE_DIR, LOCAL_TARGET_DIR);
		assertFolderContentEquals(LOCAL_SRC_DIR, LOCAL_TARGET_DIR);

		assertHdfsFolderNumberOfFiles(REMOTE_DIR, SMALL_NUMBER_OF_FILES);
		ShmackUtils.deleteFolderInHdfs(REMOTE_DIR);
		assertHdfsFolderDoesNotExist(REMOTE_DIR);
	}

	private void assertHdfsFolderNumberOfFiles(File remoteDir, int expectedNumberOfFiles)
			throws ExecuteException, IOException {
		ExecuteResult executeResult = ShmackUtils.runOnMaster("hadoop", "fs", "-ls",
				ShmackUtils.getHdfsPath(remoteDir));
		assertTrue(executeResult.getStandardOutput().startsWith("Found " + expectedNumberOfFiles + " items"));
	}

	private void assertHdfsFolderDoesNotExist(File remoteDir) throws IOException {
		try {
			ShmackUtils.runOnMaster("hadoop", "fs", "-ls", ShmackUtils.getHdfsPath(remoteDir));
			fail("Exception expected here.");
		} catch (ExecuteException e) {
			assertExceptionMessageContains(e, "No such file or directory");
		}
	}

	private void assertExceptionMessageContains(ExecuteException e, String expectedSubstring) {
		if (!e.getMessage().contains(expectedSubstring)) {
			fail("Exception does not contain '" + expectedSubstring + "': " + e.getMessage());
		}
	}

	@Test
	@Ignore("Only intended to be invoked in scenarios for testing failover of Cluster, e.g. when removing number of clients")
	public void testSyncFolderHdfsManyFiles() throws ExecuteException, IOException {
		resetTransferDirectories(LARGE_NUMBER_OF_FILES);
		ShmackUtils.syncFolderToHdfs(LOCAL_SRC_DIR, REMOTE_DIR);
		ShmackUtils.syncFolderFromHdfs(REMOTE_DIR, LOCAL_TARGET_DIR);
		assertFolderContentEquals(LOCAL_SRC_DIR, LOCAL_TARGET_DIR);
	}

	@Test
	public void testSubmitSparkJob() throws ExecuteException, IOException {
		ExecuteResult executeResult = ShmackUtils.runOnLocalhost("bash", "submit-spark-job.sh",
				"-Dspark.mesos.coarse=true", "--driver-cores", "1", "--driver-memory", "1024M",
				"--class", "org.apache.spark.examples.SparkPi",
				"https://downloads.mesosphere.com/spark/assets/spark-examples_2.10-1.4.0-SNAPSHOT.jar", "30");
		assertExecuteResultStandardOutputContains("Run job succeeded. Submission id:", executeResult);
	}

}
