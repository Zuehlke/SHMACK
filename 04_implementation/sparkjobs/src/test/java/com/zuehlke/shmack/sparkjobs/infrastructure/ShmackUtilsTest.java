package com.zuehlke.shmack.sparkjobs.infrastructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ShmackUtilsTest {

	private final static File LOCAL_SRC_DIR = new File("build/RemoteDataTransferTest/local-src-dir/");
	private final static File LOCAL_TARGET_DIR = new File("build/RemoteDataTransferTest/local-target-dir/");
	private final static File REMOTE_DIR = new File("/tmp/ssh-transfer-test/");

	private static final int SMALL_NUMBER_OF_FILES = 2;

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

}
