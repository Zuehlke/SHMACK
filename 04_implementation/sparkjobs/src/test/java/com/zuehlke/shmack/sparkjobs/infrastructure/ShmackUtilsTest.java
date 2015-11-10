package com.zuehlke.shmack.sparkjobs.infrastructure;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class ShmackUtilsTest {

	private final static File LOCAL_SRC_DIR = new File("build/RemoteDataTransferTest/local-src-dir/");
	private final static File LOCAL_TARGET_DIR = new File("build/RemoteDataTransferTest/local-target-dir/");
	private final static File REMOTE_DIR = new File("/tmp/ssh-transfer-test/");
	private static final String FILENAME_1 = "file1.txt";
	private static final String FILENAME_2 = "file2.txt";

	private static void resetTransferDirectories() throws IOException {
		if (LOCAL_SRC_DIR.exists()) {
			FileUtils.forceDelete(LOCAL_SRC_DIR);
		}
		if (LOCAL_TARGET_DIR.exists()) {
			FileUtils.forceDelete(LOCAL_TARGET_DIR);
		}
		FileUtils.forceMkdir(LOCAL_SRC_DIR);
		writeRandomFileContent(FILENAME_1);
		writeRandomFileContent(FILENAME_2);
	}

	private static void writeRandomFileContent(String targetFileName) throws IOException {
		String fileContent = "This is content of " + targetFileName
				+ " .\n It intentionally varies on each test invocation: " + (new Date());
		File targetFile = new File(LOCAL_SRC_DIR, targetFileName);
		FileUtils.writeStringToFile(targetFile, fileContent, StandardCharsets.UTF_8);
		System.out.println("Created source file: " + targetFile.getAbsolutePath());
	}

	@Test
	public void testSyncFolderToMasterAndSlave() throws ExecuteException, IOException {
		resetTransferDirectories();
		ShmackUtils.syncFolderToMasterAndSlave(LOCAL_SRC_DIR, REMOTE_DIR);
		ShmackUtils.syncFolderFromSlave(REMOTE_DIR, LOCAL_TARGET_DIR);
		assertFileContentEquals(LOCAL_SRC_DIR, LOCAL_TARGET_DIR, FILENAME_1);
		assertFileContentEquals(LOCAL_SRC_DIR, LOCAL_TARGET_DIR, FILENAME_2);
	}

	@Test
	public void testSyncFolderToMaster() throws ExecuteException, IOException {
		resetTransferDirectories();
		ShmackUtils.syncFolderToMaster(LOCAL_SRC_DIR, REMOTE_DIR);
		ShmackUtils.syncFolderFromMaster(REMOTE_DIR, LOCAL_TARGET_DIR);
		assertFileContentEquals(LOCAL_SRC_DIR, LOCAL_TARGET_DIR, FILENAME_1);
		assertFileContentEquals(LOCAL_SRC_DIR, LOCAL_TARGET_DIR, FILENAME_2);
	}


	private void assertFileContentEquals(File expectedFilesDir, File actualFilesDir, String filename)
			throws IOException {

		File expectedFile = new File(expectedFilesDir, filename);
		File actualFile = new File(actualFilesDir, filename);

		String expectedContent = FileUtils.readFileToString(expectedFile, StandardCharsets.UTF_8);
		String actualContent = FileUtils.readFileToString(actualFile, StandardCharsets.UTF_8);

		assertEquals("Content of " + actualFile.getAbsolutePath(), expectedContent, actualContent);

	}

}
